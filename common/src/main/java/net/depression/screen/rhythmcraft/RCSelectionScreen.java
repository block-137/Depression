package net.depression.screen.rhythmcraft;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.networking.NetworkManager;
import net.depression.Depression;
import net.depression.client.DepressionClient;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.network.RhythmCraftPacket;
import net.depression.rhythmcraft.*;
import net.depression.screen.ScaleButton;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.*;

public class RCSelectionScreen extends Screen {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/background.png");
    private static final ResourceLocation BEST_SCORE = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/best_score.png");
    private static final ResourceLocation SELECTION_BAR = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/selection_bar.png");
    private static final ResourceLocation SELECTION_SHADOW = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/selection_shadow.png");
    public static final ResourceLocation COVER_FRAME = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/cover_frame.png");
    private static final ResourceLocation DIFFICULTY_LABEL = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/difficulty_label.png");
    private static final ResourceLocation DIFFICULTY_SHADOW = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/difficulty_shadow.png");
    private static final ResourceLocation START_BUTTON = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/start_button.png");
    private static final ResourceLocation BACK_BUTTON = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/back_button.png");
    private static final ResourceLocation EDIT_CHART_BUTTON = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/selection/edit_chart_button.png");
    public static final int black = 0x202020;
    public static final int white = 0xeeeeee;
    private static RCSelectionScreen instance;
    private ScaleButton startButton;
    private ScaleButton backButton;
    private ScaleButton editChartButton;
    private Song curSong;
    public ArrayList<ScaleButton> songButtons = new ArrayList<>(6) {{
        for (int i = 0; i < 6; ++i) {
            add(null);
        }
    }};
    public ArrayList<ScaleButton> difficultyButtons = new ArrayList<>(5) {{
        for (int i = 0; i < 5; ++i) {
            add(null);
        }
    }};
    public RCSelectionScreen() {
        super(Component.literal(""));
    }

    @Override
    public void init() {
        instance = this;
        RhythmCraftPacket.sendProfileRequest();
        startButton = new ScaleButton(182d/240d, 109d/135d, 58d/240d, 19d/135d, 1, null, (button) -> {
            RhythmCraftPacket.sendReadChart(curSong.id, DepressionClient.rcProfile.difficulty, false);
            DepressionClient.playingChart = new ClientPlayingChart(curSong, DepressionClient.rcProfile.difficulty, false);
            loadChart();
        });
        backButton = new ScaleButton(0d/240d, 105d/135d, 21d/240d, 23d/135d, 3, BACK_BUTTON, (button) -> {
            Minecraft.getInstance().setScreen(new RCMainScreen());
        });
        editChartButton = new ScaleButton(192d/240d, 3d/135d, 48d/240d, 15d/135d, 3, EDIT_CHART_BUTTON, (button) -> {
            RhythmCraftPacket.sendReadChart(curSong.id, DepressionClient.rcProfile.difficulty, true);
        });
        addRenderableWidget(startButton);
        addRenderableWidget(backButton);
        addRenderableWidget(editChartButton);
    }

    private void loadChart() {
        Minecraft.getInstance().getMusicManager().stopPlaying();
        try {
            OggStreamPlayer oggPlayer = new OggStreamPlayer();
            oggPlayer.load(curSong.path.toString(), curSong.durationInSeconds);
            DepressionClient.oggStreamPlayer = oggPlayer;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            Depression.LOGGER.error("Failed to load song file: " + curSong.path.toString());
            e.printStackTrace();
        }
        Minecraft.getInstance().setScreen(new RCLoadingScreen(curSong, DepressionClient.rcProfile.difficulty));
    }

