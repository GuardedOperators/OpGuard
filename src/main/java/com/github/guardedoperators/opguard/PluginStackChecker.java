package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.api.OpGuardAPI;
import com.github.guardedoperators.opguard.api.config.OpGuardConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Stack;

public final class PluginStackChecker
{
    private final StackTraceElement[] stackTrace;
    
    private final Stack<Plugin> allowed = new Stack<>();
    
    private Plugin plugin = null;
    private StackTraceElement element = null;
    
    public PluginStackChecker(OpGuardAPI api)
    {
        this.stackTrace = Thread.currentThread().getStackTrace();
        
        for (StackTraceElement element : stackTrace)
        {
            try
            {
                Plugin plugin = getPluginByClass(Class.forName(element.getClassName()));
            
                if (plugin != null && plugin != api.getPlugin())
                {
                    OpGuardConfig config = api.getConfig();
                    
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
            catch (ClassNotFoundException e) {} // Don't do anything: just keep going.
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
    
    public void disablePlugin(OpGuardAPI api, Context context)
    {
        if (!hasFoundPlugin())
        {
            throw new IllegalStateException("No plugin to disable.");
        }
    
        OpGuardConfig config = api.getConfig();
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
