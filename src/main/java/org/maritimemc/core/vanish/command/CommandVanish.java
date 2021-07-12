package org.maritimemc.core.vanish.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.service.Locator;
import org.maritimemc.core.vanish.VanishManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandVanish extends CommandBase {

    private final VanishManager vanishManager;

    /**
     * Class constructor
     *
     * @param name The name of this command.
     * @param vanishManager
     */
    public CommandVanish(String name, VanishManager vanishManager) {
        super(name);
        setAliases(Arrays.asList("incognito", "v"));
        setRequiredPermission(VanishManager.VanishPerm.USE_VANISH);

        this.vanishManager = vanishManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (vanishManager.isVanished(player)) {
            vanishManager.disableVanish(player);
        } else {
            vanishManager.enableVanish(player);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
