package org.maritimemc.core.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.maritimemc.data.perm.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command.
 */
public abstract class CommandBase {

    @Getter
    private final String name;

    @Getter @Setter
    private List<String> aliases = new ArrayList<>();

    @Getter @Setter
    private Permission requiredPermission;

    @Getter @Setter
    private boolean consoleExecutable = false;

    @Getter @Setter
    private boolean executeAsync = false;

    /**
     * Class constructor
     * @param name The name of this command.
     */
    public CommandBase(String name) {
        this.name = name;
    }

    public abstract void execute(CommandSender sender, String[] args);
    public abstract List<String> tabComplete(CommandSender sender, String[] args);
}
