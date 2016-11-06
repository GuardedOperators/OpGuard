package com.rezzedup.opguard.api;

import org.bukkit.configuration.file.FileConfiguration;

public interface Config
{
    void reload();
    
    FileConfiguration get();
}
