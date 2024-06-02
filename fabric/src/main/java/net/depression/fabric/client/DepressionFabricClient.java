package net.depression.fabric.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.client.ClientMentalStatus;
import net.depression.client.ClientActionbarHint;
import net.depression.client.DepressionClient;
import net.depression.network.CloseEyePacket;
import net.depression.network.MentalStatusPacket;
import net.depression.network.ActionbarHintPacket;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public final class DepressionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DepressionClient.onInitializeClient();
    }
}
