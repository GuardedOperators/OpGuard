package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Password;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OpPassword implements Password
{
    private String hash;
    
    public OpPassword(Password clone)
    {
        this.hash = clone.getHash();
    }
    
    public OpPassword(String plaintext)
    {
        String pass = plaintext + " :^) Enjoy!";
        
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes("UTF-8"));
            StringBuilder hashed = new StringBuilder();
            
            for (byte b : hash) 
            {
                hashed.append(String.format("%02X", b));
            }
            this.hash = hashed.toString().toLowerCase();
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public String getHash()
    {
        return this.hash;
    }
    
    @Override
    public boolean compare(Password password)
    {
        return hash.equalsIgnoreCase(password.getHash());
    }
}
