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
import com.github.guardedoperators.opguard.PluginStackTrace;
import com.github.guardedoperators.opguard.PunishmentReason;
import com.github.guardedoperators.opguard.util.Debug;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class PermissionCheckListener implements Listener
{
    private static final String ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
    
    private final Random random = new SecureRandom();
    
    private final OpGuard opguard;
    private final List<String> permissions;
    
    public PermissionCheckListener(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
        
        int total = 100;
        List<String> nodes = new ArrayList<>(total);
        
        for (int i = 0; i < total; i++) { nodes.add(generateRandomPermissionNode(random)); }
        this.permissions = List.copyOf(nodes);
        
        Debug.with(logger ->
        {
            logger.info("[Permission Check] Generated " + total + " permission nodes:");
            
            for (int i = 0; i < permissions.size(); i++)
            {
                logger.info(String.format("[Permission Check] #%03d: %s", i + 1, permissions.get(i)));
            }
        });
        
        // Check all online players once per tick.
        opguard.server().getScheduler()
            .runTaskTimer(opguard.plugin(), this::checkAllOnlinePlayers, 0L, 1L);
    }
    
    private static String generateRandomPermissionNode(Random random)
    {
        StringBuilder node = new StringBuilder();
        
        int length = 0; // get random length between 15 and 50
        while (length < 15) { length = random.nextInt(51); }
        
        int placedDotAgo = 0;
        
        for (int i = 0; i < length; i++)
        {
            if (i >= 10 && placedDotAgo++ > 0)
            {
                if (random.nextInt(50) < placedDotAgo)
                {
                    node.append('.');
                    placedDotAgo = 0;
                }
            }
            
            node.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        
        return node.toString();
    }
    
    private String randomPermissionNode()
    {
        return permissions.get(random.nextInt(permissions.size()));
    }
    
    private void checkAllOnlinePlayers()
    {
        if (!opguard.config().canCheckPermissions()) { return; }
        
        // Get a random permission node to check all players against.
        String permission = randomPermissionNode();
        
        for (Player player : opguard.server().getOnlinePlayers())
        {
            if (!player.hasPermission(permission)) { continue; }
            if (opguard.verifier().isVerified(player)) { continue; }
            
            opguard.notifications().playerHasAccessToAllPermissions(player);
            opguard.punishments().punishPlayer(PunishmentReason.UNAUTHORIZED_ALL_PERMISSIONS, player);
        }
    }
    
    public void checkPermissions(PlayerEvent event)
    {
        if (!opguard.config().canCheckPermissions()) { return; }
        
        Player player = event.getPlayer();
        String permission = randomPermissionNode();
        
        if (!player.hasPermission(permission)) { return; }
        if (opguard.verifier().isVerified(player)) { return; }
        
        PluginStackTrace stack = opguard.findPluginsOnStack();
        
        // Only exempted plugins found -> exit.
        if (stack.hasOnlyExemptPlugins())
        {
            opguard.notifications().pluginAllowedToGrantAllPermissions(stack.topExemptPlugin(), player);
            return;
        }
        
        // Non-exempt plugin found -> warn and continue to punishment below.
        if (stack.hasCaughtPlugins())
        {
            opguard.notifications().pluginAttemptedToGrantAllPermissions(stack.topCaughtPlugin(), player);
            opguard.punishments().handleCaughtPlugins(PunishmentReason.UNAUTHORIZED_ALL_PERMISSIONS, stack);
        }
        
        if (event instanceof Cancellable) { ((Cancellable) event).setCancelled(true); }
        
        opguard.notifications().playerHasAccessToAllPermissions(player);
        opguard.punishments().punishPlayer(PunishmentReason.UNAUTHORIZED_ALL_PERMISSIONS, player);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        checkPermissions(event);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        checkPermissions(event);
    }
}
