package net.depression.client;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Date;

public class ClientActionbarHint {
    private final String formHint1 = "message.depression.ptsd_form_hint_1";
    private final String formHint2 = "message.depression.ptsd_form_hint_2";
    private final String disperseHint1 = "message.depression.ptsd_disperse_hint_1";
    private final String disperseHint2 = "message.depression.ptsd_disperse_hint_2";
    private final String insomniaHint = "message.depression.insomnia";
    private final String mentalFatigueHint = "message.depression.mental_fatigue";

    public static final String diaryUnwrittenHint = "message.depression.diary_unwritten";

    private Component formLastId;
    private Component disperseLastId;
    private long formLastTime;
    private long disperseLastTime;
    private long nearbyBlockHealLastTime;
    private long breakBlockHealLastTime;
    private long killEntityHealLastTime;

    public static void displayTranslatable(String string) {
        Gui gui = Minecraft.getInstance().gui;
        gui.setOverlayMessage(Component.translatable(string), false);
    }

    public void receiveNearbyBlockHealPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        long curTime = Minecraft.getInstance().level.getGameTime();
        if (curTime - nearbyBlockHealLastTime < 1200) {
            return;
        }
        nearbyBlockHealLastTime = curTime;
        Component id = buf.readComponent();
        Gui gui = Minecraft.getInstance().gui;
        gui.setOverlayMessage(Component.translatable("message.depression.nearby_block_heal_hint_1")
                .append(id)
                .append(Component.translatable("message.depression.nearby_block_heal_hint_2")), false);
    }
    public void receiveBreakBlockHealPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        long curTime = Minecraft.getInstance().level.getGameTime();
        if (curTime - breakBlockHealLastTime < 1200) {
            return;
        }
        breakBlockHealLastTime = curTime;
        Component id = buf.readComponent();
        Gui gui = Minecraft.getInstance().gui;
        gui.setOverlayMessage(Component.translatable("message.depression.break_block_heal_hint_1")
                .append(id)
                .append(Component.translatable("message.depression.break_block_heal_hint_2")), false);
    }
    public void receiveKillEntityHealPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        long curTime = Minecraft.getInstance().level.getGameTime();
        if (curTime - killEntityHealLastTime < 1200) {
            return;
        }
        killEntityHealLastTime = curTime;
        Component id = buf.readComponent();
        Gui gui = Minecraft.getInstance().gui;
        gui.setOverlayMessage(Component.translatable("message.depression.kill_entity_heal_hint_1")
                .append(id)
                .append(Component.translatable("message.depression.kill_entity_heal_hint_2")), false);
    }

    public void receivePTSDFormPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        Component id = buf.readComponent();
        Gui gui = Minecraft.getInstance().gui;
        long time = new Date().getTime();
        if (time - formLastTime < 1000 && !id.equals(formLastId)) { //1s内如过有多个不同的提示包，则合并显示
            gui.setOverlayMessage(Component.translatable(formHint1)
                    .append(formLastId)
                    .append(", ")
                    .append(id)
                    .append(Component.translatable(formHint2)), false);
        }
        else {
            gui.setOverlayMessage(Component.translatable(formHint1)
                    .append(id)
                    .append(Component.translatable(formHint2)), false);
        }
        formLastId = id;
        formLastTime = time;
    }

    public void receivePTSDDispersePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        Component id = buf.readComponent();
        Gui gui = Minecraft.getInstance().gui;
        long time = new Date().getTime();
        if (time - disperseLastTime < 1000 && !id.equals(disperseLastId)) { //1s内如过有多个不同的提示包，则合并显示
            gui.setOverlayMessage(Component.translatable(disperseHint1)
                    .append(disperseLastId)
                    .append(", ")
                    .append(id)
                    .append(Component.translatable(disperseHint2)), false);
        }
        else {
            gui.setOverlayMessage(Component.translatable(disperseHint1)
                    .append(id)
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
