/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2021 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.guardedoperators.opguard;

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
    private final OpGuard api;
    
    PluginDisableHijack(OpGuard api)
    {
        this.api = api;
        
        Stream.of(Bukkit.getPluginManager().getPlugins()).forEach(this::exemptFromPlugMan);
    }
    
    @EventHandler
    public void on(PluginDisableEvent event)
    {
        if (event.getPlugin() == api.plugin() && api.config().canShutDownOnDisable())
        {
            Messenger.console("&c[&fOpGuard was disabled&c] Shutting server down.");
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
        
        if (!isPlugMan || !api.config().canExemptSelfFromPlugMan())
        {
            return;
        }
        
        Plugin instance = api.plugin();
        
        Runnable task = () ->
        {
            try
            {
                Field ignoredPluginsField = plugin.getClass().getDeclaredField("ignoredPlugins");
                ignoredPluginsField.setAccessible(true);
                List<String> ignored = (List<String>) ignoredPluginsField.get(plugin);
                
                ignored.add(instance.getName());
                Messenger.console("&f[OpGuard] &9Exempted OpGuard from PlugMan.");
            }
            catch (Exception ignored) {}
        };
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, task, 1L);
    }
}
