package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.api.ExecutableCommand;
import com.github.guardedoperators.opguard.api.OpGuardAPI;
import com.github.guardedoperators.opguard.api.Verifier;
import com.github.guardedoperators.opguard.api.Version;
import com.github.guardedoperators.opguard.api.config.OpGuardConfig;
import com.github.guardedoperators.opguard.api.message.Loggable;
import com.github.guardedoperators.opguard.api.message.Punishable;
import com.github.guardedoperators.opguard.api.message.Warnable;
import com.github.guardedoperators.opguard.config.DataStorage;
import com.github.guardedoperators.opguard.config.MigratableConfig;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpGuard extends JavaPlugin implements Listener
{
    // https://bstats.org/plugin/bukkit/OpGuard/540
    public static final int BSTATS = 540;
    
    @Override
    public void onEnable()
    {
        if (getDataFolder().mkdir())
        {
            getLogger().info("Created directory: " + getDataFolder().getPath());
        }
        
        OpGuardAPI api = new GuardedDependencies(this);
        
        new VerifyOpListTask(api);
        new UpdateCheckTask(api);
        
        if (api.getConfig().metricsAreEnabled())
        {
            new Metrics(this, BSTATS);
        }
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
