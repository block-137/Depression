package net.depression.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ClientActionbarHint {
    private final Charset charset = StandardCharsets.UTF_8;
    private final String formHint1 = "message.depression.ptsd_form_hint_1";
    private final String formHint2 = "message.depression.ptsd_form_hint_2";
    private final String disperseHint1 = "message.depression.ptsd_disperse_hint_1";
    private final String disperseHint2 = "message.depression.ptsd_disperse_hint_2";
    private final String prefix = "message.depression.damagesource.";
    private final String insomniaHint = "message.depression.insomnia";
    private final String mentalFatigueHint = "message.depression.mental_fatigue";

    public static final String diaryUnwrittenHint = "message.depression.diary_unwritten";

    private String formLastId;
    private String disperseLastId;
    private long formLastTime;
    private long disperseLastTime;

    public static void displayTranslatable(String string) {
        Gui gui = Minecraft.getInstance().gui;
        gui.setOverlayMessage(Component.translatable(string), false);
    }
    public void receivePTSDFormPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {

        String id = buf.readCharSequence(buf.readableBytes(), charset).toString();
        id = prefix + id;
        Gui gui = Minecraft.getInstance().gui;
        long time = new Date().getTime();
        //TODO: 如果翻译不了就不翻译
        if (time - formLastTime < 1000 && !id.equals(formLastId)) { //1s内如过有多个不同的提示包，则合并显示
            gui.setOverlayMessage(Component.translatable(formHint1)
                    .append(Component.translatable(formLastId))
                    .append(", ")
                    .append(Component.translatable(id))
                    .append(Component.translatable(formHint2)), false);
        }
        else {
            gui.setOverlayMessage(Component.translatable(formHint1)
                    .append(Component.translatable(id))
                    .append(Component.translatable(formHint2)), false);
        }
        formLastId = id;
        formLastTime = time;
    }

    public void receivePTSDDispersePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        String id = buf.readCharSequence(buf.readableBytes(), charset).toString();
        id = prefix + id;
        Gui gui = Minecraft.getInstance().gui;
        long time = new Date().getTime();
        if (time - disperseLastTime < 1000 && !id.equals(disperseLastId)) { //1s内如过有多个不同的提示包，则合并显示
            gui.setOverlayMessage(Component.translatable(disperseHint1)
                    .append(Component.translatable(disperseLastId))
                    .append(", ")
                    .append(Component.translatable(id))
                    .append(Component.translatable(disperseHint2)), false);
        }
        else {
            gui.setOverlayMessage(Component.translatable(disperseHint1)
                    .append(Component.translatable(id))
                    .append(Component.translatable(disperseHint2)), false);
        }
        disperseLastId = id;
        disperseLastTime = time;
    }

    public void receiveInsomniaPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        Gui gui = Minecraft.getInstance().gui;
        gui.setOverlayMessage(Component.translatable(insomniaHint), false);
    }

    public void receiveMentalFatiguePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        Gui gui = Minecraft.getInstance().gui;
        gui.setOverlayMessage(Component.translatable(mentalFatigueHint), false);
    }
}
