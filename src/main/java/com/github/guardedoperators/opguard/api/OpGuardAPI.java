package com.github.guardedoperators.opguard.api;

import com.github.guardedoperators.opguard.api.config.OpGuardConfig;
import com.github.guardedoperators.opguard.api.message.Loggable;
import com.github.guardedoperators.opguard.api.message.Punishable;
import com.github.guardedoperators.opguard.api.message.Warnable;
import com.github.zafarkhaja.semver.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface OpGuardAPI
{
    Plugin getPlugin();
    
    Version getVersion();
    
    OpGuardConfig getConfig();
    
    Verifier getVerifier();
    
    void reloadConfig();
    
    OpGuardAPI log(Loggable context);
    
    OpGuardAPI log(String message);
    
    OpGuardAPI warn(Warnable context);
    
    OpGuardAPI warn(CommandSender sender, Warnable context);
    
    OpGuardAPI warn(String message);
    
    void run(CommandSender sender, String[] args);
    
    void punish(Punishable context, String username);
}
