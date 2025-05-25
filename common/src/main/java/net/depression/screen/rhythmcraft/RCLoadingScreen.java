package net.depression.screen.rhythmcraft;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.Depression;
import net.depression.client.DepressionClient;
import net.depression.network.RhythmCraftPacket;
import net.depression.rhythmcraft.Chart;
import net.depression.rhythmcraft.Song;
import net.depression.screen.UncloseableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RCLoadingScreen extends UncloseableScreen {
    private static final ResourceLocation BLACK_LOADING_BAR = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/loading/black_loading_bar.png");
    private static final ResourceLocation WHITE_LOADING_BAR = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/loading/white_loading_bar.png");
    private static final int loadingBarX = 195;
    private Song song;
    private Chart chart;
    private long startTime;
    private boolean loadingBarColor = false; //false: Black, true: White
    private int loadingBarSplitX = 0;

    public RCLoadingScreen(Song song, int difficulty) {
        super(Component.literal(""));
        this.song = song;
        this.chart = song.charts.get(difficulty);
    }
    @Override
    public void init() {
        super.init();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (System.currentTimeMillis() - startTime > 3000) {
            RhythmCraftPacket.sendReady();
            Minecraft.getInstance().setScreen(null);
        }
        Window window = Minecraft.getInstance().getWindow();
        int x = window.getGuiScaledWidth();
        int y = window.getGuiScaledHeight();
        float xScale = ((float) x) / 240f;
        float yScale = ((float) y) / 135f;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(xScale, yScale, 1);
        Font font = Minecraft.getInstance().font;
        guiGraphics.blit(RCSelectionScreen.BACKGROUND, 0, 0, 0, 0, 240, 135, 240, 135); //背景
        //谱面信息
        guiGraphics.drawString(font, "Song: " + song.author, 11, 72, RCSelectionScreen.white, true);
        guiGraphics.drawString(font, "Chart: " + chart.author, 11, 84, RCSelectionScreen.white, true);
        if (song.illustrator != null) {
            guiGraphics.drawString(font, "Illustrator: " + song.illustrator, 11, 96, RCSelectionScreen.white, true);
        }
        guiGraphics.blit(song.cover, 132, 12, 1, 0, 0, 96, 60, 96, 60); //曲绘
        guiGraphics.blit(RCSelectionScreen.COVER_FRAME, 132, 12, 1, 0, 0, 96, 60, 96, 60); //曲绘边框
        //绘制加载条
        int newSplitX = (int) ((System.currentTimeMillis() % 1000) / 1000.0 * 45);
        if (newSplitX < loadingBarSplitX) {
            loadingBarColor = !loadingBarColor;
        }

        loadingBarSplitX = newSplitX;
        guiGraphics.blit(loadingBarColor ? WHITE_LOADING_BAR : BLACK_LOADING_BAR, loadingBarX, 111, 1, 0, 0, loadingBarSplitX, 15, 45, 15); //加载条
        guiGraphics.blit((!loadingBarColor) ? WHITE_LOADING_BAR : BLACK_LOADING_BAR, loadingBarX + loadingBarSplitX, 111, 1, loadingBarSplitX, 0, 45 - loadingBarSplitX, 15, 45, 15); //加载条
        poseStack.popPose();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
