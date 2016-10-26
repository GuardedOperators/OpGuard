package com.rezzedup.opguard.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class Config
{
    protected final Plugin plugin;
    protected final File file;
    protected FileConfiguration config;
    
    public Config(Plugin plugin, File file)
    {
        this.plugin = plugin;
        
        if (file == null)
        {
            this.file = new File(plugin.getDataFolder(), "config.yml");
            this.config = plugin.getConfig();
        }
        else
        {
            this.file = file;
            this.config = YamlConfiguration.loadConfiguration(file);
        }
        
        load();
    }
    
    public Config(Plugin plugin)
    {
        this(plugin, null);
    }
    
    protected abstract void load();
    
    public abstract void save();
    
    public FileConfiguration get()
    {
        return config;
    }
    
    public void reload()
    {
        try
        {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}
