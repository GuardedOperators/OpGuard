package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.api.Password;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class OpPassword implements Password
{
    private String hash;
    
    public OpPassword(Password clone)
    {
        this.hash = clone.getHash();
    }
    
    public OpPassword(String plaintext)
    {
        this(plaintext, false);
    }
    
    OpPassword(String input, boolean isHash)
    {
        if (isHash)
        {
            this.hash = input;
            return;
        }
        
        String pass = input + " :^) Enjoy!";
        
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashed = new StringBuilder();
            
            for (byte b : hash)
            {
                hashed.append(String.format("%02X", b));
            }
            this.hash = hashed.toString().toLowerCase();
        }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
    }
    
    @Override
    public String getHash()
    {
        return this.hash;
    }
}
