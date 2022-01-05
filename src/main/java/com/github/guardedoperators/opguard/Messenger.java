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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger
{
    public static String colorful(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static void console(String message)
    {
        Bukkit.getServer().getConsoleSender().sendMessage(colorful(message));
    }
    
    public static void send(Player player, String message)
    {
        player.sendMessage(colorful(message));
    }
    
    
    public static void send(CommandSender sender, String message)
    {
        sender.sendMessage(colorful(message));
    }
    
    public static void broadcast(String message, String permission)
    {
        String content = colorful(message);
        Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(permission)).forEach(p -> p.sendMessage(content));
        console(message);
    }
}
