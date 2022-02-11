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
package com.github.guardedoperators.opguard.listeners;

import com.github.guardedoperators.opguard.OpGuard;
import com.github.guardedoperators.opguard.util.AuthenticationMode;
import com.github.guardedoperators.opguard.util.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.tlinkowski.annotation.basic.NullOr;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class OfflineModeCheckListener implements Listener
{
    private @NullOr Instant warning = null;
    
    private final OpGuard opguard;
    private AuthenticationMode mode;
    
    public OfflineModeCheckListener(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
        
        AuthenticationMode.Detected startup = AuthenticationMode.ofServer(opguard.server());
        opguard.logger().info("Detected " + startup);
        
        this.mode = startup.mode();
        warnIfOfflineMode();
        
        long delay = Duration.ofMinutes(30).toSeconds() * 20;
        opguard.server().getScheduler().runTaskTimer(opguard.plugin(), this::warnIfOfflineMode, delay, delay);
    }
    
    private void warnIfOfflineMode()
    {
        if (mode != AuthenticationMode.OFFLINE) { return; }
        // TODO: add config option to disable warning
        // if (opguard.config().offlineMode()) { return; }
        
        if (!Cooldown.of30Minutes().since(warning)) { return; }
        
        warning = Instant.now();
        Logger logger = opguard.logger();
        
        logger.warning("======================");
        logger.warning("OFFLINE MODE DETECTED!");
        logger.warning("======================");
        logger.warning("OpGuard does not officially support offline mode - unauthenticated UUIDs cannot be properly verified.");
        logger.warning("You are at great risk of account takeover! Please consider running your server in online mode.");
        logger.warning("This warning may be disabled in the config, but beware: support will NOT be provided to offline mode servers.");
        logger.warning("======================");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (AuthenticationMode.ofUuid(uuid) == AuthenticationMode.OFFLINE)
        {
            // An offline-mode player joined, server must be in offline mode
            if (mode != AuthenticationMode.OFFLINE) { mode = AuthenticationMode.OFFLINE; }
            
            // TODO: check same warning config option mentioned above
            opguard.logger().warning(player.getName() + " joined with unauthenticated offline UUID: " + uuid);
            warnIfOfflineMode();
        }
    }
}
