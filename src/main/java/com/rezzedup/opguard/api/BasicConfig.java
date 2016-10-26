package com.rezzedup.opguard.api;

import org.bukkit.configuration.file.FileConfiguration;

public interface BasicConfig
{
    public void reload();
    
    public FileConfiguration get();
}
