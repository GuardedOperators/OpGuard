package com.rezzedup.opguard.api;

import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.UUID;

public interface Verifier extends PasswordHandler, Savable
{
    Collection<OfflinePlayer> getVerifiedOperators();
    
    Collection<UUID> getVerifiedUUIDs();
    
    boolean op(OfflinePlayer player, Password password);
    
    boolean deop(OfflinePlayer player, Password password);
}
