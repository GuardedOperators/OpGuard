package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Config;
import com.rezzedup.opguard.api.ExecutableCommand;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.config.MigratableConfig;
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
    
    private static final class GuardedDependencies implements OpGuardAPI
    {
        private final OpGuard instance;
        private final Config config;
        private final Log log;
        private final ExecutableCommand command;
        private final Verifier verifier;
    
        private GuardedDependencies(OpGuard instance) 
        {
            this.instance = instance;
            this.config = new MigratableConfig(instance);
            this.log = new Log(instance, "guard");
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
        public void log(Context context)
        {
            if (context.hasMessage() && context.isLoggable())
            {
                log(context.getMessage());
            }
        }
    
        @Override
        public void log(String message)
        {
            if (getConfig().getBoolean("enable-logging"))
            {
                log.append(message);
            }
        }
        
        @Override
        public void warn(Context context)
        {
            if (context.hasMessage() && context.isWarnable())
            {
                warn(context.getMessage());
            }
        }
        
        @Override
        public void warn(String message)
        {
            Messenger.broadcast(message, "opguard.warn");
        }
    
        @Override
        public void run(CommandSender sender, String[] args)
        {
            command.execute(sender, args);
        }
    
        @Override
        public void punish(Context context, String username)
        {
            if (!context.isPunishable())
            {
                return;
            }
            
            context = context.copy().punish();
            
            FileConfiguration config = getConfig();
            
            for (String command : config.getStringList("punishment-commands"))
            {
                command = command.replaceAll("(%player%)", username);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
    
            context.setMessage("Punished `&7" + username + "&f` for attempting to gain op.").warning();
    
            warn(context);
            log(context);
        }
    }
}
