package net.depression.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.depression.Depression;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CloseEyePacket {
    public static final ResourceLocation CLOSE_EYE_PACKET = new ResourceLocation(Depression.MOD_ID, "close_eye_packet");
    public static void sendToPlayer(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToPlayer(player, CLOSE_EYE_PACKET, buf);
    }
}
