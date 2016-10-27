package com.rezzedup.opguard.api;

public interface Savable
{
    public boolean save();
    
    public boolean save(boolean async);
}
