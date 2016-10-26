package com.rezzedup.opguard.api;

public interface SavableConfig extends Config
{
    public void save();
    
    public void save(boolean async);
}
