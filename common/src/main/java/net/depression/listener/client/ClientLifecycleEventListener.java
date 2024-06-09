package net.depression.listener.client;

import net.depression.client.ClientMentalStatus;
import net.minecraft.client.multiplayer.ClientLevel;

public class ClientLifecycleEventListener {
    public static void onClientLevelLoad(ClientLevel clientLevel) {
        ClientMentalStatus.isJoinGame = true;
    }
}
