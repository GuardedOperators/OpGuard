package com.rezzedup.opguard.config;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.SavableConfig;

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
        /*if (api.getConfig().contains("verified") || api.getConfig().contains("password.hash"))
        {
            
        }*/
    }
    
    @Override
    public boolean save()
    {
        return true;
    }
    
    @Override
    public boolean save(boolean async)
    {
        return true;
    }
}
