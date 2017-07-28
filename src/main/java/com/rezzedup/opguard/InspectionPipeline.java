package com.rezzedup.opguard;

import java.security.SecureRandom;

public class InspectionPipeline
{
    private static final String RANDOM_ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789_-";
    
    private static String generateRandomString(int minimumLength, int maximumLength)
    {
        SecureRandom random = new SecureRandom();
        StringBuilder string = new StringBuilder();
        
        int length = minimumLength + random.nextInt(maximumLength - minimumLength);
        
        while (string.length() <= length) 
        { 
            string.append(RANDOM_ALPHABET.charAt(random.nextInt(RANDOM_ALPHABET.length()))); 
        }
        
        return string.toString();
    }
    
    private final String arbitraryPermissionNode = generateRandomString(45, 55);
    
    public InspectionPipeline()
    {
        
    }
}
