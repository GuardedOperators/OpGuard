package com.rezzedup.opguard.config;

import com.rezzedup.opguard.Messenger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OpGuardConfig extends Config
{
    public OpGuardConfig(Plugin plugin)
    {
        super(plugin);
    }
    
    @Override
    protected void load()
    {
        if (!config.contains("version"))
        {
            migrateConfig(config);
        }
        else
        {
            Messenger.broadcast("Version exists: " + config.getString("version"));
        }
    }
    
    private void migrateConfig(FileConfiguration old)
    {
        ConfigTemplate template = new ConfigTemplate(this.getClass(), "config.template.yml");
        List<String> lines = template.apply(old);
        
        File dir = plugin.getDataFolder();
        
        if (file.exists())
        {
            file.renameTo(new File(dir, "config.yml.old"));
        }
        
        try
        {
            file.createNewFile();
            
            Path path = Paths.get(file.toURI());
            Files.write(path, lines, Charset.forName("UTF-8"));
            
            config = YamlConfiguration.loadConfiguration(file);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
    
        Messenger.broadcast("&aCompleted migration: &2&l" + config.getString("version"));
    }
    
    @Override
    public void save() {}
}
