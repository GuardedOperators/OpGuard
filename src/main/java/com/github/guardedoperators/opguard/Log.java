package com.github.guardedoperators.opguard;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log
{
    private final Plugin plugin;
    private final File file;
    
    public Log(Plugin plugin, String name)
    {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name + ".log");
        
        try
        {
            this.file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private String now()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }
    
    public void append(String message)
    {
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
