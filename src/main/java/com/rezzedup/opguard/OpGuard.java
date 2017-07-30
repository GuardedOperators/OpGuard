package com.rezzedup.opguard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rezzedup.opguard.users.History;
import com.rezzedup.opguard.users.User;
import com.rezzedup.opguard.util.Version;
import org.bukkit.plugin.java.JavaPlugin;

public class OpGuard extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        Version java = Version.from(System.getProperty("java.specification.version"));
    
        if (!java.isAtLeast(1,8))
        {
            // The server isn't running Java 8 (obviously), so we can't use a fancy lambda-ified runnable.
            getServer().getScheduler().runTask(this, new Runnable() {
                @Override
                public void run()
                {
                    getLogger().warning("OpGuard requires Java 8, but this server currently runs Java " + java.getMinor());
                    getServer().getPluginManager().disablePlugin(OpGuard.this);
                }
            });
        
            return;
        }
    
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(User.class, User.SERIALIZER)
            .registerTypeAdapter(User.class, User.DESERIALIZER)
            .registerTypeAdapter(History.class, History.SERIALIZER)
            .registerTypeAdapter(History.class, History.DESERIALIZER)
            .create();
    }
}
