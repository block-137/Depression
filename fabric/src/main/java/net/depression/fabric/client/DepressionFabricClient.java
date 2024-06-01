package net.depression.fabric.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.client.ClientMentalStatus;
import net.depression.client.ClientActionbarHint;
import net.depression.network.CloseEyePacket;
import net.depression.network.MentalStatusPacket;
import net.depression.network.ActionbarHintPacket;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public final class DepressionFabricClient implements ClientModInitializer {
    public static final ClientMentalStatus clientMentalStatus = new ClientMentalStatus();
    public static final ClientActionbarHint clientActionbarHint = new ClientActionbarHint();
    @Override
    public void onInitializeClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalStatusPacket.EMOTION_PACKET, clientMentalStatus::receiveEmotionPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalStatusPacket.MENTAL_HEALTH_PACKET, clientMentalStatus::receiveMentalHealthPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.PTSD_FROM_PACKET, clientActionbarHint::receivePTSDFormPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.PTSD_DISPERSE_PACKET, clientActionbarHint::receivePTSDDispersePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.INSOMNIA_PACKET, clientActionbarHint::receiveInsomniaPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.MENTAL_FATIGUE_PACKET, clientActionbarHint::receiveMentalFatiguePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                CloseEyePacket.CLOSE_EYE_PACKET, clientMentalStatus.mentalIllness::receiveCloseEyePacket);
        ClientGuiEvent.RENDER_HUD.register(clientMentalStatus::renderHud);
    }
}
