package com.rezzedup.opguard.config;

import com.rezzedup.opguard.api.config.Config;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class BaseConfig implements Config
{
    protected final Plugin plugin;
    protected final File file;
    protected FileConfiguration config;
    
    public BaseConfig(Plugin plugin, String filename)
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
    
    public BaseConfig(Plugin plugin)
    {
        this(plugin, null);
    }
    
    protected abstract void load();
    
    @Override
    public FileConfiguration get()
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
