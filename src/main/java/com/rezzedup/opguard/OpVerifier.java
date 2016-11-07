package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Password;
import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.config.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OpVerifier implements Verifier
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
        private static Password password;
    }
    
    private final DataStorage storage;
    
    public OpVerifier(DataStorage storage)
    {
        this.storage = storage;
        FileConfiguration data = storage.get();
        
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
    
    @Override
    public boolean hasPassword()
    {
        return PasswordWrapper.password != null;
    }
    
    @Override
    public boolean setPassword(Password password)
    {
        if (!hasPassword() && password != null)
        {
            PasswordWrapper.password = password;
            return save();
        }
        return false;
    }
    
    @Override
    public Password getPassword()
    {
        return new OpPassword(PasswordWrapper.password);
    }
    
    @Override
    public boolean removePassword(Password password)
    {
        if (check(password))
        {
            PasswordWrapper.password = null;
            return save();
        }
        return false;
    }
    
    @Override
    public boolean check(Password password)
    {
        if (!hasPassword())
        {
            return true;
        }
        return PasswordWrapper.password.compare(password);
    }
    
    @Override
    public Collection<OfflinePlayer> getVerifiedOperators()
    {
        return OpListWrapper.getValues();
    }
    
    @Override
    public Collection<UUID> getVerifiedUUIDs()
    {
        return OpListWrapper.getKeys();
    }
    
    @Override
    public boolean op(OfflinePlayer player, Password password)
    {
        if (check(password))
        {
            OpListWrapper.verified.put(player.getUniqueId(), player);
            player.setOp(true);
            return save();
        }
        return false;
    }
    
    @Override
    public boolean deop(OfflinePlayer player, Password password)
    {
        if (check(password))
        {
            OpListWrapper.verified.remove(player.getUniqueId());
            player.setOp(false);
            return save();
        }
        return false;
    }
    
    @Override
    public boolean save()
    {
        return save(true);
    }
    
    @Override
    public boolean save(boolean async)
    {
        storage.reset(this);
        storage.save(async);
        return true;
    }
}
