package net.depression.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.depression.Depression;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PTSDOnsetPacket {
    public static final ResourceLocation PTSD_ONSET_PACKET = new ResourceLocation(Depression.MOD_ID, "ptsd_onset_packet");
    public static final ResourceLocation PHONISM_PACKET = new ResourceLocation(Depression.MOD_ID, "phonism_packet");
    public static void sendToPlayer(ServerPlayer player, int onsetLevel, double distance) {
        FriendlyByteBuf ptsdBuf = new FriendlyByteBuf(Unpooled.buffer());
        ptsdBuf.writeInt(onsetLevel);
        ptsdBuf.writeDouble(distance);
        NetworkManager.sendToPlayer(player, PTSD_ONSET_PACKET, ptsdBuf);
    }

    public static void sendPhotismPacket(ServerPlayer player, String id) {
        FriendlyByteBuf photismBuf = new FriendlyByteBuf(Unpooled.buffer());
        photismBuf.writeUtf(id);
        NetworkManager.sendToPlayer(player, PHONISM_PACKET, photismBuf);
    }
}
