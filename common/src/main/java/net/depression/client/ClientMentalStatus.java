package net.depression.client;


import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.networking.NetworkManager;
import net.depression.Depression;
import net.depression.client.screen.DiaryAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static net.depression.mental.MentalIllness.getMentalHealthLevel;

public class ClientMentalStatus {
    public double emotionValue;
    public double mentalHealthValue;
    public int mentalHealthLevel;
    public ClientMentalIllness mentalIllness = new ClientMentalIllness();
    public String mentalIllnessString;
    public static boolean isJoinGame = false;
    public final double mentalHealthMaxValue = 100d;
    private static final ResourceLocation EMOTION = new ResourceLocation(Depression.MOD_ID, "textures/gui/emotion.png");
    private static final ResourceLocation MENTAL_HEALTH_BASE = new ResourceLocation(Depression.MOD_ID, "textures/gui/mental_health/bases.png");
    private static final ResourceLocation MENTAL_HEALTH_HOVER = new ResourceLocation(Depression.MOD_ID, "textures/gui/mental_health/hovers.png");
    private static final ResourceLocation MENTAL_HEALTH_HEART = new ResourceLocation(Depression.MOD_ID, "textures/gui/mental_health/hearts.png");
    public void receiveEmotionPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        emotionValue = buf.readDouble();
        //Minecraft.getInstance().gui.setOverlayMessage(Component.literal(String.format("情绪值: %.2f 精神健康值: %.2f", emotionValue, mentalHealthValue)), false);
    }
    public void receiveMentalHealthPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        mentalHealthValue = buf.readDouble();
        mentalHealthLevel = getMentalHealthLevel(mentalHealthValue);
        String curIllness = getMentalIllness(mentalHealthLevel);
        if (isJoinGame) {
            isJoinGame = false;
            mentalIllnessString = curIllness;
            return;
        }
        if (mentalIllnessString != null && !mentalIllnessString.equals(curIllness)) {
            if (mentalIllnessString.isEmpty()) {
                Minecraft.getInstance().gui.setOverlayMessage(
                        Component.translatable("message.depression.develop_illness_1")
                                .append(Component.translatable("message.depression." + curIllness))
                                .append(Component.translatable("message.depression.develop_illness_2")), false);
            }
            else if (curIllness.isEmpty()) {
                Minecraft.getInstance().gui.setOverlayMessage(
                        Component.translatable("message.depression.illness_cure_1")
                                .append(Component.translatable("message.depression." + mentalIllnessString))
                                .append(Component.translatable("message.depression.illness_cure_2")), false);
            }
            else {
                Minecraft.getInstance().gui.setOverlayMessage(
                        Component.translatable("message.depression.illness_change_1")
                                .append(Component.translatable("message.depression." + mentalIllnessString))
                                .append(Component.translatable("message.depression.illness_change_2"))
                                .append(Component.translatable("message.depression." + curIllness)), false);
            }
        }
        mentalIllnessString = curIllness;
        //Minecraft.getInstance().gui.setOverlayMessage(Component.literal(String.format("情绪值: %.2f 精神健康值: %.2f", emotionValue, mentalHealthValue)), false);
    }

    public void renderHud(GuiGraphics guiGraphics, float v) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (ClientDiaryUpdater.curDiaryItem != null) { //打开日记
            minecraft.setScreen(new BookViewScreen(new DiaryAccess(ClientDiaryUpdater.curDiaryItem)));
            ClientDiaryUpdater.curDiaryItem = null;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        Window window = minecraft.getWindow();
        int x = window.getGuiScaledWidth() / 2;
        int y = window.getGuiScaledHeight();
        //设置绘制信息
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //绘制情绪值
        RenderSystem.setShaderTexture(0, EMOTION);
        guiGraphics.blit(EMOTION, x - 8, y - 51,90,16*getEmotionLevel(), 0, 16, 16, 128, 16); //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        //绘制精神健康值
        /*
        int mentalHealthLevel = getMentalHealthLevel(mentalHealthValue);
        RenderSystem.setShaderTexture(0, MENTAL_HEALTH_BASE);
        guiGraphics.blit(MENTAL_HEALTH_BASE, x + 97, y - 22, 90, (mentalHealthLevel < 3 ? 0 : 22), 0, 22, 22, 44, 22);
        RenderSystem.setShaderTexture(0, MENTAL_HEALTH_HOVER);
        int pixels = (int) (mentalHealthValue / mentalHealthMaxValue * 20);
        guiGraphics.blit(MENTAL_HEALTH_HOVER, x + 97, y - (1 + pixels), 90, mentalHealthLevel * 22, (float) (21 - pixels), 22, (1 + pixels), 110, 22);
        RenderSystem.setShaderTexture(0, MENTAL_HEALTH_HEART);
        guiGraphics.blit(MENTAL_HEALTH_HEART, x + 97, y - 22, 90, (mentalHealthLevel < 3 ? 0 : 22), 0, 22, 22, 66, 22);
         */
        mentalIllness.render(guiGraphics, window.getGuiScaledWidth(), y);
    }

    public int getEmotionLevel() {
        if (emotionValue >= -20d && emotionValue < -12d) { //[-20, -12)
            return 1; // 绝望
        } else if (emotionValue >= -12d && emotionValue < -6d) { //[-12, -6)
            return 2; // 悲伤
        } else if (emotionValue >= -6d && emotionValue < -2d) { //[-6, -2)
            return 3; // 略悲伤
        } else if (emotionValue >= -2d && emotionValue <= 2d) { //[-2, 2]
            return 4; // 平静
        } else if (emotionValue > 2d && emotionValue <= 6d) { //(2, 6]
            return 5; // 略喜悦
        } else if (emotionValue > 6d && emotionValue <= 12d) { //(6, 12]
            return 6; // 喜悦
        } else if (emotionValue > 12d && emotionValue <= 20d) { //(12, 20]
            return 7; // 激动
        } else {
            return 0; // 未定义或超出范围
        }
    }

    public static String getMentalIllness(int mentalHealthLevel) {
        return switch (mentalHealthLevel) {
            case 1 -> "mild_depression";
            case 2 -> "moderate_depression";
            case 3 -> "major_depressive_disorder";
            default -> "";
        };
    }
}
