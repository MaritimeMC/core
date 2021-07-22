package org.maritimemc.core.service;

import org.maritimemc.core.announce.AnnouncementModule;
import org.maritimemc.core.builder.BuilderModule;
import org.maritimemc.core.chat.Chat;
import org.maritimemc.core.chatlog.ChatLogModule;
import org.maritimemc.core.command.CommandCenter;
import org.maritimemc.core.currency.CurrencyModule;
import org.maritimemc.core.db.RedisModule;
import org.maritimemc.core.db.SqlModule;
import org.maritimemc.core.db.messaging.DatabaseMessageManager;
import org.maritimemc.core.give.Give;
import org.maritimemc.core.info.InfoCommandModule;
import org.maritimemc.core.menu.MenuManager;
import org.maritimemc.core.message.MessageManager;
import org.maritimemc.core.perm.PermissionManager;
import org.maritimemc.core.perm.RemotePermissionManager;
import org.maritimemc.core.perm.group.GroupManagementModule;
import org.maritimemc.core.profile.ProfileManager;
import org.maritimemc.core.punish.Punish;
import org.maritimemc.core.reports.Reports;
import org.maritimemc.core.server.BungeeInfoModule;
import org.maritimemc.core.server.ServerDataManager;
import org.maritimemc.core.store.StoreModule;
import org.maritimemc.core.suffix.SuffixManager;
import org.maritimemc.core.sync.DiscordSyncModule;
import org.maritimemc.core.twofactor.TwoFactor;
import org.maritimemc.core.vanish.VanishManager;

import static org.maritimemc.core.service.Locator.locate;

public class ModuleManager {

    public static void loadRequiredModules() {
        locate(MenuManager.class);

        locate(SqlModule.class);
        locate(RedisModule.class);
        locate(DatabaseMessageManager.class).listen();

        locate(RemotePermissionManager.class);
        locate(PermissionManager.class);
        locate(ServerDataManager.class);

        locate(ProfileManager.class);
        locate(CommandCenter.class);

        locate(GroupManagementModule.class);
        locate(SuffixManager.class);

        locate(InfoCommandModule.class);

        locate(StoreModule.class);
        locate(Chat.class);
        locate(ChatLogModule.class);

        locate(VanishManager.class);
        locate(TwoFactor.class);
        locate(DiscordSyncModule.class);

        locate(Reports.class);
        locate(Punish.class);
        locate(MessageManager.class);

        locate(Give.class);
        locate(BuilderModule.class);

        locate(CurrencyModule.class);
        locate(AnnouncementModule.class);

        locate(BungeeInfoModule.class);
    }
}
