package com.rezzedup.opguard.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServerStack
{
    public static Deque<Plugin> getCurrentPlugins()
    {
        Map<ClassLoader, Plugin> pluginClassLoaders = new HashMap<>();
        
        // Associate ClassLoaders to Plugins.
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
            .forEach(plugin -> pluginClassLoaders.put(plugin.getClass().getClassLoader(), plugin));
        
        List<Plugin> plugins = 
            Arrays.stream(Thread.currentThread().getStackTrace())
                // Get the class of this StackTraceElement.
                .map(element -> 
                {
                    try { return Class.forName(element.getClassName()); }
                    catch (ClassNotFoundException ignored) { return null; }
                })
                .filter(Objects::nonNull)
                // Get the plugin of this class via its ClassLoader.
                .map(clazz -> pluginClassLoaders.get(clazz.getClassLoader()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        return new LinkedList<>(plugins);
    }
    
    public static Predicate<Plugin> eliminate(Collection<Plugin> whitelist)
    {
        return whitelist::contains;
    }
}
