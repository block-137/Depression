package net.depression.listener.client;

import net.depression.client.*;
import net.depression.mental.MentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.mental.PTSDManager;
import net.depression.network.MentalTraitPacket;
import net.depression.screen.MentalTraitSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.client.multiplayer.ClientLevel;

public class ClientLifecycleEventListener {
    public static void onClientLevelLoad(ClientLevel clientLevel) {
        if (Minecraft.getInstance().level == null) {
            ClientMentalStatus clientMentalStatus = DepressionClient.clientMentalStatus;
            clientMentalStatus.reset();
            ClientPTSDManager ptsdManager = clientMentalStatus.ptsdManager;
            ptsdManager.clear();
            ClientActionbarHint clientActionbarHint = DepressionClient.clientActionbarHint;
            clientActionbarHint.clear();
            ClientDiaryUpdater.clear();
        }
    }
}
