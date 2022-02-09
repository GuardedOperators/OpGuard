/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2022 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.config.OpGuardConfig;
import com.github.zafarkhaja.semver.Version;
import com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

final class UpdateCheckTask extends BukkitRunnable
{
    public static final String DOWNLOAD_URL = "https://www.spigotmc.org/resources/opguard.23200/";
    public static final String SPIGET_URL = "https://api.spiget.org/v2/resources/23200/versions/latest";
    
    private final OpGuard opguard;
    private final String userAgent;
    
    UpdateCheckTask(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
        
        this.userAgent = "OpGuard" + "/" + opguard.version() + " (Minecraft) " +
             opguard.plugin().getServer().getName() + "/" + opguard.plugin().getServer().getVersion();
        
        OpGuardConfig config = opguard.config();
        long hours = config.getUpdateCheckInterval();
        
        if (hours < 1)
        {
            opguard.logger().warning("Invalid update check interval: " + hours + ". Defaulting to every 12 hours.");
            hours = 12;
        }
        
        if (config.canCheckForUpdates())
        {
            runTaskTimerAsynchronously(opguard.plugin(), 20L, hours * 60L * 60L * 20L);
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
            
            if (opguard.version().lessThan(version))
            {
                Messenger.console(
                    "[OpGuard] &eAn update is available!&r Download &fv" + version + "&r here: &6" + DOWNLOAD_URL
                );
            }
        }
        catch (Exception ignored) {}
    }
}
