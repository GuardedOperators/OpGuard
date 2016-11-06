package com.rezzedup.opguard.api;

import com.rezzedup.opguard.Context;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface OpGuardAPI
{
    Plugin getPlugin();
    
    OpGuardConfig getConfig();
    
    Verifier getVerifier();
    
    void reloadConfig();
    
    void registerEvents(Listener listener);
    
    OpGuardAPI log(Context context);
    
    OpGuardAPI log(String message);
    
    OpGuardAPI warn(Context context);
    
    OpGuardAPI warn(String message);
    
    void run(CommandSender sender, String[] args);
    
    void punish(Context context, String username);
}
