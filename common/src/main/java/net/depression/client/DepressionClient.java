package net.depression.client;

import dev.architectury.event.events.client.*;
import dev.architectury.networking.NetworkManager;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.config.ClientConfig;
import net.depression.key.KeyMappings;
import net.depression.listener.client.ClientLifecycleEventListener;
import net.depression.listener.client.ClientRawInputEventListener;
import net.depression.listener.client.ClientTickEventListener;
import net.depression.network.*;
import net.depression.rhythmcraft.RhythmCraftProfile;
import net.depression.rhythmcraft.SongSortType;
import net.depression.screen.rhythmcraft.GameGuiRenderer;
import net.depression.screen.rhythmcraft.RCSelectionScreen;
import net.depression.util.OggStreamPlayer;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DepressionClient {
    public static boolean ENABLE_COMPUTER;
    public static final ClientMentalStatus clientMentalStatus = new ClientMentalStatus();
    public static final ClientActionbarHint clientActionbarHint = new ClientActionbarHint();
    public static RhythmCraftProfile rcProfile = new RhythmCraftProfile();
    public static OggStreamPlayer oggStreamPlayer = new OggStreamPlayer();
    public static ClientPlayingChart playingChart;
    public static void onInitializeClient() {
        KeyMappings.init();

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
                PTSDOnsetPacket.PHOTISM_PACKET, ClientPTSDManager::receivePhotismPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                MentalTraitPacket.MENTAL_TRAIT_PACKET, ClientMentalStatus::receiveMentalTraitPacket);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.PROFILE_PACKET, DepressionClient::receiveRCProfilePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.PLAY_SONG_PACKET, ClientPlayingChart::receivePlaySongPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.ACCEPT_EDIT_PACKET, RCSelectionScreen::receiveAcceptEditPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.NOTE_CHANGE_PACKET, ClientPlayingChart::receiveNoteChangePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.GAMEPLAY_CHANGE_PACKET, ClientPlayingChart::receiveGameplayChangePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.TIME_CHANGE_PACKET, ClientPlayingChart::receiveTimeChangePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.GAME_END_PACKET, ClientPlayingChart::receiveGameEndPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C,
                RhythmCraftPacket.SPACE_CHANGE_PACKET, ClientPlayingChart::receiveSpaceChangePacket);

        ClientGuiEvent.RENDER_HUD.register(clientMentalStatus::renderHud);
        ClientGuiEvent.RENDER_HUD.register(GameGuiRenderer::renderHud);
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(ClientLifecycleEventListener::onClientLevelLoad);
        ClientLifecycleEvent.CLIENT_STOPPING.register(ClientLifecycleEventListener::onClientStopping);
        ClientRawInputEvent.MOUSE_SCROLLED.register(ClientRawInputEventListener::onMouseScrolled);
        ClientRawInputEvent.MOUSE_CLICKED_PRE.register(ClientRawInputEventListener::onMouseClicked);
        ClientTickEvent.CLIENT_LEVEL_PRE.register(ClientTickEventListener::onClientLevelTick);
        ClientTickEvent.CLIENT_PRE.register(ClientTickEventListener::onClientTick);
        ClientConfig.load();
    }

    public static void receiveRCProfilePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        int stringSize = buf.readInt();
        rcProfile.sortType = SongSortType.valueOf(buf.readCharSequence(stringSize, StandardCharsets.UTF_8).toString());
        rcProfile.difficulty = buf.readInt();
        rcProfile.index = buf.readInt();
        while (buf.isReadable()) {
            stringSize = buf.readInt();
            String key = buf.readCharSequence(stringSize, StandardCharsets.UTF_8).toString();
            int size = buf.readInt();
            ArrayList<Integer> scores = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                scores.add(buf.readInt());
            }
            rcProfile.chartScores.put(key, scores);
        }
    }
}
