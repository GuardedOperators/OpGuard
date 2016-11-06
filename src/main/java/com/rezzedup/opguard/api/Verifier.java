package com.rezzedup.opguard.api;

import com.rezzedup.opguard.Password;
import org.bukkit.OfflinePlayer;

import java.util.Collection;

public interface Verifier extends PasswordHandler, Savable
{
    Collection<OfflinePlayer> getVerifiedOperators();
    
    boolean op(OfflinePlayer player, Password password);
    
    boolean deop(OfflinePlayer player, Password password);
}
