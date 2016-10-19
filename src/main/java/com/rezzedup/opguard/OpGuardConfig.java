package com.rezzedup.opguard;

import org.bukkit.plugin.Plugin;

public class OpGuardConfig extends Config
{
    private Plugin plugin;
    
    public OpGuardConfig(Plugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
    }
    
    @Override
    protected void load()
    {
        config.options().copyDefaults(true);
        
        /*
         * 
         *  OPTIONS
         * 
         */
    
        config.addDefault("inspection-interval", 4L);
        config.addDefault("save-interval", 1200L);
        config.addDefault("only-op-if-online", true);
        config.addDefault("shutdown-on-disable", false);
        config.addDefault("metrics", true);
        
        /*
         * 
         *  LOGGING
         * 
         */
    
        config.addDefault("log.enabled", true);
        config.addDefault("log.status", true);
        config.addDefault("log.plugin-attempt", true);
        config.addDefault("log.console-attempt", true);
        config.addDefault("log.player-attempt", true);
        
        /*
         * 
         *  WARNINGS
         * 
         */
    
        config.addDefault("warn.status", true);
        config.addDefault("warn.plugin-attempt", true);
        config.addDefault("warn.console-attempt", true);
        config.addDefault("warn.player-attempt", true);
        config.addDefault("warn.security-risk", true);
        
        /*
         * 
         *  PUNISHMENTS
         * 
         */
    
        config.addDefault("punish.plugin-attempt", true);
        config.addDefault("punish.console-attempt", true);
        config.addDefault("punish.player-attempt", false);
        config.addDefault("punish.command", "ban %player% Attempting to gain op");
        
        /*
         * 
         *  MANAGEMENT
         * 
         */
    
        config.addDefault("manage.password-in-game", true);
        
        /*
         *
         *  UNSAFE
         * 
         */
        
        config.addDefault("unsafe.info", "---\nDo not modify this section manually!\nYou may lose data in future updates.\n---");
        config.addDefault("unsafe.version", plugin.getDescription().getVersion());
        
        save();
    }
    
    @Override
    public void save()
    {
        plugin.saveConfig();
    }
}
