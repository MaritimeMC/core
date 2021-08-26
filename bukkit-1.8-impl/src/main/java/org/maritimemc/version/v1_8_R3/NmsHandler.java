package org.maritimemc.version.v1_8_R3;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.maritimemc.abstraction.INmsHandler;

import java.lang.reflect.Field;

public class NmsHandler implements INmsHandler {

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int hold, int fadeOut) {
        title = ChatColor.translateAlternateColorCodes('&', title);
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

        JsonObject titleObj = new JsonObject();
        titleObj.addProperty("text", title);

        JsonObject subtitleObj = new JsonObject();
        subtitleObj.addProperty("text", subtitle);

        IChatBaseComponent titleChat = IChatBaseComponent.ChatSerializer.a(titleObj.toString());
        IChatBaseComponent subtitleChat = IChatBaseComponent.ChatSerializer.a(subtitleObj.toString());

        CraftPlayer cp = (CraftPlayer) player;

        PacketPlayOutTitle times = new PacketPlayOutTitle(fadeIn, hold, fadeIn);
        cp.getHandle().playerConnection.sendPacket(times);

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleChat);
        cp.getHandle().playerConnection.sendPacket(titlePacket);

        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleChat);
        cp.getHandle().playerConnection.sendPacket(subtitlePacket);
    }

    @Override
    public void sendActionBar(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        JsonObject object = new JsonObject();
        object.addProperty("text", message);

        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(object.toString()), (byte) 2);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @SneakyThrows
    @Override
    public void sendTabHeaderFooter(Player player, String header, String footer) {
        IChatBaseComponent headerJson = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + header + "\"}");
        IChatBaseComponent footerJson = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + footer + "\"}");

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(headerJson);
        Field footerField = packet.getClass().getDeclaredField("b");

        footerField.setAccessible(true);
        footerField.set(packet, footerJson);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public Sound getNotePling() {
        return Sound.NOTE_PLING;
    }

    @Override
    public void setSkullOwner(SkullMeta meta, String data) {
        meta.setOwner(data);
    }

    @Override
    public boolean usesSkullUUIDs() {
        return false;
    }

}
