package com.rezzedup.opguard.config;

import com.rezzedup.opguard.Messenger;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConfigurationTemplate
{
    private final BufferedReader reader;
    private List<String> content;
    
    public ConfigurationTemplate(Object instance, String resource)
    {
        this(instance.getClass(), resource);
    }
    
    public ConfigurationTemplate(Class clazz, String resource)
    {
        reader = new BufferedReader(new InputStreamReader(clazz.getClassLoader().getResourceAsStream(resource)));
    }
    
    public List<String> apply(FileConfiguration config)
    {
        if (content != null)
        {
            return content;
        }
        
        List<String> lines = new ArrayList<>();
        
        try
        {
            lines = applyTemplate(config);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        
        return content = lines;
    }
    
    public List<String> getLines()
    {
        return content;
    }
    
    private List<String> applyTemplate(FileConfiguration config) throws IOException
    {
        List<String> lines = new ArrayList<>();
        String regex = "@\\((.+)\\)$";
        Pattern replaceable = Pattern.compile(regex);
        String line;
        
        while ((line = reader.readLine()) != null)
        {
            String pure = line;
            line = line.replaceAll("#@.*$", "");
            
            if (line.isEmpty())
            {
                if (lines.isEmpty() || !line.equals(pure)) { continue; }
            }
            
            boolean lineIsAdded = false;
            Matcher matcher = replaceable.matcher(line);
            
            if (matcher.find())
            {
                String origin = matcher.group(1);
                String[] options = origin.split(" *\\| *");
                String value = "<Template Error: No default value found>";
                
                for (String option : options)
                {
                    boolean list = option.endsWith("[]");
                    value = (list) ? option.substring(0, option.length() - 2) : option;
                    
                    if (config.get(value) != null)
                    {
                        if (!list)
                        {
                            value = sanitize(config.getString(option));
                            break;
                        }
                        
                        String start = line.replaceAll("- .*$", "") + "- "; // gets: '  - ' or '- ' etc.
                        List<String> items = config.getStringList(option);
                        
                        if (items.size() > 0)
                        {
                            for (String item : items)
                            {
                                lines.add(start + sanitize(item));
                            }
                            lineIsAdded = true;
                            break;
                        }
                    }
                    
                    Messenger.send("&cPrevious config does not contain option:&f " + value + "&r from " + matcher.group(0));
                }
                
                line = line.replaceAll(regex, value);
            }
            
            if (!lineIsAdded) 
            {
                lines.add(line); 
            }
        }
        
        reader.close();
        
        return lines;
    }
    
    private String sanitize(String value)
    {
        if (value.contains("@") || value.contains("&") || value.contains("#") || value.contains(":") || value.contains("[") || value.contains("]"))
        {
            value = value.replace("'", "''");
            value = value.replace("\n", "\\n");
            value =  "'" + value + "'";
        }
        return value;
    }
}
