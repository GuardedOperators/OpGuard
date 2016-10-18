package com.rezzedup.opguard.api;

import com.rezzedup.opguard.ManagementCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface OpGuardAPI
{
    public Plugin getPlugin();
    
    public FileConfiguration getConfig();
    
    public void registerEvents(Listener listener);
    
    public void log(String type, String message);
    
    public void warn(String type, String message);
    
    public ManagementCommand getManagementCommand();
    
    public void punish(String username);
}