    public static void receiveAcceptEditPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        DepressionClient.playingChart = new ClientPlayingChart(instance.curSong, DepressionClient.rcProfile.difficulty, true);
        Minecraft.getInstance().execute(() -> {
            instance.loadChart();
        });
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Window window = Minecraft.getInstance().getWindow();
        int x = window.getGuiScaledWidth();
        int y = window.getGuiScaledHeight();
        float xScale = ((float) x) / 240f;
        float yScale = ((float) y) / 135f;
        //渲染固定的基础背景
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        guiGraphics.blit(BACKGROUND, 0, 0, -1, 0, 0, x, y, x, y); //底层背景
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(xScale, yScale, 1f);

        //渲染选歌界面其它组件
        RhythmCraftProfile profile = DepressionClient.rcProfile;
        if (profile.sortType != null) {
            //渲染左侧
            ArrayList<Song> songList = null;
            switch (profile.sortType) {
                case NAME -> songList = Song.nameList;
                case DIFFICULTY -> songList = Song.difficultyList.get(profile.difficulty);
            }
            if (songList == null || songList.isEmpty()) {
                return;
            }
            curSong = songList.get(profile.index);
            int frameX = 11;
            int frameY = 53;
            int levelX = 110;
            int levelY = 59;
            int frameDeltaY = 21;
            int frameLength = 102; //单个选歌框的长度
            int frameHeight = 19;
            int barLength = 204; //选歌框图片的缩放长度
            int barHeight = 152;
            for (int i = -2; i < 4; ++i) {
                int widgetIndex = i + 2;
                ScaleButton button = songButtons.get(widgetIndex);
                if (profile.index + i < 0 || profile.index + i >= songList.size()) { //不在歌曲列表范围内
                    if (button != null) {
                        removeWidget(button);
                        songButtons.set(widgetIndex, null);
                    }
                    continue;
                }
                if (i != 0) {
                    //添加按钮
                    if (button == null) {
                        int finalI = i;
                        addRenderableWidget(new ScaleButton(11d/240d, ((double) (11 + widgetIndex * 21))/135d, 102d/240d, 19d/135d, 0, null, (scaleButton) -> {
                            profile.index += finalI;
                            RhythmCraftPacket.sendProfileUpdateC2S(profile, ProfileDataType.INDEX);
                        }));
                    }

                    Song song = songList.get(profile.index + i);
                    ArrayList<Integer> scores = profile.chartScores.get(song.id);
                    int rankNum = 0;
                    if (scores != null && scores.size() > profile.difficulty) {
                        rankNum = RhythmCraftProfile.getRankNumber(scores.get(profile.difficulty));
                    }
                    guiGraphics.blit(SELECTION_BAR, frameX, frameY + i * frameDeltaY, 0, 0, 19 * rankNum, frameLength, frameHeight, barLength, barHeight);
                    if (i != 3) {
                        RCDrawer.drawChartLevel(guiGraphics, levelX, levelY + i * frameDeltaY, 0, song, profile.difficulty);
                    }
                }
                else {
                    ArrayList<Integer> scores = profile.chartScores.get(curSong.id);
                    int rankNum = 0;
                    if (scores != null && scores.size() > profile.difficulty) {
                        rankNum = RhythmCraftProfile.getRankNumber(scores.get(profile.difficulty));
                    }
                    guiGraphics.blit(SELECTION_BAR, 19, frameY, 0, 102, 19 * rankNum, frameLength, frameHeight, barLength, barHeight);
                }
            }
            //绘制歌曲名
            int nameX = 20;
            int nameY = 59;

            Font font = Minecraft.getInstance().font;
            for (int i = -2; i < 4; ++i) {
                if (profile.index + i < 0 || profile.index + i >= songList.size()) { //不在歌曲列表范围内
                    continue;
                }
                if (i != 0) {
                    Song song = songList.get(profile.index + i);
                    guiGraphics.drawString(font, song.name, nameX, nameY + i * frameDeltaY, white, false);
                }
                else {
                    guiGraphics.drawString(font, curSong.name, 28, nameY, black, false);
                }
            }

            //绘制下层阴影
            RenderSystem.enableBlend();
            guiGraphics.blit(SELECTION_SHADOW, frameX, 103, 0, 0, 0, 102, 32, 102, 32);


            //渲染右侧
            //绘制曲绘
            int coverX = 132;
            int coverY = 12;
            int coverLength = 96;
            int coverHeight = 60;
            guiGraphics.blit(curSong.cover, coverX, coverY, 0, 0, 0, coverLength, coverHeight, coverLength, coverHeight);
            guiGraphics.blit(COVER_FRAME, coverX, coverY, 0, 0, 0, coverLength, coverHeight, coverLength, coverHeight);

            //绘制难度
            int difficultyX = 128;
            int difficultyY = 76;

            int rankY = 82;

            int chartLevelX = 133;
            int chartLevelY = 96;

            int deltaX = 21;

            int labelLength = 19;
            int labelHeight = 7;
            int labelTextureHeight = 42;

            int rankLength = 19;
            int rankHeight = 19;
            int rankTextureLength = 38;
            int rankTextureHeight = 152;

            int shadowLength = 19;
            int shadowHeight = 27;
            ArrayList<Integer> scores = profile.chartScores.get(curSong.id);
            for (int i = 1; i < curSong.charts.size(); ++i) {
                int index = i - 1;
                Chart chart = curSong.charts.get(i);
                ScaleButton button = difficultyButtons.get(index);
                if (chart == null) {
                    if (button != null) {
                        removeWidget(button);
                        difficultyButtons.set(index, null);
                    }
                    continue;
                }
                //添加按钮
                if (button == null) {
                    int finalI = i;
                    addRenderableWidget(new ScaleButton(((double) (128 + index * 21))/240d, 76/135d, 19d/240d, 27d/135d, 1, null, (scaleButton) -> {
                        profile.difficulty = finalI;
                        RhythmCraftPacket.sendProfileUpdateC2S(profile, ProfileDataType.DIFFICULTY);
                    }));
                }

                guiGraphics.blit(DIFFICULTY_LABEL, difficultyX + index * deltaX, difficultyY, 0, 0, 7 * i, labelLength, labelHeight, labelLength, labelTextureHeight);
                int rankNum = 0;
                if (scores != null) {
                    if (scores.size() <= i) {
                        rankNum = 0;
                    }
                    else {
                        rankNum = RhythmCraftProfile.getRankNumber(scores.get(i));
                    }
                }
                guiGraphics.blit(RCDrawer.RANKINGS, difficultyX + index * deltaX, rankY, 0, 0, 19 * rankNum, rankLength, rankHeight, rankTextureLength, rankTextureHeight);
                RCDrawer.drawChartLevel(guiGraphics, chartLevelX + index * deltaX, chartLevelY, 0, curSong, i);
                if (i != profile.difficulty) {
                    RenderSystem.enableBlend();
                    guiGraphics.blit(DIFFICULTY_SHADOW, difficultyX + index * deltaX, difficultyY, 0, 0, 0, shadowLength, shadowHeight, shadowLength, shadowHeight);
                }
            }

            //绘制最高分
            int bestScoreLength = 43;
            int bestScoreHeight = 22;
            //底图
            guiGraphics.blit(BEST_SCORE, 133, 106, 1, 0, 0, bestScoreLength, bestScoreHeight, bestScoreLength, bestScoreHeight);
            Integer score = 0;
            if (scores != null && scores.size() > profile.difficulty) {
                score = scores.get(profile.difficulty);
            }
            //分数
            RCDrawer.drawRegularNumber(guiGraphics, 133, 116, 1, score, 7, profile.difficulty);

            //绘制开始按钮
            int startButtonLength = 58;
            guiGraphics.blit(START_BUTTON, 182, 105, 1, 0, 23 * profile.difficulty, startButtonLength, 23, startButtonLength, 138);
        }

        poseStack.popPose();

        //渲染上层其它组件
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
