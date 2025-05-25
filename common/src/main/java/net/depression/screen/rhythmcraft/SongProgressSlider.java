package net.depression.screen.rhythmcraft;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.platform.Window;
import net.depression.Depression;
import net.depression.network.RhythmCraftPacket;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class SongProgressSlider extends AbstractSliderButton {
    public double xScale;
    public double yScale;
    public double widthScale;
    public double heightScale;
    public double playedSeconds;
    public int totalMinutes;
    public int totalSeconds;
    public OggStreamPlayer songPlayer;
    public SongProgressSlider(double xScale, double yScale, double widthScale, OggStreamPlayer songPlayer) throws UnsupportedAudioFileException, IOException {
        super(0, 0, 0, 20, Component.literal(""), 0);
        this.xScale = xScale;
        this.yScale = yScale;
        this.widthScale = widthScale;
        this.songPlayer = songPlayer;
        totalMinutes = (int) songPlayer.getDurationInSeconds() / 60;
        totalSeconds = (int) songPlayer.getDurationInSeconds() % 60;
    }

    @Override
    protected void updateMessage() {}

    @Override
    protected void applyValue() {
        playedSeconds = value * songPlayer.getDurationInSeconds();
        long tick = (long) (playedSeconds * 20d);
        songPlayer.seek(tick);
        RhythmCraftPacket.sendProgressChange(tick);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        playedSeconds = songPlayer.getElapsedTimeInSeconds();
        value = playedSeconds / songPlayer.getDurationInSeconds();
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        Font font = minecraft.font;
        int windowWidth = window.getGuiScaledWidth();
        int windowHeight = window.getGuiScaledHeight();
        setX((int) (windowWidth * xScale));
        if (getX() < 24) {
            setX(24);
            setWidth(windowWidth - 48);
        }
        else {
            setWidth((int) (windowWidth * widthScale));
        }
        setY((int) (windowHeight * yScale));
        super.renderWidget(guiGraphics, i, j, f);
        int minutes = (int) playedSeconds / 60;
        int seconds = (int) playedSeconds % 60;
        guiGraphics.drawString(font, minutes + ":" + seconds, getX() - 24, getY(), RCSelectionScreen.white);
        if (songPlayer.isSpacePaused) {
            long tick = Math.round((playedSeconds - (double) (int) (playedSeconds)) * 20d);
            String text = "(" + tick + " ticks)";
            guiGraphics.drawString(font, text, getX() - font.width(text), getY() + 10, RCSelectionScreen.white);
        }
        guiGraphics.drawString(font, totalMinutes + ":" + totalSeconds, getX() + getWidth() + 4, getY(), RCSelectionScreen.white);
    }
}
