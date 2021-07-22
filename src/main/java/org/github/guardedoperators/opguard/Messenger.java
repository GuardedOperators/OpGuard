package org.github.guardedoperators.opguard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A utility class for sending messages with &-based colorcodes.
 */
public class Messenger
{
    /**
     * Wrapper method for {@link org.bukkit.ChatColor#translateAlternateColorCodes(char, String)}
     * where the translated-char is set to '&'.
     * <br>
     *     <b>Valid color codes:</b>  &0-9, &a-f, &k, &l-o
     *
     * @param message Text to translate color codes.
     * @return Colorful string.
     */
    public static String colorful(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Utility method for sending the console ({@link org.bukkit.command.ConsoleCommandSender})
     * an &-based color coded message.
     * @param message Message to be sent.
     */
    public static void send(String message)
    {
        Bukkit.getServer().getConsoleSender().sendMessage(colorful(message));
    }
    
    /**
     * Wrapper method for sending a {@link org.bukkit.entity.Player}
     * an &-based color coded message.
     * @param player Player to receive the message.
     * @param message Message to be sent.
     */
    public static void send(Player player, String message)
    {
        player.sendMessage(colorful(message));
    }
    
    /**
     * Wrapper method for sending a {@link org.bukkit.command.CommandSender}
     * an &-based color coded message.
     * @param sender CommandSender to receive the message.
     * @param message Message to be sent.
     */
    public static void send(CommandSender sender, String message)
    {
        sender.sendMessage(colorful(message));
    }
    
    /**
     * Wrapper method for {@link org.bukkit.Bukkit#broadcastMessage(String)}
     * where the &-based color coded message is automatically translated.
     * @param message Message to be broadcasted.
     */
    public static void broadcast(String message)
    {
        Bukkit.broadcastMessage(colorful(message));
    }
    
    /**
     * Wrapper method for {@link org.bukkit.Bukkit#broadcast(String, String)}
     * where the &-based color coded message is automatically translated.
     * @param message Message to be broadcasted.
     * @param permission Permission online players must have in order to receive the broadcasted message.
     */
    public static void broadcast(String message, String permission)
    {
        Bukkit.broadcast(colorful(message), permission);
        send(message);
    }
}
