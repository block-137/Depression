package net.depression.server;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import net.depression.mental.MentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.network.DiaryUpdatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Registry {

    public static final HashMap<UUID, MentalStatus> mentalStatus = new HashMap<>();
    public static final HashSet<UUID> quitPlayers = new HashSet<>();
    public static final HashMap<UUID, StatManager> statManager = new HashMap<>();
    private static final HashMap<UUID, ItemStack> diaryUpdateMap = new HashMap<>();
    private static final LinkedHashMap<UUID, ServerLevel> pendingLevels = new LinkedHashMap<>();
    private static final LinkedHashMap<UUID, ServerPlayer> pendingPlayers = new LinkedHashMap<>();
    private static final LinkedHashMap<UUID, ChunkMap> pendingChunkMaps = new LinkedHashMap<>();

    public static void addPendingPlayer(ServerLevel serverLevel, ServerPlayer player) {
        pendingLevels.put(player.getUUID(), serverLevel);
        pendingPlayers.put(player.getUUID(), player);
        while (pendingLevels.size() > 100) { // 防止服务器被假人攻击
            pendingLevels.remove(pendingLevels.keySet().iterator().next());
        }
        while (pendingPlayers.size() > 100) {
            pendingPlayers.remove(pendingPlayers.keySet().iterator().next());
        }
    }
    public static void addPendingChunkMap(ServerPlayer player, ChunkMap chunkMap) {
        pendingChunkMaps.put(player.getUUID(), chunkMap);
        while (pendingChunkMaps.size() > 100) {
            pendingChunkMaps.remove(pendingChunkMaps.keySet().iterator().next());
        }
    }
    public static boolean isPending(ServerPlayer player) {
        return pendingPlayers.containsKey(player.getUUID());
    }
    public static void receiveMentalTraitPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        String id = buf.readCharSequence(buf.readableBytes(), DiaryUpdatePacket.charset).toString();
        UUID uuid = packetContext.getPlayer().getUUID();
        MentalStatus mentalStatus = Registry.mentalStatus.get(uuid);
        mentalStatus.loadMentalTrait(MentalTrait.byId(id));
        if (pendingChunkMaps.containsKey(uuid)) {
            pendingChunkMaps.get(uuid).move(pendingPlayers.get(uuid));
            pendingChunkMaps.remove(uuid);
        }
        pendingLevels.get(uuid).addNewPlayer(pendingPlayers.get(uuid));
        pendingPlayers.remove(uuid);
        pendingLevels.remove(uuid);
    }
    public static void diaryUpdate(ServerPlayer player, ItemStack diary) {
        diaryUpdateMap.put(player.getUUID(), diary);
        DiaryUpdatePacket.sendToPlayer(player);
    }
    public static void receiveDiaryUpdatePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ItemStack diary = diaryUpdateMap.get(packetContext.getPlayer().getUUID());
        CharSequence rawContent = buf.readCharSequence(buf.readableBytes(), DiaryUpdatePacket.charset);
        CompoundTag compoundTag = diary.getOrCreateTag();
        ListTag oldPages = compoundTag.getList("pages", 8);
        ListTag pages = new ListTag();
        StringBuilder pageContent = new StringBuilder();
        for (int i = 0; i < rawContent.length(); ++i) {
            char c = rawContent.charAt(i);
            if (c == '/') {
                pages.add(StringTag.valueOf(pageContent.toString()));
                pageContent = new StringBuilder();
            }
            else {
                pageContent.append(c);
            }
        }
        pages.addAll(oldPages);
        while (pages.size() > 100) {
            pages.remove(pages.size()-1);
        }
        compoundTag.put("pages", pages);
        diary.setTag(compoundTag);
        diaryUpdateMap.remove(packetContext.getPlayer().getUUID());
    }
}
