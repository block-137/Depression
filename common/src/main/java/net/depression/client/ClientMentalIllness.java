package net.depression.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.networking.NetworkManager;
import net.depression.Depression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ClientMentalIllness {
    public long startCloseEyeTime = 0;
    public boolean isCloseEye = false;
    public double elapsedTime; //经过的时间(-60 tick ~ 60 tick) 0~10:闭合; 10~50:全闭眼; 50~60:睁眼
    public static final int priority = 300;
    public static Vec3 curPosition;
    private static final ResourceLocation DROWSY_UP = new ResourceLocation(Depression.MOD_ID, "textures/symptom/drowsy_up.png");
    private static final ResourceLocation DROWSY_DOWN = new ResourceLocation(Depression.MOD_ID, "textures/symptom/drowsy_down.png");
    private static final ResourceLocation DROWSY_FULL = new ResourceLocation(Depression.MOD_ID, "textures/symptom/drowsy_full.png");

    public void receiveCloseEyePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {
            return;
        }
        startCloseEyeTime = Minecraft.getInstance().level.getGameTime() + 60;
        isCloseEye = true;
        curPosition = Minecraft.getInstance().player.position();
        Minecraft.getInstance().gui.setOverlayMessage(Component.translatable("message.depression.close_eye"), false);
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        if (!isCloseEye) {
            return;
        }
        if (Minecraft.getInstance().level == null) {
            return;
        }
        elapsedTime = (double) (Minecraft.getInstance().level.getGameTime() - startCloseEyeTime);
        if (elapsedTime < 0) {
            return;
        }
        if (elapsedTime > 60) {
            isCloseEye = false;
            return;
        }
        int yLoc;
        if (elapsedTime > 10 && elapsedTime < 50) { //全闭眼
            RenderSystem.setShaderTexture(0, DROWSY_FULL);
            guiGraphics.blit(DROWSY_FULL, 0, 0, priority, 0, 0, x, y, 480, 360);
            return;
        }
        else if (elapsedTime <= 10) { //闭眼
            yLoc = (int) (elapsedTime / 10d * (double) y / 2d);
        }
        else { //睁眼
            yLoc = (int) ((60 - elapsedTime) / 10d * (double) y / 2);
        }
        RenderSystem.setShaderTexture(0, DROWSY_UP);
        guiGraphics.blit(DROWSY_UP, 0, yLoc - 360, priority, 0, 0, x, 360, 480, 360); //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        RenderSystem.setShaderTexture(0, DROWSY_DOWN);
        guiGraphics.blit(DROWSY_DOWN, 0, y - yLoc, priority, 0, 0, x, 360, 480, 360);
        if (yLoc > 360) {
            RenderSystem.setShaderTexture(0, DROWSY_FULL);
            guiGraphics.blit(DROWSY_FULL, 0, 0, priority, 0, 0, x, yLoc - 360, 480, 360);
            guiGraphics.blit(DROWSY_FULL, 0, y - (yLoc - 360), priority, 0, 0, x, yLoc - 360, 480, 360);
        }
    }
}
