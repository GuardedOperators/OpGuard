package space.rezz.opguard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class VerifiedOperators
{
    private static List<OfflinePlayer> verified = new ArrayList<OfflinePlayer>();
    private static boolean latest = false;
    
    public static void addExistingOperators()
    {
        if (verified.size() > 0)
        {
            return;
        }
        List<String> storage = OpGuard.getInstance().getConfig().getStringList("verified");
        
        if (storage.size() > 0)
        {
            for (String uuidString : storage)
            {
                UUID uuid = UUID.fromString(uuidString);
                verified.add(Bukkit.getOfflinePlayer(uuid));
            }
            latest = true;
        }
        else
        {
            verified.addAll(Bukkit.getOperators());
            save();
        }
    }
    
    public static void inspect()
    {
        boolean punish = OpGuard.getInstance().getConfig().getBoolean("punish.plugin-attempt");
        
        for (OfflinePlayer player : Bukkit.getOperators())
        {
            if (!verified.contains(player))
            {
                player.setOp(false);
                
                String message = "&f[&c&lWARNING&f] A plugin has attempted to op `&c" + player.getName() + "&f`";
                
                OpGuard.warn("plugin-attempt", message);
                OpGuard.log("plugin-attempt", message);
                
                if (punish)
                {
                    PunishmentCommand.execute(player.getName());
                }
            }
        }
        int size = verified.size();
        verified.removeIf(op -> !(Bukkit.getOperators().contains(op)));
        
        if (size != verified.size())
        {
            latest = false;
        }
    }
    
    static void addOperator(OfflinePlayer player)
    {       
        if (!verified.contains(player))
        {
            verified.add(player);
            player.setOp(true);
            
            latest = false;
            String message = "&f[&6&lVERIFIED&f] &e" + player.getName() + "&f is now op.";
            
            OpGuard.warn("status", message);
            OpGuard.log("status", message);
        }
    }
    
    public static boolean save()
    {
        if (!latest)
        {
            List<String> uuids = new ArrayList<String>();
            
            for (OfflinePlayer player : verified)
            {
                uuids.add(player.getUniqueId().toString());
            }
            OpGuard.getInstance().getConfig().set("verified", uuids);
            
            latest = true;
            
            return true;
        }
        return false;
    }
}

