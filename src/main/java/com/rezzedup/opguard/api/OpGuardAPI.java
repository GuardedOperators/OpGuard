package com.rezzedup.opguard.api;

import com.rezzedup.opguard.OpGuardCommand;
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
    
    public void log(String type, String message);
    
    public void warn(String type, String message);
    
    public void run(CommandSender sender, String[] args);
    
    public void punish(String username);
}
