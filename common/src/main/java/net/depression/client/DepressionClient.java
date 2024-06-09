package net.depression.client;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.listener.client.ClientLifecycleEventListener;
import net.depression.listener.client.ClientRawInputEventListener;
import net.depression.network.ActionbarHintPacket;
import net.depression.network.CloseEyePacket;
import net.depression.network.DiaryUpdatePacket;
import net.depression.network.MentalStatusPacket;

public class DepressionClient {
    public static final ClientMentalStatus clientMentalStatus = new ClientMentalStatus();
    public static final ClientActionbarHint clientActionbarHint = new ClientActionbarHint();
    public static void onInitializeClient() {
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
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                DiaryUpdatePacket.DIARY_UPDATE_PACKET, ClientDiaryUpdater::receiveDiaryUpdatePacket);
        ClientGuiEvent.RENDER_HUD.register(clientMentalStatus::renderHud);
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(ClientLifecycleEventListener::onClientLevelLoad);
        ClientRawInputEvent.MOUSE_SCROLLED.register(ClientRawInputEventListener::onMouseScrolled);
        ClientRawInputEvent.MOUSE_CLICKED_PRE.register(ClientRawInputEventListener::onMouseClicked);
    }
}
