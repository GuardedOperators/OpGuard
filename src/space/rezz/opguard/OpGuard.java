package space.rezz.opguard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import space.rezz.opguard.util.Config;
import space.rezz.opguard.util.Messenger;

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
        
        PluginManager plugin = Bukkit.getPluginManager();
        
        plugin.registerEvents(new PluginDisableHijack(), this);
        plugin.registerEvents(new InterceptCommands(), this);
    }
    
    @Override
    public void onDisable()
    {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
