package org.maritimemc.core.chat;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.maritimemc.data.perm.PermissionGroup;
import org.maritimemc.data.player.Suffix;

@Data
public class PlayerMessage {

    private final Player player;
    private final PermissionGroup prefix;
    private final Suffix suffix;
    private final String content;

}
