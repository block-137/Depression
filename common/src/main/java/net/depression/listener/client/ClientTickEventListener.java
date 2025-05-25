package net.depression.listener.client;

import com.mojang.datafixers.util.Pair;
import net.depression.Depression;
import net.depression.client.ClientMentalIllness;
import net.depression.client.ClientMentalStatus;
import net.depression.client.ClientPTSDManager;
import net.depression.client.DepressionClient;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.mixin.client.MouseHandlerInvoker;
import net.depression.screen.ComputerScreen;
import net.depression.screen.MentalTraitSelectionScreen;
import net.depression.screen.UncloseableScreen;
import net.depression.screen.rhythmcraft.RCMainScreen;
import net.depression.sound.ModSounds;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;

public class ClientTickEventListener {
    private static MentalTraitSelectionScreen selectionScreen;
    public static boolean isSetComputerScreen = false;
    public static final ConcurrentHashMap<Integer, Integer> pressedKeys = new ConcurrentHashMap<>(); //key-scancode map
    public static final ConcurrentSkipListSet<Integer> pressedButtons = new ConcurrentSkipListSet<>(); //key set
    public static void onClientLevelTick(ClientLevel clientLevel) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!ClientMentalStatus.isMentalTraitSelected) {
            if (selectionScreen == null) {
                selectionScreen = new MentalTraitSelectionScreen();
            }
            if (!(minecraft.screen instanceof UncloseableScreen)) {
                minecraft.setScreen(selectionScreen);
            }
        }
        if (isSetComputerScreen) {
            isSetComputerScreen = false;
            minecraft.setScreen(new ComputerScreen());
        }
        ClientMentalStatus mentalStatus = DepressionClient.clientMentalStatus;
        ClientMentalIllness illness = mentalStatus.mentalIllness;
        if (mentalStatus.mentalHealthId == 3 && illness.isCloseEye && illness.elapsedTime >= -60 && illness.elapsedTime <= 60) {
            KeyboardHandler keyboardHandler = minecraft.keyboardHandler;
            MouseHandlerInvoker mouseHandler = (MouseHandlerInvoker) minecraft.mouseHandler;
            long window = minecraft.getWindow().getWindow();
            for (Map.Entry<Integer, Integer> entry : pressedKeys.entrySet()) {
                keyboardHandler.keyPress(window, entry.getKey(), entry.getValue(), 0, 0);
            }
            for (Integer button : pressedButtons) {
                mouseHandler.invokeOnPress(window, button, 0, 0);
            }
        }
        ClientPTSDManager ptsdManager = mentalStatus.ptsdManager;
        Player player = Minecraft.getInstance().player;
        long curTick = clientLevel.getGameTime();
        String dimensionID = Minecraft.getInstance().level.dimensionTypeId().location().toString();
        ConcurrentLinkedDeque<Pair<Entity, Long>> falseEntities = ClientPTSDManager.falseEntities.get(dimensionID);
        if (falseEntities != null) {
            falseEntities.removeIf(pair -> curTick - pair.getSecond() > 400);
        }
        switch (ptsdManager.onsetLevel) {
            case 4:
            case 3:
            case 2:
                if ((curTick - ptsdManager.startTick) % 20 == 0) {
                    player.playSound(ModSounds.PANT.get());
                }
            case 1:
                if (ptsdManager.onsetLevel <= 1 && (curTick - ptsdManager.startTick) % 20 == 0) {
                    player.playSound(ModSounds.PANT.get(), 0.5f, 1f);
                }
                if ((curTick - ptsdManager.startTick) % ptsdManager.heartBeatTick == 0) {
                    player.playSound(ModSounds.HEARTBEATS.get(), (float) ptsdManager.heartBeatVolume, 1f);
                }
                break;
            case 0:
                break;
        }
    }

    public static void onClientTick(Minecraft minecraft) {
        if (minecraft.level == null) {
            if (DepressionClient.playingChart != null) {
                DepressionClient.playingChart = null;
            }
            if (DepressionClient.oggStreamPlayer.isPlaying) {
                DepressionClient.oggStreamPlayer.stop();
            }
        }
    }
}
