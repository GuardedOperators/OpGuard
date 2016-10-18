package com.rezzedup.opguard;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Password
{
    private String hash;
    
    public Password(String plaintext)
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
    
    public String getHash()
    {
        return this.hash;
    }
}
