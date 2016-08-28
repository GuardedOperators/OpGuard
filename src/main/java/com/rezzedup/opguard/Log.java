package com.rezzedup.opguard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Log
{
    private File file;
    private FileConfiguration config;
    
    public Log(JavaPlugin plugin, String name)
    {
        plugin.getDataFolder().mkdirs();
        
        this.file = new File(plugin.getDataFolder() + "/" + name + ".log");
        this.config = plugin.getConfig();
        
        if (!this.file.exists())
        {
            try
            {
                this.file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private String now()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }
    
    public void append(String type, String message)
    {
        if (!config.getBoolean("log." + type) || !config.getBoolean("log.enabled"))
        {
            return;
        }
        message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
        message = now() + " " + message + "\n";
        byte[] msg = message.getBytes();
        
        try
        {
            Files.write(this.file.toPath(), msg, StandardOpenOption.APPEND);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
