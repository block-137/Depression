package net.depression.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.Depression;
import net.depression.screen.rhythmcraft.RCMainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComputerScreen extends Screen {
    private static final ResourceLocation WALLPAPER = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/wallpaper_default.png");
    private static final ResourceLocation TASKBAR_BASE = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/taskbar_base.png");
    public static final ResourceLocation TASKBAR_HOVER = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/taskbar_hover_full.png");
    public static final ResourceLocation THIS_PC_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_this_pc.png");
    public static final ResourceLocation CONTROL_PANEL_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_control_panel.png");
    public static final ResourceLocation RECYCLE_BIN_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_recycle_bin.png");
    public static final ResourceLocation BROWSER_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_browser.png");
    public static final ResourceLocation RHYTHMCRAFT_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_rhythmcraft.png");
    public static final ResourceLocation MESSAGE_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_message.png");
    public static final ResourceLocation EMAIL_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_email.png");
    public static final ResourceLocation DOCUMENT_ICON = new ResourceLocation(Depression.MOD_ID, "textures/computer_screen/desktop_icon_document.png");
    public static final int black = 0x202020;
    public static final int white = 0xeeeeee;
    public static final int nameX1 = 32;
    public static final int nameX2 = 80;
    public static final int nameY1 = 54;
    public static final int nameY2 = 112;
    public static final int nameY3 = 170;
    public static final int nameY4 = 228;
    private ScaleButton rcButton;
    private int clickCount = 0;
    private long startClickTime = 0;
    public ComputerScreen() {
        super(Component.literal(""));
    }
    @Override
    public void init() {
        rcButton = new ScaleButton(64d/480d, 16d/270d, 32d/480d, 32d/270d, 1, RHYTHMCRAFT_ICON, (button) -> {
            startClickTime = System.currentTimeMillis();
            if (++clickCount == 2) {
                Minecraft.getInstance().setScreen(new RCMainScreen());
            }
        });
        addRenderableWidget(rcButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (System.currentTimeMillis() - startClickTime > 1000) {
            clickCount = 0;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        Font font = minecraft.font;
        int x = window.getGuiScaledWidth();
        int y = window.getGuiScaledHeight();
        float xScale = x / 480f;
        float yScale = y / 270f;
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        guiGraphics.blit(WALLPAPER, 0, 0, 0, 0, 0, x, y, x, y);
        //绘制任务栏
        guiGraphics.blit(TASKBAR_BASE, 0, 0, 0, 0, 0, x, y, x, y);
        guiGraphics.blit(TASKBAR_HOVER, 0, 0, 0, 0, 0, x, y, x, y);
        //绘制图标
        int iconLength = (int) (32f * xScale);
        int iconX1 = (int) (16f*xScale);
        int iconX2 = (int) (64f*xScale);
        int iconY1 = (int) (16f*yScale);
        int iconY2 = (int) (74f*yScale);
        int iconY3 = (int) (132f*yScale);
        int iconY4 = (int) (190f*yScale);
        guiGraphics.blit(THIS_PC_ICON, iconX1, iconY1, 1, 0, 0, iconLength, iconLength, iconLength, iconLength);
        guiGraphics.blit(CONTROL_PANEL_ICON, iconX1, iconY2, 1, 0, 0, iconLength, iconLength, iconLength, iconLength);
        guiGraphics.blit(RECYCLE_BIN_ICON, iconX1, iconY3, 1, 0, 0, iconLength, iconLength, iconLength, iconLength);
        guiGraphics.blit(BROWSER_ICON, iconX1, iconY4, 1, 0, 0, iconLength, iconLength, iconLength, iconLength);
        guiGraphics.blit(MESSAGE_ICON, iconX2, iconY2, 1, 0, 0, iconLength, iconLength, iconLength, iconLength);
        guiGraphics.blit(EMAIL_ICON, iconX2, iconY3, 1, 0, 0, iconLength, iconLength, iconLength, iconLength);
        guiGraphics.blit(DOCUMENT_ICON, iconX2, iconY4, 1, 0, 0, iconLength, iconLength, iconLength, iconLength);
        //绘制文字
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(xScale, yScale, 1f);
        //绘制任务栏文字
        guiGraphics.drawString(font, Component.translatable("screen.depression.computer.start"), 23, 256, black, false);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Component.translatable("screen.depression.computer.date_format").getString());
        String time = dateFormat.format(new Date());
        guiGraphics.drawString(font, time, 430 - font.width(time) / 2 , 256, black, false);
        //绘制桌面图标文字
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.this_pc"), nameX1, nameY1, white);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.control_panel"), nameX1, nameY2, white);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.recycle_bin"), nameX1, nameY3, white);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.browser"), nameX1, nameY4, white);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.rhythmcraft"), nameX2, nameY1, white);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.message"), nameX2, nameY2, white);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.email"), nameX2, nameY3, white);
        guiGraphics.drawCenteredString(font, Component.translatable("screen.depression.computer.documents"), nameX2, nameY4, white);
        poseStack.popPose();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

}
