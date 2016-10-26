package com.rezzedup.opguard.api;

import org.bukkit.configuration.file.FileConfiguration;

public interface Config
{
    public void reload();
    
    public FileConfiguration get();
}
