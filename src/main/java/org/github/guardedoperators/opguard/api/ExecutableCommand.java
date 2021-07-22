package org.github.guardedoperators.opguard.api;

import org.bukkit.command.CommandSender;

public interface ExecutableCommand
{
    void execute(CommandSender sender, String[] args);
}
