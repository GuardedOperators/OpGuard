package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

final class PluginDisableHijack implements Listener
{    
    private final OpGuardAPI api;
    
    public PluginDisableHijack(OpGuardAPI api)
    {
        this.api = api;
        api.registerEvents(this);
    }
    
    @EventHandler
    public void onDisable(PluginDisableEvent event)
    {
        if (event.getPlugin().equals(api.getPlugin()))
        {
            if (api.getConfig().canShutDownOnDisable())
            {
                Messenger.send("&c[&fOpGuard was disabled&c] Shutting server down.");
                Bukkit.shutdown();
            }
        }
    }
}
