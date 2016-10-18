package com.rezzedup.opguard;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class GuardLog extends Log
{
    private final FileConfiguration config;
    
    public GuardLog(Plugin plugin, String name)
    {
        super(plugin, name);
        this.config = plugin.getConfig();
    }
    
    public void append(String type, String message)
    {
        if (config.getBoolean("log.enabled") && config.getBoolean("log." + type))
        {
            append(message);
        }
    }
}
