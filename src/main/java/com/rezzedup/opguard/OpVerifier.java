package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Verifier;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OpVerifier implements Verifier
{
    private static final class OpListWrapper
    {
        private static final Set<OfflinePlayer> verified = new HashSet<>();
        
        private static Set<OfflinePlayer> getCopy()
        {
            return new HashSet<>(verified);
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
            // TODO: add to verified list
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
            // TODO: remove from verified list
            player.setOp(false);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean save()
    {
        return false;
    }
}
