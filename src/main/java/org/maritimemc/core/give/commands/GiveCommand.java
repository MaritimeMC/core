package org.maritimemc.core.give.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.maritimemc.core.Formatter;
import org.maritimemc.core.command.CommandBase;
import org.maritimemc.core.give.Give;
import org.maritimemc.core.util.ItemBuilder;

import java.util.Arrays;
import java.util.List;

public class GiveCommand extends CommandBase {

    public GiveCommand(String name) {
        super(name);
        setAliases(Arrays.asList("g", "i", "item", "giveitem"));
        setRequiredPermission(Give.GivePerm.USE_GIVE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;
        if (args.length == 1) {

            Material material = Material.matchMaterial(args[0]);

            if (material == null) {
                player.sendMessage(Formatter.format("Give", "That is not a valid item name."));
                return;
            }

            player.getInventory().addItem(new ItemStack(material));
            player.sendMessage(Formatter.format("Give", "You gave yourself &a1 " + formatMaterialName(material) + "&7."));

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("all")) {

                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    player.sendMessage(Formatter.format("Give", "That is not a valid item name."));
                    return;
                }

                for (Player target : Bukkit.getOnlinePlayers()) {
                    target.getInventory().addItem(new ItemStack(material));
                }

                player.sendMessage(Formatter.format("Give","You gave &a1 " + formatMaterialName(material) + "&7 to all players."));

            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Formatter.format("Give", "That player is not online."));
                } else {

                    Material material = Material.matchMaterial(args[1]);
                    if (material == null) {
                        player.sendMessage(Formatter.format("Give", "That is not a valid item name."));
                        return;
                    }

                    target.getInventory().addItem(new ItemStack(material));
                    player.sendMessage(Formatter.format("Give", "You gave &a1 " + formatMaterialName(material) + "&7 to &a" + target.getName() + "&7."));

                }
            }


        } else if (args.length == 3) {

            if (args[0].equalsIgnoreCase("all")) {

                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    player.sendMessage(Formatter.format("Give", "That is not a valid item name."));
                    return;
                }

                try {
                    int i = Integer.parseInt(args[2]);

                    for (Player target : Bukkit.getOnlinePlayers()) {
                        target.getInventory().addItem(new ItemStack(material, i));
                    }

                    player.sendMessage(Formatter.format("Give", "You gave &a" + i + " " + formatMaterialName(material) + "&7 to all players."));
                } catch (NumberFormatException ex) {
                    player.sendMessage(Formatter.format("Give", "That is not a number."));
                }

            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Formatter.format("Give", "That player is not online."));
                } else {

                    Material material = Material.matchMaterial(args[1]);
                    if (material == null) {
                        player.sendMessage(Formatter.format("Give", "That is not a valid item name."));
                        return;
                    }

                    try {
                        int i = Integer.parseInt(args[2]);

                        target.getInventory().addItem(new ItemStack(material, i));

                        player.sendMessage(Formatter.format("Give", "You gave &a" + i + " " + formatMaterialName(material) + "&7 to &a" + target.getName() + "&7."));
                    } catch (NumberFormatException ex) {
                        player.sendMessage(Formatter.format("Give","That is not a number."));
                    }

                }
            }

        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("all")) {

                Material material = Material.matchMaterial(args[1]);
                if (material == null) {
                    player.sendMessage(Formatter.format("Give", "That is not a valid item name."));
                    return;
                }

                try {
                    int i = Integer.parseInt(args[2]);

                    ItemBuilder item = new ItemBuilder(material)
                            .amount(i);

                    String arg = args[3];

                    for (String enchKey : arg.split(",")) {
                        String[] parts = enchKey.split(":");

                        if (parts.length != 2) {
                            player.sendMessage(Formatter.format("Give", "Incorrect enchantment format."));
                            return;
                        }

                        String name = parts[0].toUpperCase();
                        String level = parts[1];

                        try {
                            Enchantment byName = Enchantment.getByName(name);
                            if (byName == null) {
                                player.sendMessage(Formatter.format("Give", "That is not a valid enchantment name."));
                                return;
                            } else {
                                try {
                                    int iLevel = Integer.parseInt(level);
                                    item.enchant(byName, iLevel, false);
                                } catch (NumberFormatException e) {
                                    player.sendMessage(Formatter.format("Give", "That is not a valid enchantment level."));
                                    return;
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(Formatter.format("Give", "That is not a valid enchantment name."));
                            return;
                        }
                    }


                    for (Player target : Bukkit.getOnlinePlayers()) {
                        target.getInventory().addItem(item.build());
                    }

                    player.sendMessage(Formatter.format("Give", "You gave &a" + i + " " + formatMaterialName(material) + "&7 to all players."));
                } catch (NumberFormatException ex) {
                    player.sendMessage(Formatter.format("Give", "That is not a number."));
                }

            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Formatter.format("Give", "That player is not online."));
                } else {

                    Material material = Material.matchMaterial(args[1]);
                    if (material == null) {
                        player.sendMessage(Formatter.format("Give", "That is not a valid item name."));
                        return;
                    }

                    try {
                        int i = Integer.parseInt(args[2]);

                        ItemBuilder item = new ItemBuilder(material)
                                .amount(i);

                        String arg = args[3];

                        for (String enchKey : arg.split(",")) {
                            String[] parts = enchKey.split(":");

                            if (parts.length != 2) {
                                player.sendMessage(Formatter.format("Give", "Incorrect enchantment format."));
                                return;
                            }

                            String name = parts[0].toUpperCase();
                            String level = parts[1];

                            try {
                                Enchantment byKey = Enchantment.getByName(name);
                                if (byKey == null) {
                                    player.sendMessage(Formatter.format("Give", "That is not a valid enchantment name."));
                                    return;
                                } else {
                                    try {
                                        int iLevel = Integer.parseInt(level);
                                        item.enchant(byKey, iLevel, false);
                                    } catch (NumberFormatException e) {
                                        player.sendMessage(Formatter.format("Give", "That is not a valid enchantment level."));
                                        return;
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(Formatter.format("Give", "That is not a valid enchantment name."));
                                return;
                            }
                        }

                        target.getInventory().addItem(item.build());

                        player.sendMessage(Formatter.format("Give", "You gave &a" + i + " " + formatMaterialName(material) + "&7 to &a" + target.getName() + "&7."));
                    } catch (NumberFormatException ex) {
                        player.sendMessage(Formatter.format("Give", "That is not a number."));
                    }

                }
            }
        } else {
            sendUsage(player);
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    private void sendUsage(Player player) {
        // /give <item> Give to yourself
        // /give <player/all> <item> Give item to players
        // /give <player/all> <item> <amount> Give an amount of an item to players
        // /give <player/all> <item> <amount> <enchant: DAMAGE_ALL:2,KNOCKBACK:5> Give an amount of an item to players with enchantments

        player.sendMessage(Formatter.format("Give", "&9/give <item> &7Gives an item to yourself."));
        player.sendMessage(Formatter.format("Give", "&9/give <player|all> <item> &7Gives an item to a specific player."));
        player.sendMessage(Formatter.format("Give", "&9/give <player|all> <item> <amount> &7Gives an amount of an item to a specific player."));
        player.sendMessage(Formatter.format("Give", "&9/give <player|all> <item> <amount> <enchants> &7Gives an amount of an item with enchants to a specific player."));
        player.sendMessage(Formatter.format("Give", "Enchantments Example: NAME:LEVEL,NAME:LEVEL"));
    }

    private String formatMaterialName(Material material) {
        String name = material.name();
        String[] words = name.split("_");

        StringBuilder full = new StringBuilder();
        for (int index = 0; index < words.length; index++) {
            String word = words[index];
            word = word.charAt(0) + word.substring(1).toLowerCase();
            full.append(word);
            if (index != words.length - 1) {
                full.append(" ");
            }
        }

        return full.toString();
    }
}
