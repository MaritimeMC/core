package org.maritimemc.core.suffix.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.command.CommandBase;

import java.util.Arrays;
import java.util.List;

public class CommandSuffix extends CommandBase {

    public CommandSuffix(String name) {
        super(name);
        setAliases(Arrays.asList("activatedsuffix", "changesuffix"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        new SuffixGUI().open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
