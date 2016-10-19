package com.rezzedup.opguard;

import com.rezzedup.opguard.api.Authenticator;
import com.rezzedup.opguard.api.PasswordHandler;
import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.wrapper.GuardedPlayer;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    
    private static final class AuthWrapper
    {
        private static VerifiedAuthentication token;
    }
    
    private static final class VerifiedAuthentication implements Authenticator
    {
        private final UUID id;
        
        private VerifiedAuthentication()
        {
            id = UUID.randomUUID();
        }
    
        @Override
        public boolean compare(Authenticator auth)
        {
            if (auth instanceof VerifiedAuthentication)
            {
                VerifiedAuthentication token = (VerifiedAuthentication) auth;
                return this == auth && id.equals(token.id);
            }
            else 
            {
                throw new IllegalArgumentException("Improper auth implementation. Is a plugin trying to spoof OpGuard?");
            }
        }
    }
    
    @Override
    public Authenticator generateAuth()
    {
        if (AuthWrapper.token == null)
        {
            VerifiedAuthentication auth = new VerifiedAuthentication();
            AuthWrapper.token = auth;
            return auth;
        }
        else 
        {
            throw new IllegalStateException("Cannot replace singleton Authenticator. It's only generated once.");
        }
    }
    
    @Override
    public void setPassword(Authenticator auth, Password password)
    {
        if (AuthWrapper.token.compare(auth) && password != null)
        {
            PasswordWrapper.password = password;
        }
    }
    
    @Override
    public void removePassword(Authenticator auth, Password password)
    {
        if (AuthWrapper.token.compare(auth) && check(password))
        {
            PasswordWrapper.password = null;
        }
    }
    
    @Override
    public Password getPassword(Authenticator auth)
    {
        return new Password(PasswordWrapper.password);
    }
    
    public OpVerifier()
    {
        // TODO: Add all existing operators to ops list
    }
    
    @Override
    public Collection<OfflinePlayer> getVerifiedOperators()
    {
        return OpListWrapper.getCopy();
    }
    
    @Override
    public boolean check(Password password)
    {
        return PasswordWrapper.password.compare(password);
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
