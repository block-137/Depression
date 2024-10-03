package net.depression.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.depression.Depression;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ActionbarHintPacket {
    public static final ResourceLocation PTSD_FROM_PACKET = new ResourceLocation(Depression.MOD_ID, "ptsd_form_packet");
    public static final ResourceLocation PTSD_DISPERSE_PACKET = new ResourceLocation(Depression.MOD_ID, "ptsd_disperse_packet");
    public static final ResourceLocation INSOMNIA_PACKET = new ResourceLocation(Depression.MOD_ID, "insomnia_packet");
    public static final ResourceLocation MENTAL_FATIGUE_PACKET = new ResourceLocation(Depression.MOD_ID, "mental_fatigue_packet");
    public static final ResourceLocation NEARBY_BLOCK_HEAL_PACKET = new ResourceLocation(Depression.MOD_ID, "nearby_block_heal_packet");
    public static final ResourceLocation KILL_ENTITY_HEAL_PACKET = new ResourceLocation(Depression.MOD_ID, "kill_entity_heal_packet");
    public static final ResourceLocation BREAK_BLOCK_HEAL_PACKET = new ResourceLocation(Depression.MOD_ID, "break_block_heal_packet");

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void sendNearbyBlockHealPacket(ServerPlayer player, Component id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeComponent(id);
        NetworkManager.sendToPlayer(player, NEARBY_BLOCK_HEAL_PACKET, buf);
    }
    public static void sendKillEntityHealPacket(ServerPlayer player, Component id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeComponent(id);
        NetworkManager.sendToPlayer(player, KILL_ENTITY_HEAL_PACKET, buf);
    }
    public static void sendBreakBlockHealPacket(ServerPlayer player, Component id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeComponent(id);
        NetworkManager.sendToPlayer(player, BREAK_BLOCK_HEAL_PACKET, buf);
    }
    public static void sendPTSDFormPacket(ServerPlayer player, Component id) {
        FriendlyByteBuf formBuf = new FriendlyByteBuf(Unpooled.buffer());
        formBuf.writeComponent(id);
        NetworkManager.sendToPlayer(player, PTSD_FROM_PACKET, formBuf);
    }
    public static void sendPTSDDispersePacket(ServerPlayer player, Component id) {
        FriendlyByteBuf disperseBuf = new FriendlyByteBuf(Unpooled.buffer());
        disperseBuf.writeComponent(id);
        NetworkManager.sendToPlayer(player, PTSD_DISPERSE_PACKET, disperseBuf);
    }
    public static void sendInsomniaPacket(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToPlayer(player, INSOMNIA_PACKET, buf);
    }
    public static void sendMentalFatiguePacket(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToPlayer(player, MENTAL_FATIGUE_PACKET, buf);
    }
}
