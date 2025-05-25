package net.depression.screen.rhythmcraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.Depression;
import net.depression.client.DepressionClient;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.network.RhythmCraftPacket;
import net.depression.rhythmcraft.Chart;
import net.depression.rhythmcraft.RhythmCraftProfile;
import net.depression.rhythmcraft.Song;
import net.depression.screen.ScaleButton;
import net.depression.screen.UncloseableScreen;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class GameEndScreen extends UncloseableScreen {
    private static final ResourceLocation TEXT_AUTHORS = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement/text_authors.png");
    private static final ResourceLocation RETRY_BUTTON = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement/retry_button.png");
    private static final ResourceLocation TEXT_YOUR_SCORE = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement/text_your_score.png");
    private static final ResourceLocation TEXT_NEW_BEST_SCORE = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement/text_new_best_score.png");
    private static final ResourceLocation TEXT_NOT_BEST_SCORE = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement/text_not_best_score.png");
    private static final ResourceLocation TEXT_STAT = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement/text_stat.png");
    private static final ResourceLocation CONTINUE_BUTTON = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement/continue_button.png");

    ClientPlayingChart playingChart;
    int score;
    int blockMined;
    int blockMissed;
    int prevScore;
    private long startTime;

    private ScaleButton retryButton;
    private ScaleButton continueButton;


    public GameEndScreen(ClientPlayingChart playingChart, int score, int blockMined, int prevScore) {
        super(Component.literal(""));
        this.playingChart = playingChart;
        this.score = score;
        this.blockMined = blockMined;
        this.blockMissed = playingChart.chart.notes.size() - blockMined;
        this.prevScore = prevScore;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void init() {
        retryButton = new ScaleButton(0d, 105d/135d, 24d/240d, 23d/135d, 3, null, button -> {
            Song song = playingChart.song;
            Chart chart = playingChart.chart;
            RhythmCraftPacket.sendReadChart(song.id, chart.difficulty, playingChart.isEditMode);
            DepressionClient.playingChart = new ClientPlayingChart(playingChart.song, DepressionClient.rcProfile.difficulty, playingChart.isEditMode);
            Minecraft.getInstance().getMusicManager().stopPlaying();
            try {
                OggStreamPlayer oggPlayer = new OggStreamPlayer();
                oggPlayer.load(song.path.toString(), song.durationInSeconds);
                DepressionClient.oggStreamPlayer = oggPlayer;
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                Depression.LOGGER.error("Failed to load song file: " + song.path.toString());
                e.printStackTrace();
            }
            Minecraft.getInstance().setScreen(new RCLoadingScreen(song, chart.difficulty));
        });
        continueButton = new ScaleButton(164d/240d, 109d/135d, 76d/240d, 19d/135d, 3, null, button -> {
            RhythmCraftPacket.sendLoadBack();
            Minecraft.getInstance().setScreen(new RCSelectionScreen());
        });
        addRenderableWidget(retryButton);
        addRenderableWidget(continueButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiGraphics.guiWidth();
        int y = guiGraphics.guiHeight();
        float xScale = ((float) x) / 240f;
        float yScale = ((float) y) / 135f;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(xScale, yScale, 1);
        Font font = Minecraft.getInstance().font;
        long timeElapsed = System.currentTimeMillis() - startTime;
        Song song = playingChart.song;
        Chart chart = playingChart.chart;
        //绘制背景
        guiGraphics.blit(RCSelectionScreen.BACKGROUND, 0, 0, -1, 0, 0, 240, 135, 240, 135);
        //绘制曲绘
        int coverX = (int) Math.min(16, -112 + timeElapsed * 0.256d);
        guiGraphics.blit(song.cover, coverX, 20, 0, 0, 0, 96, 60, 96, 60);
        guiGraphics.blit(RCSelectionScreen.COVER_FRAME, coverX, 20, 0, 0, 0, 96, 60, 96, 60);
        guiGraphics.drawString(font, song.name, coverX + 3, 68, RCSelectionScreen.white);
        //绘制作者名字
        guiGraphics.blit(TEXT_AUTHORS, coverX - 18, 0, 0, 0, 0, 240, 135, 240, 135);
        RCDrawer.drawStringSmall(guiGraphics, song.author, coverX + 23, 82, 2);
        RCDrawer.drawStringSmall(guiGraphics, chart.author, coverX + 27, 90, 2);
        //绘制重开按钮
        int retryX = (int) Math.min(0, -23 + timeElapsed * 0.046d);
        guiGraphics.blit(RETRY_BUTTON, retryX, 105, 2, 0, 0, 24, 23, 24, 23);

        //绘制分数
        int scoreX = (int) Math.max(120, 240 - (timeElapsed - 500) * 0.24d);
        guiGraphics.blit(TEXT_YOUR_SCORE, scoreX, 20, 1, 0, 14 * chart.difficulty, 84, 14, 84, 84);
        RCDrawer.drawSettlementSmallNumber(guiGraphics, scoreX + 19, 27, 2, chart.level, chart.difficulty);
        RCDrawer.drawSettlementLargeNumber(guiGraphics, scoreX, 38, 2, playingChart.score.get(), chart.difficulty);
        guiGraphics.blit(RCDrawer.RANKINGS_WITH_TEXT, scoreX + 82, 28, 2, 0, 25 * RhythmCraftProfile.getRankNumber(score), 19, 25, 38, 200); //19*25
        //绘制最高分
        int statX = (int) Math.max(120, 240 - (timeElapsed - 1000) * 0.24d);
        if (score > prevScore) {
            guiGraphics.blit(TEXT_NEW_BEST_SCORE, statX, 65, 2, 0, 0, 59, 14, 59, 14);
            RCDrawer.drawStringRightAligned(guiGraphics, "(+" + (score - prevScore) + ")", statX + 104, 65, 2);
            RCDrawer.drawStringRightAligned(guiGraphics, String.valueOf(prevScore), statX + 104, 72, 2);
        }
        else if (score < 1_000_000) {
            guiGraphics.blit(TEXT_NOT_BEST_SCORE, statX, 65, 2, 0, 0, 59, 14, 59, 14);
            RCDrawer.drawStringRightAligned(guiGraphics, String.valueOf(prevScore), statX + 104, 72, 2);
        }
        //绘制数据
        guiGraphics.blit(TEXT_STAT, statX, 80, 2, 0, 0, 105, 18, 105, 18);
        RCDrawer.drawStringRightAligned(guiGraphics, String.valueOf(blockMined), statX + 104, 84, 2);
        RCDrawer.drawStringRightAligned(guiGraphics, String.valueOf(blockMissed), statX + 104, 91, 2);
        //绘制继续按钮
        int continueX = (int) Math.max(164, 240 - (timeElapsed - 1000) * 0.152d);
        guiGraphics.blit(CONTINUE_BUTTON, continueX, 109, 2, 0, 0, 76, 19, 76, 19);
        poseStack.popPose();
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
