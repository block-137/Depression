package net.depression.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.config.ClientConfig;
import net.depression.listener.client.ClientLifecycleEventListener;
import net.depression.listener.client.ClientRawInputEventListener;
import net.depression.network.*;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class DepressionClient {
    public static final ClientMentalStatus clientMentalStatus = new ClientMentalStatus();
    public static final ClientActionbarHint clientActionbarHint = new ClientActionbarHint();
    public static void onInitializeClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalStatusPacket.EMOTION_PACKET, clientMentalStatus::receiveEmotionPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalStatusPacket.MENTAL_HEALTH_PACKET, clientMentalStatus::receiveMentalHealthPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,

                ActionbarHintPacket.OVERDOSE_PACKET, clientActionbarHint::receiveOverdosePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.BIPOLAR_PACKET, clientActionbarHint::receiveBipolarPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,

                ActionbarHintPacket.NEARBY_BLOCK_HEAL_PACKET, clientActionbarHint::receiveNearbyBlockHealPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.BREAK_BLOCK_HEAL_PACKET, clientActionbarHint::receiveBreakBlockHealPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.KILL_ENTITY_HEAL_PACKET, clientActionbarHint::receiveKillEntityHealPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.FISH_HEAL_PACKET, clientActionbarHint::receiveFishHealPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.FEED_ANIMAL_HEAL_PACKET, clientActionbarHint::receiveFeedAnimalHealPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.PET_HEAL_PACKET, clientActionbarHint::receivePetHealPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.LOOT_HEAL_PACKET, clientActionbarHint::receiveLootHealPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,

                ActionbarHintPacket.PTSD_FROM_PACKET, clientActionbarHint::receivePTSDFormPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.PTSD_DISPERSE_PACKET, clientActionbarHint::receivePTSDDispersePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.PTSD_REMISSION_PACKET, clientActionbarHint::receivePTSDRemissionPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,

                ActionbarHintPacket.INSOMNIA_PACKET, clientActionbarHint::receiveInsomniaPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                ActionbarHintPacket.MENTAL_FATIGUE_PACKET, clientActionbarHint::receiveMentalFatiguePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                CloseEyePacket.CLOSE_EYE_PACKET, clientMentalStatus.mentalIllness::receiveCloseEyePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                PTSDOnsetPacket.PTSD_ONSET_PACKET, clientMentalStatus.ptsdManager::receivePTSDOnsetPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                DiaryUpdatePacket.DIARY_UPDATE_PACKET, ClientDiaryUpdater::receiveDiaryUpdatePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                PTSDOnsetPacket.PHONISM_PACKET, ClientPTSDManager::receivePhotismPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalTraitPacket.MENTAL_TRAIT_PACKET, ClientMentalStatus::receiveMentalTraitPacket);

        ClientGuiEvent.RENDER_HUD.register(clientMentalStatus::renderHud);
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(ClientLifecycleEventListener::onClientLevelLoad);
        ClientRawInputEvent.MOUSE_SCROLLED.register(ClientRawInputEventListener::onMouseScrolled);
        ClientRawInputEvent.MOUSE_CLICKED_PRE.register(ClientRawInputEventListener::onMouseClicked);
        ClientConfig.load();
    }
}
