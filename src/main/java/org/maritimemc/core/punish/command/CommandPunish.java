package org.maritimemc.core.punish.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.punish.api.exception.PlayerExemptException;
import org.maritimemc.core.punish.ui.preset.PunishGUIPreset;
import org.maritimemc.core.punish.ui.specified.PunishGUISpecified;
import org.maritimemc.core.service.Locator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandPunish extends CommandBase {

    private final Punish punish;

    public CommandPunish(String name, Punish punish) {
        super(name);
        this.punish = punish;
        setRequiredPermission(Punish.PunishPerm.PUNISH_COMMAND);
        setAliases(Collections.singletonList("p"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Formatter.format("Punish", "&a/punish <player> &7Punishes a player with preset reasons and durations."));
            sender.sendMessage(Formatter.format("Punish", "&a/punish <player> <reason> &7Punishes a player with a specified reason."));
        } else if (args.length == 1) {

            String name = args[0];
            try {
                PunishGUIPreset punishGUIPreset = new PunishGUIPreset((Player) sender, name, punish);
                punishGUIPreset.open();
            } catch (IllegalArgumentException exception) {
                sender.sendMessage(Formatter.format("Punish", "That player name is not valid."));
            } catch (PlayerExemptException exception) {
                sender.sendMessage(Formatter.format("Punish", "That player is exempt from punishment."));
            }

        } else {

            StringBuilder reason = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i == args.length - 1) {
                    reason.append(args[i]);
                } else {
                    reason.append(args[i]).append(" ");
                }
            }

            String name = args[0];
            try {
                PunishGUISpecified punishGUISpecified = new PunishGUISpecified((Player) sender, name, reason.toString(), punish);
                punishGUISpecified.open();
            } catch (IllegalArgumentException exception) {
                sender.sendMessage(Formatter.format("Punish", "That player name is not valid."));
            } catch (PlayerExemptException exception) {
                sender.sendMessage(Formatter.format("Punish", "That player is exempt from punishment."));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        return null;
    }
}
