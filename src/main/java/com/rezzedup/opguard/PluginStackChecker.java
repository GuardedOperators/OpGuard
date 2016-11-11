package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

public final class PluginStackChecker
{
    private final StackTraceElement[] stack;
    
    private Plugin plugin = null;
    private StackTraceElement element = null;
    
    public PluginStackChecker(OpGuardAPI api)
    {
        this.stack = Thread.currentThread().getStackTrace();
        
        for (StackTraceElement element : stack)
        {
            try
            {
                Plugin plugin = getPluginByClass(Class.forName(element.getClassName()));
            
                if (plugin != null && plugin != api.getPlugin())
                {
                    this.plugin = plugin;
                    this.element = element;
                    break;
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
    
    public boolean foundPlugin()
    {
        return plugin != null;
    }
    
    public Plugin getPlugin()
    {
        return plugin;
    }
    
    public StackTraceElement[] getStackTrace()
    {
        return stack;
    }
    
    public StackTraceElement getPluginStackElement()
    {
        return element;
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
        if (!foundPlugin())
        {
            throw new IllegalStateException("No plugin to disable.");
        }
    
        OpGuardConfig config = api.getConfig();
        String name = plugin.getName();
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
