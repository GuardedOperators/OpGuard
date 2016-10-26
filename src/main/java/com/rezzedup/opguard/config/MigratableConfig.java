package com.rezzedup.opguard.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MigratableConfig extends Config
{
    public MigratableConfig(Plugin plugin)
    {
        super(plugin);
    }
    
    @Override
    protected void load()
    {
        // Check version in the future.
        if (!config.contains("version"))
        {
            migrateConfig(config);
        }
    }
    
    private void migrateConfig(FileConfiguration old)
    {
        ConfigurationTemplate template = new ConfigurationTemplate(this.getClass(), "config.template.yml");
        List<String> lines = template.apply(old);
        
        File dir = plugin.getDataFolder();
        
        if (file.exists())
        {
            String name = "config.yml.old";
            File rename = new File(dir, name);
            int version = 0;
            
            while (rename.exists())
            {
                version += 1;
                String updatedName = name + version;
                rename = new File(dir, updatedName);
            }
            
            file.renameTo(rename);
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
    }
}
