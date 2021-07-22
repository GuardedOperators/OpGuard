package org.github.guardedoperators.opguard;

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
    
    public static void send(String message)
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
        send(message);
    }
}
