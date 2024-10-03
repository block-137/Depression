package net.depression.client;


import dev.architectury.networking.NetworkManager;
import net.depression.item.DiaryItem;
import net.depression.mixin.client.FontAccess;
import net.depression.network.DiaryUpdatePacket;
import net.depression.util.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;


public class ClientDiaryUpdater {
    public static DiaryItem diaryItem;
    public static Level level;
    public static Player player;
    public static InteractionHand interactionHand;
    public static ItemStack curDiaryItem;
    public static Random random = new Random();
    public static final ResourceLocation writeSound = new ResourceLocation( "assets/diary_sound/write_diary");

    public static void setInfo(DiaryItem diaryItem, Level level, Player player, InteractionHand interactionHand) {
        ClientDiaryUpdater.diaryItem = diaryItem;
        ClientDiaryUpdater.level = level;
        ClientDiaryUpdater.player = player;
        ClientDiaryUpdater.interactionHand = interactionHand;
    }

    public static void receiveDiaryUpdatePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        player = Minecraft.getInstance().player;
        level.playSeededSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, SoundSource.PLAYERS, 1.0F, 1.0F, random.nextLong());
        //解析文本
        CharSequence rawContent = buf.readCharSequence(buf.readableBytes(), DiaryUpdatePacket.charset);
        StringBuilder content;
        boolean isMDD = false;
        if (DepressionClient.clientMentalStatus.mentalHealthLevel < 3) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Component.translatable("diary.depression.date_format").getString());
            content = new StringBuilder(dateFormat.format(Tools.getGameDate(Minecraft.getInstance().level.getDayTime())) + "\n");
        }
        else {
            isMDD = true;
            content = new StringBuilder();
        }
        boolean isTranslatable = false;
        String key = "";
        for (int i = 0; i < rawContent.length(); ++i) {
            char c = rawContent.charAt(i);
            if (isTranslatable) { //如果正在扫描可翻译文本
                if (c == '\'') { //如果遇到了结束符则结束可翻译模式，将翻译文本加入content
                    content.append(Component.translatable(key).getString());
                    isTranslatable = false;
                }
                else { //否则继续扫描
                    key += c;
                }
            }
            else { //如果不在扫描可翻译文本
                if (c == '\'') { //如果遇到了开始符则开始翻译模式
                    isTranslatable = true;
                    key = "";
                }
                else { //否则直接加入content
                    content.append(c);
                }
            }
        }
        //将content分页
        String text;
        if (isMDD) { //如果是重度，需要加入日期
            text = Tools.textDateFormat(content.toString(), Minecraft.getInstance().level.getDayTime());
        }
        else {
            text = content.toString();
        }
        int spaceCount = 0;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                ++spaceCount;
            }
        }
        boolean isLatin = spaceCount > 24;

        ItemStack itemStack = player.getItemInHand(interactionHand);
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        ListTag listTag = new ListTag();
        StringBuilder pageBuilder = new StringBuilder();
        StringBuilder sentToServerContent = new StringBuilder();
        StringSplitter splitter = ((FontAccess) Minecraft.getInstance().font).getSplitter();
        int index;
        int line = 0;
        while (!text.isEmpty()) {
            if (isLatin) {
                index = splitter.findLineBreak(text, 114, Style.EMPTY);
            }
            else {
                index = splitter.formattedIndexByWidth(text, 114, Style.EMPTY);
                for (int i = 0; i < index; ++i) { //寻找是否有换行符
                    if (text.charAt(i) == '\n') {
                        index = i + 1;
                        break;
                    }
                }
            }
            if (index < text.length() && text.charAt(index) == '\n') { //如果断的位置正好是换行符
                ++index;
            }
            pageBuilder.append(text, 0, index);
            text = text.substring(index);
            if (++line % 14 == 0) {
                String pageText = pageBuilder.toString();
                listTag.add(StringTag.valueOf(pageText));
                sentToServerContent.append(pageText);
                sentToServerContent.append('/');
                pageBuilder = new StringBuilder();
            }
        }
        if (line % 14 != 0) { //如果最后一页不满14行
            String pageText = pageBuilder.toString();
            listTag.add(StringTag.valueOf(pageText));
            sentToServerContent.append(pageText);
            sentToServerContent.append('/');
        }

        //加入新的内容
        ListTag oldListTag = compoundTag.getList("pages", 8);
        listTag.addAll(oldListTag);
        compoundTag.put("pages", listTag);
        itemStack.setTag(compoundTag);
        //打开日记界面
        curDiaryItem = itemStack;
        //向服务器发送更新
        DiaryUpdatePacket.sendToServer(sentToServerContent.toString());
    }
}
