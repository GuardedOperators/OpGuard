package com.rezzedup.opguard.api;

public interface SavableConfig extends BasicConfig
{
    public void save();
    
    public void save(boolean async);
}
