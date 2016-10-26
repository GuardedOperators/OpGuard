package com.rezzedup.opguard;

import com.rezzedup.opguard.api.ExecutableCommand;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.config.Config;
import com.rezzedup.opguard.config.OpGuardConfig;
import com.rezzedup.opguard.metrics.MetricsLite;
import com.rezzedup.opguard.wrapper.GuardedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class OpGuard extends JavaPlugin
{
    //private final DependencyWrapper api = new DependencyWrapper();
    
    @Override
    public void onEnable()
    {
        new GuardedDependencies(this);
        
        // TODO: Schedule runnables
        
        if (getConfig().getBoolean("metrics"))
        {
            try 
            {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
            } 
            catch (IOException e) {}
        }
    }
    
    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
    }
    
    /*private static final class DependencyWrapper
    {
        private GuardedDependencies dependencies;
    }*/
    
    private static final class GuardedDependencies implements OpGuardAPI
    {
        private final OpGuard instance;
        private final GuardLog log;
        private final Config config;
        private final ExecutableCommand command;
        private final Verifier verifier;
    
        private GuardedDependencies(OpGuard instance) 
        {
            this.instance = instance;
            this.log = new GuardLog(instance, "guard");
            this.config = new OpGuardConfig(instance);
            this.command = new OpGuardCommand(this);
            this.verifier = new OpVerifier();
            
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
            return config.get();
        }
    
        @Override
        public void reloadConfig()
        {
            config.reload();
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
        public void run(CommandSender sender, String[] args)
        {
            command.execute(sender, args);
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
