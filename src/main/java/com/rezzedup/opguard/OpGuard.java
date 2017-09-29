package com.rezzedup.opguard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rezzedup.opguard.users.History;
import com.rezzedup.opguard.users.User;
import com.rezzedup.opguard.util.Version;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpGuard extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        Version java = Version.from(System.getProperty("java.specification.version"));
    
        if (!java.isAtLeast(1,8))
        {
            getLogger().severe("OpGuard requires Java 8, but this server currently runs Java " + java.getMinor());
            getLogger().severe("Disabling OpGuard...");
            setEnabled(false);
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
