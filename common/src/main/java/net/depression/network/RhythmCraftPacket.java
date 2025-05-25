package net.depression.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.depression.Depression;
import net.depression.rhythmcraft.ProfileDataType;
import net.depression.rhythmcraft.RhythmCraftProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RhythmCraftPacket {
    public static final ResourceLocation READ_CHART_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_read_chart_packet");
    public static final ResourceLocation READY_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_ready_packet");
    public static final ResourceLocation PLAY_SONG_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_play_song_packet");
    public static final ResourceLocation PROFILE_REQUEST_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_profile_request_packet");
    public static final ResourceLocation PROFILE_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_profile_packet");
    public static final ResourceLocation ACCEPT_EDIT_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_accept_edit_packet");
    public static final ResourceLocation NOTE_CHANGE_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_note_change_packet");
    public static final ResourceLocation PAUSE_CHANGE_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_pause_packet");
    public static final ResourceLocation PROGRESS_CHANGE_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_progress_change_packet");
    public static final ResourceLocation GAMEPLAY_CHANGE_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_gameplay_change_packet");
    public static final ResourceLocation SPACE_CHANGE_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_space_change_packet");
    public static final ResourceLocation TIME_CHANGE_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_time_change_packet");
    public static final ResourceLocation GAME_END_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_game_end_packet");
    public static final ResourceLocation LOAD_BACK_PACKET = new ResourceLocation(Depression.MOD_ID, "rc_load_back_packet");

    public static void sendReadChart(String songId, int difficulty, boolean isEditMode) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(songId.length());
        buf.writeCharSequence(songId, StandardCharsets.UTF_8);
        buf.writeInt(difficulty);
        buf.writeBoolean(isEditMode);
        NetworkManager.sendToServer(READ_CHART_PACKET, buf);
    }
    public static void sendReady() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToServer(READY_PACKET, buf);
    }
    public static void sendPlaySong(ServerPlayer player, String id) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeCharSequence(id, StandardCharsets.UTF_8);
        NetworkManager.sendToPlayer(player, PLAY_SONG_PACKET, buf);
    }
    public static void sendProfileRequest() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToServer(PROFILE_REQUEST_PACKET, buf);
    }
    public static void sendProfileS2C(RhythmCraftProfile profile, ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(profile.sortType.name().length());
        buf.writeCharSequence(profile.sortType.name(), StandardCharsets.UTF_8);
        buf.writeInt(profile.difficulty);
        buf.writeInt(profile.index);
        for (String key : profile.chartScores.keySet()) {
            buf.writeInt(key.length());
            buf.writeCharSequence(key, StandardCharsets.UTF_8);
            ArrayList<Integer> scores = profile.chartScores.get(key);
            buf.writeInt(scores.size());
            for (int score : scores) {
                buf.writeInt(score);
            }
        }
        NetworkManager.sendToPlayer(player, PROFILE_PACKET, buf);
    }
    public static void sendProfileUpdateC2S(RhythmCraftProfile profile, ProfileDataType type) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(type.name().length());
        buf.writeCharSequence(type.name(), StandardCharsets.UTF_8);
        switch (type) {
            case SORT_TYPE:
                buf.writeInt(profile.sortType.name().length());
                buf.writeCharSequence(profile.sortType.name(), StandardCharsets.UTF_8);
                break;
            case DIFFICULTY:
                buf.writeInt(profile.difficulty);
                break;
            case INDEX:
                buf.writeInt(profile.index);
                break;
        }
        NetworkManager.sendToServer(PROFILE_PACKET, buf);
    }
    public static void sendAcceptEdit(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToPlayer(player, ACCEPT_EDIT_PACKET, buf);
    }

    public static void sendNoteChange(ServerPlayer player, BlockPos pos, boolean isAdd) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeBoolean(isAdd);
        NetworkManager.sendToPlayer(player, NOTE_CHANGE_PACKET, buf);
    }
    public static void sendPauseChange(long tick) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeLong(tick);
        NetworkManager.sendToServer(PAUSE_CHANGE_PACKET, buf);
    }
    public static void sendProgressChange(long tick) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeLong(tick);
        NetworkManager.sendToServer(PROGRESS_CHANGE_PACKET, buf);
    }
    public static void sendTimeChange(ServerPlayer player, long tick) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeLong(tick);
        NetworkManager.sendToPlayer(player, TIME_CHANGE_PACKET, buf);
    }
    public static void sendGameplayChange(ServerPlayer player, int score, int combo) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(score);
        buf.writeInt(combo);
        NetworkManager.sendToPlayer(player, GAMEPLAY_CHANGE_PACKET, buf);
    }

    public static void sendGameEnd(ServerPlayer player, int score, int hits, int prevBest) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(score);
        buf.writeInt(hits);
        buf.writeInt(prevBest);
        NetworkManager.sendToPlayer(player, GAME_END_PACKET, buf);
    }

    public static void sendLoadBack() {
        NetworkManager.sendToServer(LOAD_BACK_PACKET, new FriendlyByteBuf(Unpooled.buffer()));
    }

    public static void sendSpaceChange(ServerPlayer player, int space) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(space);
        NetworkManager.sendToPlayer(player, SPACE_CHANGE_PACKET, buf);
    }
}
