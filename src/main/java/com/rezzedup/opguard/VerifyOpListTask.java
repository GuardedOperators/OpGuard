package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Verifier;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

final class VerifyOpListTask extends BukkitRunnable
{
    private final OpGuardAPI api;
    
    public VerifyOpListTask(OpGuardAPI api)
    {
        this.api = api;
    }
    
    @Override
    public void run()
    {
        Verifier verifier = api.getVerifier();
        Set<OfflinePlayer> operators = Bukkit.getOperators();
        
        for (OfflinePlayer operator : operators)
        {
            if (!verifier.isVerified(operator))
            {
                String name = operator.getName();
                operator.setOp(false);
                Context context = new Context(api).pluginAttempt().setOp().warning
                (
                    "An unknown plugin attempted to op <!>" + name + "&f. A recently-installed plugin may be to blame."
                );
                api.warn(context).log(context).punish(context, name);
            }
        }
    }
}
