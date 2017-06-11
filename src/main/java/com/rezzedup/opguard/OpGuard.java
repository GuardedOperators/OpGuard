package com.rezzedup.opguard;

import com.rezzedup.opguard.api.ExecutableCommand;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.api.Version;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import com.rezzedup.opguard.api.message.Loggable;
import com.rezzedup.opguard.api.message.Punishable;
import com.rezzedup.opguard.api.message.Warnable;
import com.rezzedup.opguard.config.DataStorage;
import com.rezzedup.opguard.config.MigratableConfig;
import com.rezzedup.opguard.metrics.BStatsMetricsLite;
import com.rezzedup.opguard.metrics.MCStatsMetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class OpGuard extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        Version java = Version.of(System.getProperty("java.specification.version"));
        
        if (!java.isAtLeast(1, 8))
        {
            Messenger.send("[OpGuard] &cOpGuard requires Java 8, but this server currently runs Java " + java.getMinor());
            
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    OpGuard instance = OpGuard.this;
                    instance.getServer().getPluginManager().disablePlugin(instance);
                }
            }
            .runTask(this);
            
            return;
        }
        
        getDataFolder().mkdir();
        
        OpGuardAPI api = new GuardedDependencies(this);
        
        if (api.getConfig().metricsAreEnabled())
        {
            try 
            {
                new MCStatsMetricsLite(this).start();
            } 
            catch (Exception ignored) {}
            
            new BStatsMetricsLite(this);
        }
        
        new VerifyOpListTask(api);
        new UpdateCheckTask(api);
    }
    
    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
    }
    
    private static final class GuardedDependencies implements OpGuardAPI
    {
        private final OpGuard instance;
        private final Version version;
        private final Log log;
        private final OpGuardConfig config;
        private final Verifier verifier;
        private final ExecutableCommand command;
    
        private GuardedDependencies(OpGuard instance) 
        {
            this.instance = instance;
            this.version = Version.of(instance.getDescription().getVersion());
            this.log = new Log(instance, "guard");
            this.config = new MigratableConfig(instance);
            this.verifier = new OpVerifier(new DataStorage(this));
            this.command = new OpGuardCommand(this);
            
            new CommandInterceptor(this);
            new PluginDisableHijack(this);
            new PermissionChecker(this);
        }
    
        @Override
        public Plugin getPlugin()
        {
            return instance;
        }
    
        @Override
        public Version getVersion()
        {
            return version;
        }
    
        @Override
        public OpGuardConfig getConfig()
        {
            return config;
        }
    
        @Override
        public Verifier getVerifier()
        {
            return verifier;
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
        public OpGuardAPI log(Loggable context)
        {
            if (context.hasMessage() && context.isLoggable())
            {
                log(context.getMessage());
            }
            return this;
        }
    
        @Override
        public OpGuardAPI log(String message)
        {
            if (config.loggingIsEnabled())
            {
                log.append(message);
            }
            return this;
        }
        
        @Override
        public OpGuardAPI warn(Warnable context)
        {
            if (context.hasMessage() && context.isWarnable())
            {
                warn(context.getMessage());
            }
            return this;
        }
    
        @Override
        public OpGuardAPI warn(CommandSender sender, Warnable context)
        {
            if (context.hasMessage() && context.isWarnable())
            {
                Messenger.send(sender, context.getMessage());
            }
            return this;
        }
    
        @Override
        public OpGuardAPI warn(String message)
        {
            Messenger.broadcast(message, "opguard.warn");
            return this;
        }
    
        @Override
        public void run(CommandSender sender, String[] args)
        {
            command.execute(sender, args);
        }
    
        @Override
        public void punish(Punishable punishable, String username)
        {
            if (!punishable.isPunishable() || !(punishable instanceof Context))
            {
                return;
            }
            
            Context context = ((Context) punishable).copy().punish(); 
            
            for (String command : config.getPunishmentCommands())
            {
                command = command.replaceAll("(%player%)", username);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
    
            context.okay("Punished &7" + username + "&f for attempting to gain op");
            warn(context).log(context);
        }
    }
}
