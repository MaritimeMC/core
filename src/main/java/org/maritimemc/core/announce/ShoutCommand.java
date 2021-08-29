package org.maritimemc.core.announce;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.util.UtilText;
import org.maritimemc.data.player.PlayerProfile;

import java.util.Collections;
import java.util.List;

public class ShoutCommand extends CommandBase {

    private final ProfileManager profileManager = Locator.locate(ProfileManager.class);

    /**
     * Class constructor
     *
     * @param name The name of this command.
     */
    public ShoutCommand(String name) {
        super(name);
        setAliases(Collections.singletonList("s"));
        setRequiredPermission(AnnouncementModule.AnnouncePerm.SHOUT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PlayerProfile profile = profileManager.getCached((Player) sender);

        String msg = UtilText.getArguments(args);

        Bukkit.broadcastMessage(Formatter.format(Formatter.toChatColor(profile.getHighestPrimaryGroup().getColour()) + "&l") + profile.getName() + " " + ChatColor.GRAY + msg);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
