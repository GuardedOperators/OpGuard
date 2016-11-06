package com.rezzedup.opguard.api;

public interface Savable
{
    boolean save();
    
    boolean save(boolean async);
}
