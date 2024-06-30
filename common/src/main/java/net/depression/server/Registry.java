package net.depression.server;

import dev.architectury.networking.NetworkManager;
import net.depression.mental.MentalStatus;
import net.depression.network.DiaryUpdatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Registry {
    public static final HashMap<UUID, MentalStatus> mentalStatus = new HashMap<>();
    public static final HashSet<UUID> quitPlayers = new HashSet<>();
    public static final HashMap<UUID, StatManager> statManager = new HashMap<>();
    private static final HashMap<UUID, ItemStack> diaryUpdateMap = new HashMap<>();

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
