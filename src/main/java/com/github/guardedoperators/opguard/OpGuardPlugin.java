package com.github.guardedoperators.opguard;

import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpGuardPlugin extends JavaPlugin implements Listener
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
        
        OpGuard api = new OpGuard(this);
        
        new VerifyOpListTask(api);
        new UpdateCheckTask(api);
        
        if (api.config().metricsAreEnabled())
        {
            new Metrics(this, BSTATS);
        }
    }
}
