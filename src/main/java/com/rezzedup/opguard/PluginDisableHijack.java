package com.rezzedup.opguard;

import com.rezzedup.opguard.util.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class PluginDisableHijack implements Listener
{    
    @EventHandler
    public void onDisable(PluginDisableEvent event)
    {
        if (event.getPlugin().equals(OpGuard.getInstance()))
        {
            if (OpGuard.getInstance().getConfig().getBoolean("shutdown-on-disable"))
            {
                Messenger.sendConsole("&c[&fOpGuard was disabled&c] Shutting server down.");
                Bukkit.shutdown();
            }
        }
    }
}
