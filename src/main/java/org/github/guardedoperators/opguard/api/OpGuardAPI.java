package org.github.guardedoperators.opguard.api;

import org.github.guardedoperators.opguard.api.config.OpGuardConfig;
import org.github.guardedoperators.opguard.api.message.Loggable;
import org.github.guardedoperators.opguard.api.message.Punishable;
import org.github.guardedoperators.opguard.api.message.Warnable;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface OpGuardAPI
{
    Plugin getPlugin();
    
    Version getVersion();
    
    OpGuardConfig getConfig();
    
    Verifier getVerifier();
    
    void reloadConfig();
    
    void registerEvents(Listener listener);
    
    OpGuardAPI log(Loggable context);
    
    OpGuardAPI log(String message);
    
    OpGuardAPI warn(Warnable context);
    
    OpGuardAPI warn(CommandSender sender, Warnable context);
    
    OpGuardAPI warn(String message);
    
    void run(CommandSender sender, String[] args);
    
    void punish(Punishable context, String username);
}
