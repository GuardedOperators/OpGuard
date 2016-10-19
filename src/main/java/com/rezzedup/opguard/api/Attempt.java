package com.rezzedup.opguard.api;

public enum Attempt
{
    PLAYER("player-attempt"),
    CONSOLE("console-attempt"),
    PLUGIN("plugin-attempt");
    
    private final String key;
    
    Attempt(String key)
    {
        this.key = key;
    }
    
    @Override
    public String toString()
    {
        return key;
    }
    
    public String getKey()
    {
        return key;
    }
}
