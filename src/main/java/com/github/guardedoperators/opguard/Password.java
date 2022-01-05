/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2022 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.guardedoperators.opguard;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class Password
{
    public static final Password NO_PASSWORD = new NoPassword();
    
    public enum Algorithm
    {
        NONE(hash -> NO_PASSWORD, UnaryOperator.identity()),
        SHA_256(Sha256Password::new, Sha256Password::legacySha256Hash),
        BCRYPT(BcryptPassword::new, BcryptPassword::bcryptHash);
        
        private final Function<String, Password> constructor;
        private final UnaryOperator<String> hash;
        
        Algorithm(Function<String, Password> constructor, UnaryOperator<String> hash)
        {
            this.constructor = constructor;
            this.hash = hash;
        }
        
        public Password passwordFromHash(String hash) { return constructor.apply(hash); }
        
        public Password passwordFromPlainText(String plainText) { return passwordFromHash(hash.apply(plainText)); }
    }
    
    private final Algorithm algorithm;
    private final String hash;
    
    private Password(Algorithm algorithm, String hash)
    {
        this.algorithm = algorithm;
        this.hash = hash;
    }
    
    public Algorithm algorithm() { return algorithm; }
    
    public String hash() { return this.hash; }
    
    public abstract boolean equalsPlainText(String plainText);
    
    private static class NoPassword extends Password
    {
        private NoPassword() { super(Algorithm.NONE, ""); }
        
        @Override
        public boolean equalsPlainText(String plainText) { return plainText.isEmpty(); }
    }
    
    private static class Sha256Password extends Password
    {
        private static String legacySha256Hash(String plainText)
        {
            // ;v( very bad no good
            String pass = plainText + " :^) Enjoy!";
            
            try
            {
                byte[] hash = MessageDigest.getInstance("SHA-256").digest(pass.getBytes(StandardCharsets.UTF_8));
                StringBuilder hashed = new StringBuilder();
                for (byte b : hash) { hashed.append(String.format("%02X", b)); }
                return hashed.toString().toLowerCase();
            }
            catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
        }
        
        private Sha256Password(String hash)
        {
            super(Algorithm.SHA_256, hash);
        }
        
        @Override
        public boolean equalsPlainText(String plainText)
        {
            return hash().equals(legacySha256Hash(plainText));
        }
    }
    
    private static class BcryptPassword extends Password
    {
        private static String bcryptHash(String plainText)
        {
            return BCrypt.withDefaults().hashToString(12, plainText.toCharArray());
        }
        
        private BcryptPassword(String hash)
        {
            super(Algorithm.BCRYPT, hash);
        }
        
        @Override
        public boolean equalsPlainText(String plainText)
        {
            return BCrypt.verifyer().verify(plainText.toCharArray(), hash()).verified;
        }
    }
}
