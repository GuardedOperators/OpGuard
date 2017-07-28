package com.rezzedup.opguard.users;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.rezzedup.opguard.files.Updatable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class User implements Updatable
{
    public static final JsonSerializer<User> SERIALIZER = (user, type, context) ->
    {
        JsonObject json = new JsonObject();
        
        json.addProperty("uuid", user.uuid.toString());
        json.addProperty("name", user.name);
        // json.add("history", History.);
        
        return json;
    };
    
    public static final JsonDeserializer<User> DESERIALIZER = (element, type, context) ->
    {
        if (!element.isJsonObject())
        {
            throw new IllegalArgumentException("Expected JsonObject but received " + element.getClass().getCanonicalName());
        }
        
        JsonObject json = (JsonObject) element;
    
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String name = json.get("name").getAsString();
        History history = null; // TODO deserialize history
        
        return new User(uuid, name, history);
    };
    
    private final UUID uuid;
    private final History history;
    
    private String name;
    
    private boolean isUpdated = false;
    
    public User(UUID uuid, String name, History history)
    {
        this.uuid = uuid;
        this.name = name;
        this.history = history;
    }
    
    public UUID getUuid()
    {
        return this.uuid;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public OfflinePlayer getAsPlayer()
    {
        return Bukkit.getOfflinePlayer(this.uuid);
    }
    
    public void updateName(String name)
    {
        this.isUpdated = true;
        this.name = name;
    }
    
    @Override
    public boolean isUpdated()
    {
        return this.isUpdated || history.isUpdated();
    }
}
