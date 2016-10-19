package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.wrapper.GuardedPlayer;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OpVerifier implements Verifier
{
    private final OpListWrapper wrapper = new OpListWrapper();
    
    private final static class OpListWrapper
    {
        private final Set<OfflinePlayer> verified = new HashSet<>();
        
        private Set<OfflinePlayer> getCopy()
        {
            return new HashSet<>(verified);
        }
    }
    
    public OpVerifier()
    {
        // TODO: Add all existing operators to ops list
    }
    
    @Override
    public Collection<OfflinePlayer> getVerifiedOperators()
    {
        return wrapper.getCopy();
    }
    
    @Override
    public boolean check(Password password)
    {
        return false;
    }
    
    @Override
    public boolean op(OfflinePlayer player, Password password)
    {
        return false;
    }
    
    @Override
    public boolean deop(OfflinePlayer player, Password password)
    {
        return false;
    }
    
    @Override
    public boolean save()
    {
        return false;
    }
}
