/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2021 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.guardedoperators.opguard.config;

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
    
    public ConfigurationTemplate(Class<?> clazz, String resource)
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
                    if (!option.endsWith("[]"))
                    {
                        if (config.contains(option))
                        {
                            value = sanitize(config.getString(option));
                            break;
                        }
                    }
                    else
                    {
                        // Removing [] from option.
                        List<String> items = config.getStringList(option.substring(0, option.length() - 2));
    
                        if (items.size() > 0)
                        {
                            // gets: '  - ' or '- ' etc.
                            String start = line.replaceAll("- .*$", "") + "- "; 
                            
                            for (String item : items)
                            {
                                lines.add(start + sanitize(item));
                            }
                            lineIsAdded = true;
                            break;
                        }
                    }
                    value = option; // Default value. (Logic above has not broken away yet)
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
        if (value.contains("@") || value.contains("&") || value.contains("#") || value.contains(":"))
        {
            value = value.replace("'", "''");
            value = value.replace("\n", "\\n");
            value =  "'" + value + "'";
        }
        return value;
    }
}
