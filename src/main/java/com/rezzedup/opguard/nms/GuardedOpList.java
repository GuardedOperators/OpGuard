package com.rezzedup.opguard.nms;

import com.rezzedup.opguard.PluginStackChecker;
import net.minecraft.server.v1_10_R1.DedicatedPlayerList;
import net.minecraft.server.v1_10_R1.OpList;
import net.minecraft.server.v1_10_R1.OpListEntry;
import net.minecraft.server.v1_10_R1.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Field;


public class GuardedOpList extends OpList
{
    private static boolean injected = false;
    
    public static void inject() throws Exception
    {
        if (injected)
        {
            return;
        }
        
        Field playerList = CraftServer.class.getDeclaredField("playerList");
        playerList.setAccessible(true);
        
        DedicatedPlayerList dedicatedPlayerList = (DedicatedPlayerList) playerList.get(Bukkit.getServer());
        
        Field opFile = PlayerList.class.getDeclaredField("c");
        opFile.setAccessible(true);
        File file = (File) opFile.get(dedicatedPlayerList);
        
        Field operators = PlayerList.class.getDeclaredField("operators");
        operators.setAccessible(true);
        
        GuardedOpList guardedList = new GuardedOpList(file);
    
        operators.set(dedicatedPlayerList, guardedList);
        injected = true;
    }
    
    private GuardedOpList(File file)
    {
        super(file);
    }
    
    public void add(OpListEntry entry)
    {
        PluginStackChecker stack = new PluginStackChecker();
        String name = entry.getKey().getName();
        
        Bukkit.broadcastMessage("\n\n\nGiving OP to " + name + "\n\n");
        
        if (stack.foundPlugin())
        {
            Plugin plugin = stack.getPlugin();
            // TODO: Unload plugin
            Bukkit.broadcastMessage("\n\nPlugin " + plugin.getName() + " tried to OP " + entry.getKey().getName() + "\n\n");
            return;
        }
        
        super.add(entry);
    }
}
