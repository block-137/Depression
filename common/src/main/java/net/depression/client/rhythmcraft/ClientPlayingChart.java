package net.depression.client.rhythmcraft;

import dev.architectury.networking.NetworkManager;
import net.depression.client.DepressionClient;
import net.depression.mixin.rhythmcraft.EntityAccessor;
import net.depression.network.RhythmCraftPacket;
import net.depression.rhythmcraft.Chart;
import net.depression.rhythmcraft.Song;
import net.depression.screen.OffsetButton;
import net.depression.screen.PosOrigin;
import net.depression.screen.rhythmcraft.GameEndScreen;
import net.depression.screen.rhythmcraft.RCSelectionScreen;
import net.depression.screen.rhythmcraft.SongProgressSlider;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.ChunkPos;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientPlayingChart {
    public static final ResourceLocation PAUSE = new ResourceLocation("depression", "textures/rc_screen/modifying/pause.png");
    public static final ResourceLocation PLAYING = new ResourceLocation("depression", "textures/rc_screen/modifying/playing.png");
    public static final ResourceLocation FORWARD = new ResourceLocation("depression", "textures/rc_screen/modifying/forward.png");
    public static final ResourceLocation BACKWARD = new ResourceLocation("depression", "textures/rc_screen/modifying/backward.png");
    public SongProgressSlider songProgressSlider;
    public OffsetButton pauseButton, forwardButton, backwardButton;
    public long spaceInTicks = 1;
    public ConcurrentSkipListMap<BlockPos, Shulker> highlightedNotes;
    public boolean isEditMode;
    public Chart chart;
    public Song song;
    public Comparator<? super BlockPos> comparator;
    public boolean isPlaying;
    public AtomicInteger score;
    public AtomicInteger combo;
    public BlockPos frontBlockPos;
    public BlockPos backBlockPos;
    public LerpingBossEvent progressBar;
    public boolean isTimeFreeze;

    public ArrayList<BossEvent.BossBarColor> colors = new ArrayList<>(Arrays.asList(
            BossEvent.BossBarColor.WHITE,
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarColor.PINK,
            BossEvent.BossBarColor.PURPLE
    ));

    public ArrayList<String> difficultyNames = new ArrayList<>(Arrays.asList(
            "Default",
            "Easy",
            "Normal",
            "Hard",
            "Insane",
            "Extreme"
    ));

    public ClientPlayingChart(Song song, int difficulty, boolean isEditMode) {
        this.isEditMode = isEditMode;
        this.song = song;
        this.chart = song.charts.get(difficulty);
        this.comparator = chart.notes.comparator();
        score = new AtomicInteger(0);
        combo = new AtomicInteger(0);
        highlightedNotes = new ConcurrentSkipListMap<>(comparator);

        if (!isEditMode) {
            progressBar = new LerpingBossEvent(Mth.createInsecureUUID(), Component.literal(song.name + " - " + difficultyNames.get(difficulty) + " Lv." + chart.level), 0f,
                    colors.get(chart.difficulty), BossEvent.BossBarOverlay.PROGRESS, false, false, false);
        }
    }


    public void onChunkLoad(ChunkPos chunkPos) {
        BlockPos min = chart.getMinBoundary(chunkPos);
        BlockPos max = chart.getMaxBoundary(chunkPos);
        for (BlockPos note : chart.notes.subSet(min, false, max, false)) {
            renderNote(note);
        }
    }

    public void onChunkUnload(ChunkPos chunkPos) {
        BlockPos min = chart.getMinBoundary(chunkPos);
        BlockPos max = chart.getMaxBoundary(chunkPos);
        for (BlockPos note : chart.notes.subSet(min, true, max, true)) {
            Shulker shulker = highlightedNotes.remove(note);
            if (shulker != null) {
                shulker.setRemoved(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    public void renderNote(BlockPos pos) {
        if (highlightedNotes.containsKey(pos)) {
            return;
        }
        EntityType.byString("minecraft:shulker").ifPresent(entityType -> {
            Shulker shulker = new Shulker((EntityType<? extends Shulker>) entityType, Minecraft.getInstance().level);
            shulker.moveTo(pos, 0, 0);
            shulker.setInvisible(true);
            ((EntityAccessor) shulker).invokeSetSharedFlag(6, true);
            highlightedNotes.put(pos, shulker);
        });
    }

    public static void receivePlaySongPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ClientPlayingChart playingChart = DepressionClient.playingChart;
        playingChart.isPlaying = true;
        try {
            if (playingChart.isEditMode) {
                playingChart.songProgressSlider = new SongProgressSlider(0.1, 0, 0.8, DepressionClient.oggStreamPlayer);
                playingChart.backwardButton = new OffsetButton(PosOrigin.MID, -32, PosOrigin.ZERO, 24, 20, 20, 1, () -> BACKWARD, true,
                        button -> {
                            OggStreamPlayer oggStreamPlayer = DepressionClient.oggStreamPlayer;
                            long newTime = Math.round(oggStreamPlayer.getElapsedTimeInSeconds() * 20) - playingChart.spaceInTicks;
                            newTime = Math.max(0, newTime);
                            oggStreamPlayer.seek(newTime);
                            RhythmCraftPacket.sendProgressChange(newTime);
                },
                        (button, guiGraphics) -> {
                            Font font = Minecraft.getInstance().font;
                            String text = "-" + playingChart.spaceInTicks + " ticks";
                            guiGraphics.drawString(font, text, button.getX() - font.width(text), button.getY(), RCSelectionScreen.white);
                        });
                playingChart.forwardButton = new OffsetButton(PosOrigin.MID, 12, PosOrigin.ZERO, 24, 20, 20, 1, () -> FORWARD, true,
                        button -> {
                            OggStreamPlayer oggStreamPlayer = DepressionClient.oggStreamPlayer;
                            if (!oggStreamPlayer.isForwarding) {
                                long newTime = Math.round(oggStreamPlayer.getElapsedTimeInSeconds() * 20) + playingChart.spaceInTicks;
                                newTime = Math.min((long) (oggStreamPlayer.getDurationInSeconds() * 20d), newTime);
                                oggStreamPlayer.forward(playingChart.spaceInTicks);
                                RhythmCraftPacket.sendProgressChange(newTime);
                            }
                },
                        (button, guiGraphics) -> {
                            guiGraphics.drawString(Minecraft.getInstance().font, "+" + playingChart.spaceInTicks + " ticks", button.getX() + 20, button.getY(), RCSelectionScreen.white);
                        });
                playingChart.pauseButton = new OffsetButton(PosOrigin.MID, -10, PosOrigin.ZERO, 24, 20, 20, 1, () -> DepressionClient.oggStreamPlayer.isPaused ? PAUSE : PLAYING, true,
                        button -> {
                            OggStreamPlayer oggStreamPlayer = DepressionClient.oggStreamPlayer;
                            long newTime = Math.round(oggStreamPlayer.getElapsedTimeInSeconds() * 20);

                            //Depression.LOGGER.info("newTime: " + newTime);

                            RhythmCraftPacket.sendPauseChange(newTime);
                            if (oggStreamPlayer.isPaused) {
                                oggStreamPlayer.isSpacePaused = false;
                                oggStreamPlayer.resume();
                                oggStreamPlayer.seek(newTime);
                            }
                            else {
                                oggStreamPlayer.isSpacePaused = true;
                                oggStreamPlayer.pause();
                                oggStreamPlayer.seek(newTime);
                            }
                });
            }
        }
        catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
        DepressionClient.oggStreamPlayer.play();
    }
    public static void receiveNoteChangePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        BlockPos pos = buf.readBlockPos();
        boolean isAdd = buf.readBoolean();
        ClientPlayingChart playingChart = DepressionClient.playingChart;
        if (isAdd) {
            playingChart.renderNote(pos);
        }
        else {
            playingChart.highlightedNotes.remove(pos).setRemoved(Entity.RemovalReason.DISCARDED);
        }
    }
    public static void receiveGameplayChangePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ClientPlayingChart playingChart = DepressionClient.playingChart;
        playingChart.score.set(buf.readInt());
        playingChart.combo.set(buf.readInt());
    }
    public static void receiveTimeChangePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ClientPlayingChart playingChart = DepressionClient.playingChart;
        long time = buf.readLong();
        Minecraft.getInstance().execute(() -> {
            playingChart.isTimeFreeze = false;
            Minecraft.getInstance().level.setDayTime(time);
            playingChart.isTimeFreeze = true;
        });
    }
    public static void receiveGameEndPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ClientPlayingChart playingChart = DepressionClient.playingChart;
        playingChart.isPlaying = false;
        int score = buf.readInt();
        int hits = buf.readInt();
        int prevScore = buf.readInt();
        String id = playingChart.song.id;
        int difficulty = playingChart.chart.difficulty;
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
           minecraft.setScreen(new GameEndScreen(playingChart, score, hits, prevScore));
        });
        DepressionClient.rcProfile.newScore(id, difficulty, score);
    }

    public static void receiveSpaceChangePacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ClientPlayingChart playingChart = DepressionClient.playingChart;
        playingChart.spaceInTicks = buf.readInt();
    }
}
