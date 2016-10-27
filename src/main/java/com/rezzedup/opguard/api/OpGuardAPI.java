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
    
    public void log(Context context);
    
    public void log(String message);
    
    public void warn(Context context);
    
    public void warn(String message);
    
    public void run(CommandSender sender, String[] args);
    
    public void punish(Context context, String username);
}
