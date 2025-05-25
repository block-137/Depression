package net.depression.screen.rhythmcraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.client.DepressionClient;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.mixin.rhythmcraft.BossHealthOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import javax.swing.text.html.parser.Entity;

public class GameGuiRenderer {
    public static void renderHud(GuiGraphics guiGraphics, float v) {
        if (DepressionClient.playingChart != null && DepressionClient.playingChart.isPlaying) {
            ClientPlayingChart playingChart = DepressionClient.playingChart;
            Minecraft minecraft = Minecraft.getInstance();
            if (!playingChart.isEditMode) {
                playingChart.progressBar.setProgress((float) (DepressionClient.oggStreamPlayer.getElapsedTimeInSeconds() / DepressionClient.oggStreamPlayer.getDurationInSeconds()));
                //绘制曲名
                int halfWidth = guiGraphics.guiWidth() / 2;
                int halfTextWidth = minecraft.font.width(playingChart.progressBar.getName()) / 2;
                guiGraphics.drawString(minecraft.font, playingChart.progressBar.getName(), halfWidth - halfTextWidth, 3, RCSelectionScreen.white);
                //绘制进度条
                ((BossHealthOverlayAccessor) minecraft.gui.getBossOverlay()).invokeDrawBar(guiGraphics, guiGraphics.guiWidth() / 2 - 91, 12, playingChart.progressBar);

                if (playingChart.combo.get() >= 3) {
                    //绘制COMBO
                    int halfTextWidth2 = minecraft.font.width("COMBO") / 2;
                    guiGraphics.drawString(minecraft.font, "COMBO", halfWidth - halfTextWidth2, 3 + 16, RCSelectionScreen.white);
                    //绘制COMBO数
                    String comboString = Integer.toString(playingChart.combo.get());
                    PoseStack poseStack = guiGraphics.pose();
                    poseStack.pushPose();
                    poseStack.scale(2.0f, 2.0f, 1.0f);
                    int halfComboWidth = minecraft.font.width(comboString) / 2;
                    guiGraphics.drawString(minecraft.font, comboString, halfWidth / 2 - halfComboWidth, 15, RCSelectionScreen.white);
                    poseStack.popPose();
                }

                //绘制分数
                PoseStack poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.scale(2.0f, 2.0f, 1.0f);
                String scoreString = Integer.toString(playingChart.score.get());
                int scoreWidth = minecraft.font.width(scoreString);
                guiGraphics.drawString(minecraft.font, Integer.toString(playingChart.score.get()), halfWidth - scoreWidth, 3, RCSelectionScreen.white);
                poseStack.popPose();
            }
            if (minecraft.screen == null) {
                if (playingChart.songProgressSlider != null) {
                    playingChart.songProgressSlider.renderWidget(guiGraphics, 0, 0, v);
                }
                if (playingChart.pauseButton != null) {
                    playingChart.pauseButton.renderWidget(guiGraphics, 0, 0, v);
                }
                if (playingChart.forwardButton != null) {
                    playingChart.forwardButton.renderWidget(guiGraphics, 0, 0, v);
                }
                if (playingChart.backwardButton != null) {
                    playingChart.backwardButton.renderWidget(guiGraphics, 0, 0, v);
                }
            }

        }
    }
}
