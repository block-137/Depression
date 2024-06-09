package net.depression.fabric.client;

import net.depression.client.DepressionClient;
import net.fabricmc.api.ClientModInitializer;

public final class DepressionFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DepressionClient.onInitializeClient();
    }
}
