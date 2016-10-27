package com.rezzedup.opguard.api;

import com.rezzedup.opguard.Password;
import com.rezzedup.opguard.wrapper.GuardedPlayer;
import org.bukkit.OfflinePlayer;

import java.util.Collection;

public interface Verifier extends PasswordHandler, Savable
{
    public Collection<OfflinePlayer> getVerifiedOperators();
    
    public boolean op(OfflinePlayer player, Password password);
    
    public boolean deop(OfflinePlayer player, Password password);
}
