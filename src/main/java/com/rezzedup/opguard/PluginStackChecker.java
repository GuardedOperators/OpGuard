package com.rezzedup.opguard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class PluginStackChecker
{
    private final StackTraceElement[] stack;
    
    private Plugin plugin = null;
    
    public PluginStackChecker()
    {
        stack = Thread.currentThread().getStackTrace();
        check();
    }
    
    public boolean foundPlugin()
    {
        return plugin != null;
    }
    
    public Plugin getPlugin()
    {
        return plugin;
    }
    
    private void check()
    {
        Map<String, Plugin> plugins = getPluginPackageMap();
        
        for (StackTraceElement element : stack)
        {
            String pack = getPackage(element.getClassName());
            
            if (pack.startsWith("net.minecraft") || pack.startsWith("org.bukkit") || pack.startsWith("com.rezzedup.opguard"))
            {
                continue;
            }
            
            for (String pluginPackage : plugins.keySet())
            {
                if (pack.startsWith(pluginPackage))
                {
                    plugin = plugins.get(pluginPackage);
                    return;
                }
            }
        }
    }
    
    // plugin.package.name -> Plugin
    private Map<String, Plugin> getPluginPackageMap()
    {
        SimplePluginManager pluginManager = (SimplePluginManager) Bukkit.getPluginManager();
        Plugin[] plugins = pluginManager.getPlugins();
    
        Map<String, Plugin> packages = new LinkedHashMap<>();
    
        for (Plugin plugin : plugins)
        {
            packages.put(getPackage(plugin.getClass()), plugin);
        }
        
        return packages;
    }
    
    private String getPackage(Class clazz)
    {
        try
        {
            return getPackage(clazz.getCanonicalName());
        }
        catch (NullPointerException e)
        {
            return "null";
        }
    }
    
    private String getPackage(String from)
    {
        return from.replaceAll("/\\.[$a-z0-9_]*$/i", "");
    }
}
