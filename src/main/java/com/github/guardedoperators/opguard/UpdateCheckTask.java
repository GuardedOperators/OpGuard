package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.api.OpGuardAPI;
import com.github.guardedoperators.opguard.api.config.OpGuardConfig;
import com.github.zafarkhaja.semver.Version;
import com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class UpdateCheckTask extends BukkitRunnable
{
    public static final String DOWNLOAD_URL = "https://www.spigotmc.org/resources/opguard.23200/";
    public static final String SPIGET_URL = "https://api.spiget.org/v2/resources/23200/versions/latest";
    
    private final OpGuardAPI api;
    private final String userAgent;
    
    UpdateCheckTask(OpGuardAPI api)
    {
        this.api = api;
        
        this.userAgent = "OpGuard" + "/" + api.getVersion() + " (Minecraft) " +
            api.getPlugin().getServer().getName() + "/" + api.getPlugin().getServer().getVersion();
        
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
            connection.addRequestProperty("User-Agent", userAgent);
            
            String name = "0.0.0";
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
            {
                name = new JsonParser().parse(reader).getAsJsonObject().get("name").getAsString();
            }
            
            Version version = Version.valueOf(name);
            
            if (api.getVersion().lessThan(version))
            {
                Messenger.send(
                    "[OpGuard] &eAn update is available!&r Download &fv" + version + "&r here: &6" + DOWNLOAD_URL
                );
            }
        }
        catch (Exception ignored) {}
    }
}
