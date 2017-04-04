package com.rezzedup.opguard;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Version;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class UpdateCheckTask extends BukkitRunnable
{
    public static final String DOWNLOAD_URL = "https://www.spigotmc.org/resources/opguard.23200/";
    public static final String USER_AGENT = "OpGuard";
    public static final String SPIGET_URL = "https://api.spiget.org/v2/resources/23200/versions?size=1&sort=-name";
    
    private final OpGuardAPI api;
    
    UpdateCheckTask(OpGuardAPI api)
    {
        this.api = api;
    
        OpGuardConfig config = api.getConfig();
        long hours = config.getUpdateCheckInterval();
        
        if (hours < 1)
        {
            Messenger.send("[OpGuard] Invalid update check interval " + hours + ". Defaulting to 12 hours.");
            hours = 12;
        }
        
        if (config.canCheckForUpdates())
        {
            runTaskTimerAsynchronously(api.getPlugin(), 100L, hours * 60L * 60L * 20L);
        }
    }
    
    @Override
    public void run()
    {
        try 
        {
            URLConnection connection = new URL(SPIGET_URL).openConnection();
            String name = null;
            
            connection.addRequestProperty("User-Agent", USER_AGENT);
    
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) 
            {
                JsonArray results = new JsonParser().parse(reader).getAsJsonArray();
                name = results.get(0).getAsJsonObject().get("name").getAsString();
            }
            
            Version version = Version.of(name);
    
            synchronized (api)
            {
                if (api.getVersion().isAtLeast(version))
                {
                    return;
                }
        
                Messenger.send
                (
                    "[OpGuard] &eAn update is available!&r Download &fv" + version + "&r here: &6" + DOWNLOAD_URL
                );
            }
        }
        catch (Exception e) 
        {
            // Don't pester users about failed update checks...
        }
    }
}
