package com.github.guardedoperators.opguard.config;

import com.github.guardedoperators.opguard.api.config.Config;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

abstract class BaseConfig implements Config
{
    protected final Plugin plugin;
    protected final File file;
    protected FileConfiguration config;
    
    BaseConfig(Plugin plugin, String filename)
    {
        this.plugin = plugin;
        
        if (filename == null)
        {
            this.file = new File(plugin.getDataFolder(), "config.yml");
            this.config = plugin.getConfig();
        }
        else
        {
            this.file = new File(plugin.getDataFolder(), filename);
            this.config = YamlConfiguration.loadConfiguration(file);
        }
        
        load();
    }
    
    BaseConfig(Plugin plugin)
    {
        this(plugin, null);
    }
    
    protected abstract void load();
    
    @Override
    public FileConfiguration yaml()
    {
        return config;
    }
    
    @Override
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
