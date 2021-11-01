package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.config.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class OpVerifier
{
    private static final class OpListWrapper
    {
        private static final Map<UUID, OfflinePlayer> verified = new LinkedHashMap<>();
        
        private static Set<UUID> getKeys()
        {
            return new LinkedHashSet<>(verified.keySet());
        }
        
        private static Set<OfflinePlayer> getValues()
        {
            return new LinkedHashSet<>(verified.values());
        }
    }
    
    private static final class PasswordWrapper
    {
        private static OpPassword password;
    }
    
    private final DataStorage storage;
    
    OpVerifier(DataStorage storage)
    {
        this.storage = storage;
        FileConfiguration data = storage.yaml();
        
        if (data.contains("hash"))
        {
            PasswordWrapper.password = new OpPassword(data.getString("hash"), true);
        }
        if (data.contains("verified"))
        {
            for (String operator : data.getStringList("verified"))
            {
                UUID uuid = UUID.fromString(operator);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                OpListWrapper.verified.put(uuid, player);
            }
        }
    }
    
    public boolean hasPassword()
    {
        return PasswordWrapper.password != null;
    }
    
    public boolean setPassword(OpPassword password)
    {
        if (!hasPassword() && password != null)
        {
            PasswordWrapper.password = password;
            return save();
        }
        return false;
    }
    
    public OpPassword getPassword()
    {
        return new OpPassword(PasswordWrapper.password);
    }
    
    public boolean removePassword(OpPassword password)
    {
        if (check(password))
        {
            PasswordWrapper.password = null;
            return save();
        }
        return false;
    }
    
    public boolean check(OpPassword password)
    {
        return !hasPassword() || PasswordWrapper.password.getHash().equals(password.getHash());
    }
    
    public Collection<OfflinePlayer> getVerifiedOperators()
    {
        return OpListWrapper.getValues();
    }
    
    public Collection<UUID> getVerifiedUUIDs()
    {
        return OpListWrapper.getKeys();
    }
    
    public boolean op(OfflinePlayer player, OpPassword password)
    {
        if (check(password))
        {
            OpListWrapper.verified.put(player.getUniqueId(), player);
            player.setOp(true);
            return save();
        }
        return false;
    }
    
    public boolean deop(OfflinePlayer player, OpPassword password)
    {
        if (check(password))
        {
            OpListWrapper.verified.remove(player.getUniqueId());
            player.setOp(false);
            return save();
        }
        return false;
    }
    
    public boolean isVerified(UUID uuid)
    {
        return OpListWrapper.verified.containsKey(uuid);
    }
    
    public boolean isVerified(OfflinePlayer player)
    {
        return isVerified(player.getUniqueId());
    }
    
    public boolean isVerified(CommandSender sender)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            return true;
        }
        else if (sender instanceof Player)
        {
            Player player = (Player) sender;
            return isVerified(player.getUniqueId());
        }
        return false;
    }
    
    public boolean save()
    {
        return save(true);
    }
    
    public boolean save(boolean async)
    {
        storage.reset(this);
        storage.save(async);
        return true;
    }
}
