package net.depression.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.depression.Depression;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.StandardCharsets;

public class MentalTraitPacket {
    public static final ResourceLocation MENTAL_TRAIT_PACKET = new ResourceLocation(Depression.MOD_ID, "mental_trait_packet");
    public static void sendToPlayer(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToPlayer(player, MENTAL_TRAIT_PACKET, buf);
    }

    public static void sendToServer(String id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeCharSequence(id, StandardCharsets.UTF_8);
        NetworkManager.sendToServer(MENTAL_TRAIT_PACKET, buf);
    }
}
