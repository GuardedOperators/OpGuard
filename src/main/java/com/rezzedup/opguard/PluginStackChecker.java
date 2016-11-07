package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.net.URL;

public class PluginStackChecker
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
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
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
    
    public URL getPluginJar()
    {
        Class clazz = plugin.getClass();
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }
    
    public boolean renameJarFile()
    {
        File current = new File(getPluginJar().getFile());
        File replace = new File(current.toString() + ".opguard-disabled");
        return current.renameTo(replace);
    }
}
