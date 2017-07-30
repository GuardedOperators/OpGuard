package com.rezzedup.opguard.users;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.rezzedup.opguard.files.Updatable;

public class History implements Updatable
{
    public static final JsonSerializer<History> SERIALIZER = (history, type, context) ->
    {
        return null; // TODO
    };
    
    public static final JsonDeserializer<History> DESERIALIZER = (element, type, context) ->
    {
        return null; // TODO
    };
    
    @Override
    public boolean isUpdated()
    {
        return false;
    }
}
