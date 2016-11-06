package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Verifier;
import org.bukkit.OfflinePlayer;

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
        
        private static Set<OfflinePlayer> getCopy()
        {
            return new LinkedHashSet<>(verified.values());
        }
    }
    
    private static final class PasswordWrapper
    {
        private static Password password;
    }
    
    public OpVerifier()
    {
        // TODO: Add all existing operators to ops list
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
            return true;
        }
        return false;
    }
    
    @Override
    public Password getPassword()
    {
        return new Password(PasswordWrapper.password);
    }
    
    @Override
    public boolean removePassword(Password password)
    {
        if (check(password))
        {
            PasswordWrapper.password = null;
            return true;
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
        return OpListWrapper.getCopy();
    }
    
    @Override
    public boolean op(OfflinePlayer player, Password password)
    {
        if (check(password))
        {
            OpListWrapper.verified.put(player.getUniqueId(), player);
            player.setOp(true);
            return true;
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
            return true;
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
        // TODO: save data
        return true;
    }
}
