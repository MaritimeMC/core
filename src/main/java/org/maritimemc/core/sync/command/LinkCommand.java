package org.maritimemc.core.sync.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.sync.api.ILinkManager;

import java.util.Arrays;
import java.util.List;

public class LinkCommand extends CommandBase {

    private final ILinkManager linkManager;

    public LinkCommand(String name, ILinkManager linkManager) {
        super(name);
        this.linkManager = linkManager;
        setAliases(Arrays.asList("verify", "sync"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;
        if (linkManager.isUserLinked(player.getUniqueId())) {
            player.sendMessage(Formatter.format("Link", "You are already linked to a Discord Account."));
        } else if (linkManager.isUserInCache(player.getUniqueId())) {
            player.sendMessage(Formatter.format("Link", "You have already requested a verification code. Please wait until your current code expires."));
        } else {

            String code = linkManager.generateVerificationCode();
            linkManager.insertIntoCache(player.getUniqueId(), code);
            player.sendMessage(Formatter.format("Link", "Your verification code is &a" + code + "&7. Please execute &a.link " + code + " &7in our Discord Server. This code will expire in &a10 minutes&7."));

        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
