package com.rezzedup.opguard;

import org.mindrot.jbcrypt.BCrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Password
{
    @Deprecated
    static final String DEPRECATED_GLOBAL_SALT = " :^) Enjoy!";
    
    public interface Hasher
    {
        String hash(String input, String salt);
    }
    
    @Deprecated
    static Hasher DEPRECATED = (input, salt) ->
    {
        String pass = input + salt;
    
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes("UTF-8"));
            StringBuilder hashed = new StringBuilder();
        
            for (byte b : hash)
            {
                hashed.append(String.format("%02X", b));
            }
            
            return hashed.toString().toLowerCase();
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    };
    
    static Hasher BCRYPT = BCrypt::hashpw;
    
    public static Password fromDeprecated(String hashedSource)
    {
        return new Password(DEPRECATED, hashedSource, DEPRECATED_GLOBAL_SALT);
    }
    
    public static Password from(String hashedSource, String salt)
    {
        return new Password(BCRYPT, hashedSource, salt);
    }
    
    public static Password create(String plaintext)
    {
        String salt = BCrypt.gensalt();
        return new Password(BCRYPT, BCRYPT.hash(plaintext, salt), salt);
    }
    
    private final Hasher algorithm;
    private final String hash;
    private final String salt;
    private final boolean isDeprecated;
    
    private Password(Hasher algorithm, String hash, String salt)
    {
        this.algorithm = algorithm;
        this.hash = hash;
        this.salt = salt;
        this.isDeprecated = algorithm == DEPRECATED;
    }
    
    public Hasher getAlgorithm()
    {
        return this.algorithm;
    }
    
    public String getHash()
    {
        return this.hash;
    }
    
    public String getSalt()
    {
        return this.salt;
    }
    
    public boolean isDeprecated()
    {
        return this.isDeprecated;
    }
    
    public boolean check(String plaintext)
    {
        return algorithm.hash(plaintext, salt).equals(hash);
    }
}
