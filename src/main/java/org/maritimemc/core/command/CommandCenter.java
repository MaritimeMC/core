package org.maritimemc.core.command;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maritimemc.core.Messages;
import org.maritimemc.core.Module;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.thread.ThreadPool;

import java.lang.reflect.Field;
import java.util.List;

import static org.maritimemc.core.service.Locator.locate;

public class CommandCenter implements Module {

    private final ProfileManager profileManager =
            locate(ProfileManager.class);

    private final PermissionManager permissionManager =
            locate(PermissionManager.class);

    public CommandCenter() {
        // Load command restrictions
        locate(BlockCommands.class);
    }

    /**
     * Registers a command through reflection into Bukkit's command map.
     * @param commandBase The command to register.
     */
    @SneakyThrows
    private void register(CommandBase commandBase) {
        Server server = Bukkit.getServer();
        Field field = server.getClass().getDeclaredField("commandMap");
        field.setAccessible(true);

        CommandMap commandMap = (CommandMap) field.get(server);

        Command command = new Command(commandBase.getName()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {

                if (sender instanceof Player) {
                    if (commandBase.getRequiredPermission() != null) {
                        if (!permissionManager.hasPermission(
                                profileManager.getCached((Player) sender),
                                commandBase.getRequiredPermission()
                        )) {
                            sender.sendMessage(Messages.NO_PERMISSION);
                            return true;
                        }
                    }
                } else {
                    if (!commandBase.isConsoleExecutable()) {
                        sender.sendMessage(Messages.CONSOLE_DISABLED);
                        return true;
                    }
                }

                if (commandBase.isExecuteAsync()) {
                    ThreadPool.ASYNC_POOL.execute(() -> commandBase.execute(sender, args));
                } else {
                    commandBase.execute(sender, args);
                }
                return true;
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
                if (sender instanceof Player) {

                    if (commandBase.getRequiredPermission() != null) {
                        if (!permissionManager.hasPermission(
                                profileManager.getCached((Player) sender),
                                commandBase.getRequiredPermission()
                        )) {
                            return null;
                        }
                    }
                }

                return commandBase.tabComplete(sender, args);
            }
        };

        command.setAliases(commandBase.getAliases());
        commandMap.register("maritime", command);
    }

    public void register(CommandBase... commandBases) {
        for (CommandBase commandBase : commandBases) {
            register(commandBase);
        }
    }
}
