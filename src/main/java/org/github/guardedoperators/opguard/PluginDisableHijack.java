package org.github.guardedoperators.opguard;

import org.github.guardedoperators.opguard.api.OpGuardAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

final class PluginDisableHijack implements Listener
{    
    private final OpGuardAPI api;
    
    PluginDisableHijack(OpGuardAPI api)
    {
        this.api = api;
        api.registerEvents(this);
        
        Stream.of(Bukkit.getPluginManager().getPlugins()).forEach(this::exemptFromPlugMan);
    }
    
    @EventHandler
    public void on(PluginDisableEvent event)
    {
        if (event.getPlugin() == api.getPlugin() && api.getConfig().canShutDownOnDisable())
        {
            Messenger.send("&c[&fOpGuard was disabled&c] Shutting server down.");
            Bukkit.shutdown();
        }
    }
    
    @EventHandler
    public void on(PluginEnableEvent event)
    {
        exemptFromPlugMan(event.getPlugin());
    }
    
    @SuppressWarnings("unchecked")
    private void exemptFromPlugMan(Plugin plugin)
    {
        boolean isPlugMan = plugin != null && plugin.getName().equalsIgnoreCase("PlugMan");
        
        if (!isPlugMan || !api.getConfig().canExemptSelfFromPlugMan())
        {
            return;
        }
        
        Plugin instance = api.getPlugin();
        
        Runnable task = () ->
        {
            try
            {
                Field ignoredPluginsField = plugin.getClass().getDeclaredField("ignoredPlugins");
                ignoredPluginsField.setAccessible(true);
                List<String> ignored = (List<String>) ignoredPluginsField.get(plugin);
                
                ignored.add(instance.getName());
                Messenger.send("&f[OpGuard] &9Exempted OpGuard from PlugMan.");
            }
            catch (Exception ignored) {}
        };
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, task, 1L);
    }
}
