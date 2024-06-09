package net.depression.client;


import dev.architectury.networking.NetworkManager;
import net.depression.Depression;
import net.depression.item.DiaryItem;
import net.depression.mixin.client.FontAccess;
import net.depression.network.DiaryUpdatePacket;
import net.depression.sound.ModSounds;
import net.depression.util.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
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
import net.minecraft.util.RandomSource;
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
    public static final ResourceLocation writeSound = new ResourceLocation( "assets/diary_sound/write_diary");

    public static void setInfo(DiaryItem diaryItem, Level level, Player player, InteractionHand interactionHand) {
        ClientDiaryUpdater.diaryItem = diaryItem;
        ClientDiaryUpdater.level = level;
        ClientDiaryUpdater.player = player;
        ClientDiaryUpdater.interactionHand = interactionHand;
    }

    public static void receiveDiaryUpdatePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        //播放音效
        player.playSound(ModSounds.WRITE_DIARY.get());
        //解析文本
        CharSequence rawContent = buf.readCharSequence(buf.readableBytes(), DiaryUpdatePacket.charset);
        StringBuilder content;
        boolean isMDD = false;
        if (DepressionClient.clientMentalStatus.mentalHealthLevel < 3) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Component.translatable("diary.depression.date_format").getString());
            content = new StringBuilder(dateFormat.format(Tools.getGameDate(Minecraft.getInstance().level.getDayTime())) + "  ");
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
        FormattedText text;
        if (isMDD) { //如果是重度，需要加入日期
            SimpleDateFormat dateFormat = new SimpleDateFormat(content.toString());
            text = FormattedText.of(dateFormat.format(Tools.getGameDate(Minecraft.getInstance().level.getDayTime())));
        }
        else {
            text = FormattedText.of(content.toString());
        }
        FontAccess font = (FontAccess) Minecraft.getInstance().font;
        List<FormattedText> list = font.getSplitter().splitLines(text, 114, Style.EMPTY);
        ItemStack itemStack = player.getItemInHand(interactionHand);
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        ListTag listTag = new ListTag();
        StringBuilder sentToServerContent = new StringBuilder();
        StringBuilder pageContent = new StringBuilder(); //一页的内容
        for (int i = 1; i <= list.size(); ++i) {
            pageContent.append(list.get(i - 1).getString());
            if (i % 14 == 0) { //每14行一页
                listTag.add(StringTag.valueOf(pageContent.toString()));
                sentToServerContent.append(pageContent);
                sentToServerContent.append('\'');
                pageContent = new StringBuilder();
            }
        }
        if (list.size() % 14 != 0) { //如果最后一页不满14行
            listTag.add(StringTag.valueOf(pageContent.toString()));
            sentToServerContent.append(pageContent);
            sentToServerContent.append('\'');
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
