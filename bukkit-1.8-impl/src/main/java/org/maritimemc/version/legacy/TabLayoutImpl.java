package org.maritimemc.version.legacy;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.maritimemc.core.versioning.ITabLayout;

import java.lang.reflect.Field;

public class TabLayoutImpl implements ITabLayout {

    @SneakyThrows
    @Override
    public void setTabLayout(Player player, String header, String footer) {
        IChatBaseComponent headerJson = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + header + "\"}");
        IChatBaseComponent footerJson = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + footer + "\"}");

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(headerJson);
        Field footerField = packet.getClass().getDeclaredField("b");

        footerField.setAccessible(true);
        footerField.set(packet, footerJson);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
