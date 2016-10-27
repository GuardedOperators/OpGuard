package com.rezzedup.opguard.config;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.SavableConfig;

public class DataStorage extends BaseConfig implements SavableConfig
{
    private final OpGuardAPI api;
    
    public DataStorage(OpGuardAPI api)
    {
        super(api.getPlugin());
        this.api = api;
    }
    
    @Override
    protected void load()
    {
        if (api.getConfig().contains("verified") || api.getConfig().contains("password.hash"))
        {
            
        }
    }
    
    @Override
    public void save()
    {
        
    }
    
    @Override
    public void save(boolean async)
    {
        
    }
}
