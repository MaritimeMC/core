package org.maritimemc.core.info;

import org.maritimemc.core.Module;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.info.command.DiscordCommand;
import org.maritimemc.core.info.command.ForumsCommand;
import org.maritimemc.core.info.command.IpCommand;
import org.maritimemc.core.info.command.StoreCommand;
import org.maritimemc.core.service.Locator;

/**
 * Module for basic information output commands.
 */
public class InfoCommandModule implements Module {

    public InfoCommandModule() {
        CommandCenter commandCenter = Locator.locate(CommandCenter.class);

        commandCenter.register(
                new DiscordCommand("discord"),
                new ForumsCommand("forums"),
                new IpCommand("ip"),
                new StoreCommand("store")
        );
    }
}
