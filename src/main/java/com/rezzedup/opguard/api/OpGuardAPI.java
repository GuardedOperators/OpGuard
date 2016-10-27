package com.rezzedup.opguard.api;

import com.rezzedup.opguard.Context;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface OpGuardAPI
{
    public Plugin getPlugin();
    
    public FileConfiguration getConfig();
    
    public void reloadConfig();
    
    public void registerEvents(Listener listener);
    
    public OpGuardAPI log(Context context);
    
    public OpGuardAPI log(String message);
    
    public OpGuardAPI warn(Context context);
    
    public OpGuardAPI warn(String message);
    
    public void run(CommandSender sender, String[] args);
    
    public void punish(Context context, String username);
}
