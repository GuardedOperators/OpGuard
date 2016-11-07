package com.rezzedup.opguard.api.message;

public interface ChainableMessage
{
    boolean hasMessage();
    
    ChainableMessage setMessage(String message);
    
    String getMessage();
}
