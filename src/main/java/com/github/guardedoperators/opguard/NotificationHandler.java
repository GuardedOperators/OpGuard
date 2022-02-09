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

import com.github.guardedoperators.opguard.util.Placeholders;
import org.bukkit.entity.Player;

import java.util.Objects;

public class NotificationHandler
{
    private final OpGuard opguard;
    
    NotificationHandler(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
    }
    
    public void usernamePunished(String username)
    {
        Placeholders placeholders = new Placeholders();
        
        placeholders.map("player", "username").to(() -> username);
        
        String message = placeholders.update("Punished %player% for attempting to gain op");
        
        opguard.logger().warning(message);
    }
    
    public void playerHasAccessToAllPermissions(Player player)
    {
        Placeholders placeholders = new Placeholders();
        
        placeholders.map("player").to(player::getName);
        
        String message = placeholders.update("Player %player% has access to all permissions but isn't a verified operator");
        
        opguard.logger().warning(message);
    }
    
    public void pluginAttemptedToGrantAllPermissions(PluginOnStack caught, Player player)
    {
        Placeholders placeholders = new Placeholders();
        
        placeholders.map("plugin").to(caught::name);
        placeholders.map("player").to(player::getName);
        
        String message = placeholders.update("The plugin %plugin% attempted to grant all permissions to %player%");
        
        opguard.logger().warning(message);
    }
    
    public void pluginAllowedToGrantAllPermissions(PluginOnStack allowed, Player player)
    {
        Placeholders placeholders = new Placeholders();
        
        placeholders.map("plugin").to(allowed::name);
        placeholders.map("player").to(player::getName);
        
        String message = placeholders.update("The plugin %plugin% was allowed to grant all permissions to %player%");
        
        opguard.logger().warning(message);
    }
    
    
}
