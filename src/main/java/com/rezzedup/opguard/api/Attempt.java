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
    
    public static boolean isValid(String value)
    {
        return enumOf(value) != null;
    }
    
    public static Attempt enumOf(String value)
    {
        for (Attempt type : values())
        {
            if (type.toString().equalsIgnoreCase(value))
            {
                return type;
            }
        }
        switch (value.toLowerCase())
        {
            case "player":
                return PLAYER;
            case "console":
                return CONSOLE;
            case "plugin":
                return PLUGIN;
        }
        return null;
    }
}
