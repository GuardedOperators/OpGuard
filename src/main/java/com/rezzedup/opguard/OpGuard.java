package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.metrics.MetricsLite;
import com.rezzedup.opguard.util.Config;
import com.rezzedup.opguard.util.Messenger;
import com.rezzedup.opguard.wrapper.GuardedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class OpGuard extends JavaPlugin
{
    private final DependencyWrapper dependencies = new DependencyWrapper();
    
    @Override
    public void onEnable()
    {
        dependencies.updateDependencies(new GuardedDependencies(this));
        Config.load(this);
        
        VerifiedOperators.addExistingOperators();
        
//        new BukkitRunnable()
//        {
//            @Override
//            public void run()
//            {
//                VerifiedOperators.inspect();
//            }
//        }
//        .runTaskTimer(this, 5L, getConfig().getLong("inspection-interval"));
//        
//        new BukkitRunnable()
//        {
//            @Override
//            public void run()
//            {
//                if (VerifiedOperators.save() || configUpdate)
//                {
//                    saveConfig();
//                    configUpdate = false;
//                }
//            }
//        }
//        .runTaskTimer(this, 5L, getConfig().getLong("save-interval"));
        
        PluginManager plugin = Bukkit.getPluginManager();
        
        if (getConfig().getBoolean("metrics"))
        {
            try 
            {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
            } 
            catch (IOException e) 
            {
                // Failed to submit the stats.
            }
        }
    }
    
    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
    }
    
    private static final class DependencyWrapper
    {
        private GuardedDependencies dependencies;
        
        private void updateDependencies(GuardedDependencies update)
        {
            if (update.getClass().getCanonicalName().equals(GuardedDependencies.class.getCanonicalName()))
            {
                dependencies = update;
            }
            else 
            {
                throw new IllegalStateException("Cannot replace dependencies with foreign object.");
            }
        }
        
        private GuardedDependencies getDependencies()
        {
            return dependencies;
        }
    }
    
    private static final class GuardedDependencies implements OpGuardAPI
    {
        private final OpGuard instance;
        private final GuardLog log;
        private final FileConfiguration config;
        private final ManagementCommand command;
    
        private GuardedDependencies(OpGuard instance) 
        {
            this.instance = instance;
            this.log = new GuardLog(instance, "guard");
            this.config = instance.getConfig();
            this.command = new ManagementCommand(this);
            
            new GuardedPlayer.EventInjector(this);
            new InterceptCommands(this);
            new PluginDisableHijack(this);
        }
    
        @Override
        public Plugin getPlugin()
        {
            return instance;
        }
    
        @Override
        public FileConfiguration getConfig()
        {
            return config;
        }
    
        @Override
        public void registerEvents(Listener listener)
        {
            Bukkit.getPluginManager().registerEvents(listener, instance);
        }
    
        @Override
        public void log(String type, String message)
        {
            log.append(type, message);
        }
    
        @Override
        public void warn(String type, String message)
        {
            if (getConfig().getBoolean("warn." + type))
            {
                Messenger.broadcast(message, "opguard.warn");
            }
        }
    
        @Override
        public ManagementCommand getManagementCommand()
        {
            return command;
        }
    
        @Override
        public void punish(String username)
        {
            String command = getConfig().getString("punish.command");
            command = command.replaceAll("(%player%)", username);
    
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    
            String type = "status";
            String message = "&f[&a&lOKAY&f] Punished `&7" + username + "&f` for attempting to gain op.";
    
            warn(type, message);
            log(type, "Executed punishment command: /" + command);
            log(type, message);
        }
    }
}
