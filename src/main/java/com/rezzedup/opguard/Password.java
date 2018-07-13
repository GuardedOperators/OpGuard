package com.rezzedup.opguard;

import org.mindrot.jbcrypt.BCrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Password
{
    @Deprecated
    static final String DEPRECATED_GLOBAL_SALT = " :^) Enjoy!";
    
    private interface Algorithm
    {
        String generateSalt();
        
        String hash(String plaintext, String salt);
        
        boolean check(String plaintext, String hash);
    }
    
    @Deprecated
    public static final Algorithm DEPRECATED = new Algorithm() 
    {
        @Override
        public String generateSalt()
        {
            return DEPRECATED_GLOBAL_SALT;
        }
    
        @Override
        public String hash(String plaintext, String salt)
        {
            String pass = plaintext + salt;
    
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
        }
    
        @Override
        public boolean check(String plaintext, String hash)
        {
            return hash(plaintext, DEPRECATED_GLOBAL_SALT).equalsIgnoreCase(hash);
        }
    };
    
    public static final Algorithm BCRYPT = new Algorithm() 
    {
        @Override
        public String generateSalt()
        {
            return BCrypt.gensalt();
        }
    
        @Override
        public String hash(String plaintext, String salt)
        {
            return BCrypt.hashpw(plaintext, salt);
        }
    
        @Override
        public boolean check(String plaintext, String hash)
        {
            return BCrypt.checkpw(plaintext, hash);
        }
    };
    
    public static Password fromExistingDeprecatedHash(String hashedSource)
    {
        return new Password(DEPRECATED, hashedSource);
    }
    
    public static Password fromExistingHash(String hashedSource)
    {
        return new Password(BCRYPT, hashedSource);
    }
    
    public static Password create(String plaintext)
    {
        return new Password(BCRYPT, BCRYPT.hash(plaintext, BCRYPT.generateSalt()));
    }
    
    private final Algorithm algorithm;
    private final String hash;
    
    private Password(Algorithm algorithm, String hash)
    {
        this.algorithm = algorithm;
        this.hash = hash;
    }
    
    public Algorithm getAlgorithm()
    {
        return this.algorithm;
    }
    
    public String getHash()
    {
        return this.hash;
    }
    
    public boolean isDeprecated()
    {
        return this.algorithm == DEPRECATED;
    }
    
    public boolean check(String plaintext)
    {
        return algorithm.check(plaintext, hash);
    }
}
