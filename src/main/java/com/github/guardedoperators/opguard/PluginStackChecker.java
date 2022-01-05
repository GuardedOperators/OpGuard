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
package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.config.OpGuardConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;
import java.util.Stack;

@Deprecated(forRemoval = true)
public final class PluginStackChecker
{
    private final StackTraceElement[] stackTrace;
    
    private final Stack<Plugin> allowed = new Stack<>();
    
    private Plugin plugin = null;
    private StackTraceElement element = null;
    
    public PluginStackChecker(OpGuard opguard)
    {
        Objects.requireNonNull(opguard, "opguard");
        this.stackTrace = Thread.currentThread().getStackTrace();
        
        for (StackTraceElement element : stackTrace)
        {
            try
            {
                Plugin plugin = getPluginByClass(Class.forName(element.getClassName()));
                
                if (plugin != null && plugin != opguard.plugin())
                {
                    OpGuardConfig config = opguard.config();
                    
                    if (config.shouldExemptPlugins() && config.getExemptPlugins().contains(plugin.getName()))
                    {
                        allowed.push(plugin);
                    }
                    else
                    {
                        this.plugin = plugin;
                        this.element = element;
                        break;
                    }
                }
            }
            catch (ClassNotFoundException e) { } // Don't do anything: just keep going.
        }
    }
    
    private Plugin getPluginByClass(Class<?> clazz)
    {
        ClassLoader loader = clazz.getClassLoader();
        
        synchronized (Bukkit.getPluginManager())
        {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
            {
                if (plugin.getClass().getClassLoader() == loader)
                {
                    return plugin;
                }
            }
        }
        return null;
    }
    
    public boolean hasFoundPlugin()
    {
        return plugin != null;
    }
    
    public Plugin getPlugin()
    {
        return plugin;
    }
    
    public StackTraceElement[] getStackTrace()
    {
        return stackTrace;
    }
    
    public StackTraceElement getPluginStackElement()
    {
        return element;
    }
    
    public boolean hasAllowedPlugins()
    {
        return !allowed.empty();
    }
    
    public Stack<Plugin> getAllowedPlugins()
    {
        return allowed;
    }
    
    public Plugin getTopAllowedPlugin()
    {
        return allowed.peek();
    }
    
    public File getPluginJar()
    {
        Class clazz = plugin.getClass();
        return new File(clazz.getProtectionDomain().getCodeSource().getLocation().getFile());
    }
    
    private boolean renameJarFile()
    {
        File current = getPluginJar();
        String path = current.toString() + ".opguard-disabled";
        File replace = new File(path);
        int iteration = 0;
        
        while (replace.exists())
        {
            replace = new File(path + iteration++);
        }
        return current.renameTo(replace);
    }
    
    public void disablePlugin(OpGuard api, Context context)
    {
        if (!hasFoundPlugin())
        {
            throw new IllegalStateException("No plugin to disable.");
        }
        
        OpGuardConfig config = api.config();
        String name = plugin.getName();
        
        if (config.getExemptPlugins().contains(name))
        {
            Context exemption = context.copy();
            
            exemption.warning
            (
            "The plugin &7" + name + "&f is defined in the exempt-plugins list, " +
            "but plugin exemptions are currently disabled"
            );
            api.warn(exemption).log(exemption);
        }
        
        String jar = getPluginJar().getName();
        
        if (config.canDisableOtherPlugins())
        {
            Bukkit.getPluginManager().disablePlugin(plugin);
            context.okay("Disabled plugin &7" + name + "&f. Remove it from the server immediately");
            api.warn(context).log(context);
            
            if (config.canRenameOtherPlugins())
            {
                if (renameJarFile())
                {
                    context.okay("Renamed plugin jar &7" + jar + "&f to prevent re-enabling");
                }
                else
                {
                    context.warning("Unable to rename plugin jar &7" + jar);
                }
                api.warn(context).log(context);
            }
        }
    }
}
