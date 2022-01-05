/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2022 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
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
package com.github.guardedoperators.opguard.listeners;

import com.github.guardedoperators.opguard.Messenger;
import com.github.guardedoperators.opguard.OpGuard;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PlugmanExemptListener implements Listener
{
    private final OpGuard opguard;
    
    public PlugmanExemptListener(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
        Stream.of(Bukkit.getPluginManager().getPlugins()).forEach(this::exemptFromPlugMan);
    }
    
    @EventHandler
    public void onPluginLoad(PluginEnableEvent event)
    {
        exemptFromPlugMan(event.getPlugin());
    }
    
    @SuppressWarnings("unchecked")
    private void exemptFromPlugMan(Plugin plugin)
    {
        if (!plugin.getName().equalsIgnoreCase("PlugMan")) { return; }
        if (!opguard.config().canExemptSelfFromPlugMan()) { return; }
        
        Runnable task = () ->
        {
            try
            {
                Field ignoredPluginsField = plugin.getClass().getDeclaredField("ignoredPlugins");
                ignoredPluginsField.setAccessible(true);
                List<String> ignored = (List<String>) ignoredPluginsField.get(plugin);
                
                String name = opguard.plugin().getName();
                
                if (!ignored.contains(name))
                {
                    ignored.add(name);
                    Messenger.console("&f[OpGuard] &9Exempted " + name + " from PlugMan.");
                }
            }
            catch (Exception ignored) {}
        };
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(opguard.plugin(), task, 1L);
    }
}
