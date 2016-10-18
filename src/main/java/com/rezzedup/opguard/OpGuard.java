package com.rezzedup.opguard;

import com.rezzedup.opguard.metrics.MetricsLite;
import com.rezzedup.opguard.util.Config;
import com.rezzedup.opguard.util.Messenger;
import com.rezzedup.opguard.wrapper.GuardedPlayer;
import com.rezzedup.opguard.wrapper.GuardedServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.Field;

public class OpGuard extends JavaPlugin
{
    private static OpGuard instance;
    private static Log log;
    private static boolean configUpdate = false;
    
    static OpGuard getInstance()
    {
        return instance;
    }
    
    public static void log(String type, String message)
    {
        log.append(type, message);
    }
    
    public static void warn(String type, String message)
    {
        if (getInstance().getConfig().getBoolean("warn." + type))
        {
            Messenger.broadcast(message, "opguard.warn");
        }
    }
    
    public static void updatedConfig()
    {
        configUpdate = true;
    }
    
    @Override
    public void onEnable()
    {
        instance = this;
        log = new Log(this, "guard");
        Config.load(this);
        
        injectServer();
        
        VerifiedOperators.addExistingOperators();
        
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                VerifiedOperators.inspect();
            }
        }
        .runTaskTimer(this, 5L, getConfig().getLong("inspection-interval"));
        
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (VerifiedOperators.save() || configUpdate)
                {
                    saveConfig();
                    configUpdate = false;
                }
            }
        }
        .runTaskTimer(this, 5L, getConfig().getLong("save-interval"));
    
        new AbstractEventRegistrar(this).registerAbstractListener(new GuardedPlayer.EventInjector());
        
        PluginManager plugin = Bukkit.getPluginManager();
    
        plugin.registerEvents(new PluginDisableHijack(), this);
        plugin.registerEvents(new InterceptCommands(), this);
    
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
    
    private void injectServer()
    {
        try
        {
            GuardedServer guarded = new GuardedServer(Bukkit.getServer());
    
            Field server = Bukkit.class.getDeclaredField("server");
            server.setAccessible(true);
            
            server.set(Bukkit.class, guarded);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
