package com.github.guardedoperators.opguard;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

final class VerifyOpListTask extends BukkitRunnable
{
    private final OpGuard api;
    
    public VerifyOpListTask(OpGuard api)
    {
        this.api = api;
    
        long interval = api.config().getOpListInspectionInterval();
    
        if (interval <= 0)
        {
            Messenger.console("[OpGuard] Invalid inspection interval " + interval + ". Defaulting to 4 ticks.");
            interval = 4;
        }
    
        runTaskTimer(api.plugin(), 1L, interval);
    }
    
    @Override
    public void run()
    {
        OpVerifier verifier = api.verifier();
        Set<OfflinePlayer> operators = Bukkit.getOperators();
        
        for (OfflinePlayer operator : operators)
        {
            if (!verifier.isVerified(operator))
            {
                String name = operator.getName();
                operator.setOp(false);
                Context context = new Context(api).pluginAttempt().setOp().warning
                (
                    "An unknown plugin attempted to op <!>" + name + "&f. A recently-installed plugin may be to blame"
                );
                api.warn(context).log(context).punish(context, name);
            }
        }
    }
}
