package org.maritimemc.core.suffix.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.suffix.SuffixManager;

import java.util.Arrays;
import java.util.List;

public class CommandSuffix extends CommandBase {

    private final SuffixManager suffixManager;

    public CommandSuffix(String name, SuffixManager suffixManager) {
        super(name);
        this.suffixManager = suffixManager;
        setAliases(Arrays.asList("activatedsuffix", "changesuffix"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        new SuffixGUI(suffixManager).open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
