package com.rezzedup.opguard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger 
{
    public static void send(Player player, String message)
    {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public static void send(CommandSender sender, String message)
    {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public static void broadcast(String message)
    {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        sendConsole(message);
    }
    
    public static void broadcast(String message, String permission)
    {
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', message), permission);
        sendConsole(message);
    }
    
    public static void sendConsole(String message)
    {
        Bukkit.getServer().getConsoleSender().sendMessage
        (
            ChatColor.translateAlternateColorCodes('&', message)
        );
    }
}
