package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Verifier;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OpVerifier implements Verifier
{
    private static final class OpListWrapper
    {
        private static final Map<UUID, OfflinePlayer> verified = new HashMap<>();
        
        private static Set<OfflinePlayer> getCopy()
        {
            return new HashSet<>(verified.values());
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
    public void setPassword(Password password)
    {
        if (PasswordWrapper.password == null && password != null)
        {
            PasswordWrapper.password = password;
        }
    }
    
    @Override
    public void removePassword(Password password)
    {
        if (check(password))
        {
            PasswordWrapper.password = null;
        }
    }
    
    @Override
    public Password getPassword()
    {
        return new Password(PasswordWrapper.password);
    }
    
    @Override
    public boolean check(Password password)
    {
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
        return true;
    }
}
