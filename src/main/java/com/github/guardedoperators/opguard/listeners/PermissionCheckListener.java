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

import com.github.guardedoperators.opguard.Context;
import com.github.guardedoperators.opguard.OpGuard;
import com.github.guardedoperators.opguard.PluginStackVerifier;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        
        for (int i = 0; i < total; i++) { nodes.add(generateRandomPermissionNode()); }
        this.permissions = List.copyOf(nodes);
        
        opguard.server().getScheduler().runTaskTimer(opguard.plugin(), this::checkAllOnlinePlayers, 0L, 1L);
    }
    
    private String generateRandomPermissionNode()
    {
        return IntStream.range(0, 50)
            .mapToObj(i ->
                String.valueOf(ALPHABET.charAt(random.nextInt(ALPHABET.length())))
            )
            .collect(Collectors.joining());
    }
    
    private String randomPermissionNode()
    {
        return permissions.get(random.nextInt(permissions.size()));
    }
    
    private void checkAllOnlinePlayers()
    {
        if (!opguard.config().canCheckPermissions()) { return; }
        
        String permission = randomPermissionNode();
        
        for (Player player : opguard.server().getOnlinePlayers())
        {
            if (!player.hasPermission(permission)) { continue; }
            if (opguard.verifier().isVerified(player)) { continue; }
            
            Context context = new Context(opguard).playerAttempt().hasInvalidPermissions();
            context.warning("Player <!>" + player.getName() + "&f has access to all permissions but isn't a verified operator");
            opguard.warn(context).log(context).punish(context, player.getName());
        }
    }
    
    public void checkPermissions(PlayerEvent event)
    {
        if (!opguard.config().canCheckPermissions()) { return; }
        
        String permission = randomPermissionNode();
        Player player = event.getPlayer();
        
        if (!player.hasPermission(permission)) { return; }
        if (opguard.verifier().isVerified(player)) { return; }
        
        Context context = new Context(opguard).playerAttempt().hasInvalidPermissions();
        PluginStackVerifier.Result result = opguard.callStack().findPluginsOnStack();
        
        // Only exempted plugins found -> exit method.
        if (result.hasOnlyExemptPlugins())
        {
            Context allowed = context.copy();
            String name = result.topExemptPlugin().getName();
            
            allowed.okay(
                "The plugin &7" + name + "&f was allowed to grant all permissions to &7" + player.getName()
            );
            opguard.warn(allowed).log(allowed);
            
            return;
        }
        
        // Non-exempt plugin found -> warn and continue to punishment below.
        if (result.hasCaughtPlugins())
        {
            // TODO: make %player% work
            result.handleCaughtPlugins("The plugin %plugin% attempted to grant all permissions to %player%");
        }
        
        if (event instanceof Cancellable)
        {
            ((Cancellable) event).setCancelled(true);
        }
        
        context.warning("Player <!>" + player.getName() + "&f has access to all permissions but isn't a verified operator");
        opguard.warn(context).log(context).punish(context, player.getName());
        
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
