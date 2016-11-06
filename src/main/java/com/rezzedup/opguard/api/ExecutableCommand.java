package com.rezzedup.opguard.api;

import org.bukkit.command.CommandSender;

public interface ExecutableCommand
{
    void execute(CommandSender sender, String[] args);
}
