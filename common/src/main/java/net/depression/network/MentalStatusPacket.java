package net.depression.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.depression.Depression;
import net.depression.mental.MentalStatus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class MentalStatusPacket {
    public static final ResourceLocation EMOTION_PACKET = new ResourceLocation(Depression.MOD_ID, "emotion_packet");
    public static final ResourceLocation MENTAL_HEALTH_PACKET = new ResourceLocation(Depression.MOD_ID, "mental_health_packet");
    public static void sendToPlayer(ServerPlayer player, MentalStatus mentalStatus) {
        FriendlyByteBuf emotionBuf = new FriendlyByteBuf(Unpooled.buffer());
        emotionBuf.writeDouble(mentalStatus.emotionValue);
        FriendlyByteBuf depressionBuf = new FriendlyByteBuf(Unpooled.buffer());
        depressionBuf.writeDouble(mentalStatus.mentalHealthValue);
        NetworkManager.sendToPlayer(player, EMOTION_PACKET, emotionBuf);
        NetworkManager.sendToPlayer(player, MENTAL_HEALTH_PACKET, depressionBuf);
    }
}
