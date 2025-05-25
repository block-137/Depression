package net.depression.rhythmcraft;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.depression.Depression;
import net.depression.listener.TickEventListener;
import net.depression.mental.MentalStatus;
import net.depression.mixin.rhythmcraft.PlayerListAccessor;
import net.depression.mixin.rhythmcraft.PrimaryLevelDataAccessor;
import net.depression.network.RhythmCraftPacket;
import net.depression.server.Registry;
import net.depression.world.dimension.ModDimensions;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

public class PlayingChart extends ServerLevel {
    public static MinecraftServer server;
    public static Executor executor;
    public static LevelStorageSource levelStorageSource = new LevelStorageSource(Platform.getGameFolder().resolve("rc_charts"),
            Platform.getGameFolder().resolve("rc_charts"), null, null);
    public static WorldData worldData;
    public static LevelStem chartStem;
    public static ChunkProgressListener chunkProgressListener;
    public static RandomSequences randomSequences;
    public static LinkedHashMap<UUID, PlayingChart> playingCharts = new LinkedHashMap<>();
    public static HashMap<UUID, CompoundTag> playerData = new HashMap<>();
    public boolean isEditMode;
    public boolean isPaused;
    public Boolean isJustStarted;
    public ServerPlayer player;
    public Chart chart;
    public RCMinecart minecart;
    public Vec3 speed = Vec3.ZERO;
    public ConcurrentSkipListSet<BlockPos> remainingNotes;
    public double score = 0;
    public int scoreInt = 0;
    public int combo = 0;
    //public int highestCombo = 0;
    public long tickCount = 0;
    public int prevTick = 0;
    public boolean isEnded;
    public File levelFile;
    public TreeMap<Long, Double> speedIntegration = new TreeMap<>();
    public PlayingChart(ServerPlayer player, Chart chart, boolean isEditMode) throws IOException {
        super(player.server, executor, levelStorageSource.createAccess(chart.song.id + "/" + chart.levelPath),
                new PrimaryLevelData(worldData.getLevelSettings(), worldData.worldGenOptions(), PrimaryLevelData.SpecialWorldProperty.NONE, Lifecycle.stable()),
                ModDimensions.CHART, chartStem, chunkProgressListener, false, 0, ImmutableList.of(), false, randomSequences);
        this.player = player;
        this.chart = chart;
        this.isEditMode = isEditMode;
        remainingNotes = new ConcurrentSkipListSet<>(chart.notes.comparator());
        if (!isEditMode) {
            remainingNotes.addAll(chart.notes);
        }

        if (isEditMode) {
            player.setGameMode(GameType.CREATIVE);
        }
        else {
            player.setGameMode(GameType.SURVIVAL);
        }

        BlockPos pos = chart.startingPos;
        player.teleportTo(this, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0f, 0f); //gh这俩参数一直在被偷偷篡改，每次测试都不一样，所以只能用lookAt了
        if (chart.isNeedMinecart) {
            this.minecart = new RCMinecart(this, pos.getX() + 0.5, pos.getY() + 0.0625, pos.getZ() + 0.5);
            addFreshEntity(minecart);
            player.startRiding(minecart, true);
        }
        Vec3 eyePos = player.getEyePosition();
        Vec3i direction = chart.direction.getNormal();
        player.lookAt(EntityAnchorArgument.Anchor.EYES, eyePos.add(direction.getX(), direction.getY(), direction.getZ()));

        levelFile = Platform.getGameFolder().resolve("rc_charts/" + chart.song.id + "/" + chart.levelPath + "/level.dat").toFile();
        CompoundTag playerTag = NbtIo.readCompressed(levelFile).getCompound("Data").getCompound("Player");
        ListTag inventoryTag = playerTag.getList("Inventory", 10);
        player.getInventory().load(inventoryTag);

        integrate(0);
    }
    public void start() {
        isJustStarted = true;
    }
    public EventResult onBlockBreak(BlockPos pos) {
        if (chart.notes.contains(pos)) {
            ++scoreInt;
            ++combo;
            score = (double) scoreInt / chart.notes.size() * 1e6;
            remainingNotes.remove(pos);
            RhythmCraftPacket.sendNoteChange(player, pos, false);
            RhythmCraftPacket.sendGameplayChange(player, (int) Math.round(score), combo);
            if (isEditMode) {
                chart.notes.remove(pos);
                chart.isEdited = true;
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        }
        if (isEditMode) {
            return EventResult.pass();
        }
        else {
            return EventResult.interruptFalse();
        }
    }
    public void integrate(long startingTick) {
        Map.Entry<Long, Double> curEntry = chart.speedMap.ceilingEntry(startingTick);
        if (curEntry == null) {
            return;
        }
        Map.Entry<Long, Double> prevEntry;
        do {
            prevEntry = chart.speedMap.lowerEntry(curEntry.getKey());
            if (prevEntry == null) {
                speedIntegration.put(curEntry.getKey(), 0D);
            }
            else {
                speedIntegration.put(curEntry.getKey(), speedIntegration.get(prevEntry.getKey()) + prevEntry.getValue() / 1200d * (curEntry.getKey() - prevEntry.getKey()));
            }
            curEntry = chart.speedMap.higherEntry(curEntry.getKey());
        }
        while (curEntry != null);
    }
    public void seek(long tick) {
        BlockPos pos = chart.startingPos;
        Vec3 start = new Vec3(pos.getX() + 0.5, pos.getY() + 0.0625, pos.getZ() + 0.5);
        Vec3i normalInt = chart.direction.getNormal();
        Vec3 normal = new Vec3(normalInt.getX(), normalInt.getY(), normalInt.getZ());
        Long lastKey = chart.speedMap.floorKey(tick);
        if (lastKey == null) {
            return;
        }
        Vec3 dest = start.add(normal.scale(
                speedIntegration.get(lastKey) + chart.speedMap.get(lastKey) / 1200d * (tick - lastKey)
        ));
        player.server.execute(() -> {
            minecart.teleportTo(dest.x, dest.y, dest.z);
        });
        speed = new Vec3(normalInt.getX(), normalInt.getY(), normalInt.getZ()).scale(chart.speedMap.get(lastKey) / 1200d);
        if (chart.isNeedMinecart && !isPaused) {
            minecart.setLockedDeltaMovement(speed);
        }
        tickCount = tick;
    }
    @Override
    public void tick(BooleanSupplier booleanSupplier) {
        ((PrimaryLevelDataAccessor) this.getLevelData()).invokeSetGameTime(server.overworld().getGameTime());
        if (isEnded) {
            return;
        }
        if (!isPaused) {
            if (isJustStarted == null) {
                minecart.setLockedDeltaMovement(Vec3.ZERO);
                super.tick(booleanSupplier);
                return;
            }
            else {
                if (isJustStarted) {
                    RhythmCraftPacket.sendPlaySong(player, chart.song.id);
                    isJustStarted = false;
                }
            }
            BlockPos playerPos = player.blockPosition();
            if (!isEditMode) { //是否断连
                for (BlockPos pos : remainingNotes) {
                    if (chart.notes.comparator().compare(pos, playerPos) < 0 && player.getEyePosition().distanceTo(pos.getCenter()) >= 5) {
                        //highestCombo = Math.max(highestCombo, combo);
                        combo = 0;
                        remainingNotes.remove(pos);
                        RhythmCraftPacket.sendGameplayChange(player, (int) Math.round(score), combo);
                    } else {
                        break;
                    }
                }
            }
            if (chart.speedMap.containsKey(tickCount)) {
                //速度变化
                Vec3i direction = chart.direction.getNormal();
                speed = new Vec3(direction.getX(), direction.getY(), direction.getZ()).scale(chart.speedMap.get(tickCount) / 1200d);
                if (chart.isNeedMinecart) {
                    minecart.setLockedDeltaMovement(speed);
                }
            }
            else {
                player.setDeltaMovement(speed);
            }
            if (chart.timeMap.containsKey(tickCount)) {
                long time = chart.timeMap.get(tickCount);
                setDayTime(time);
                RhythmCraftPacket.sendTimeChange(player, time);
            }
            ++tickCount;
            if (tickCount > chart.song.durationInTicks) { //游戏结束
                //highestCombo = Math.max(highestCombo, combo);
                minecart.setLockedDeltaMovement(Vec3.ZERO);
                RhythmCraftProfile profile = RhythmCraftProfile.getProfileByServerPlayer(player);
                int prevScore = 0;
                ArrayList<Integer> scores = profile.chartScores.get(chart.song.id);
                if (scores != null && scores.size() > chart.difficulty) {
                    prevScore = scores.get(chart.difficulty);
                }
                double mentalHealValue = 0;
                if (score == 1_000_000) {
                    mentalHealValue = Math.max(mentalHealValue, chart.level / 16d);
                }
                if (score > prevScore) {
                    mentalHealValue = Math.max(mentalHealValue, (score - prevScore) * chart.level / 6_400_000d);
                }
                if (mentalHealValue > 0) {
                    MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
                    mentalStatus.mentalHeal("rhythmcraft", mentalHealValue);
                }
                RhythmCraftPacket.sendGameEnd(player, (int) Math.round(score), scoreInt, prevScore);
                profile.newScore(chart.song.id, chart.difficulty, (int) Math.round(score));
                isEnded = true;
            }
        }
        super.tick(booleanSupplier);
    }
    @Override
    public void save(@Nullable ProgressListener progressListener, boolean bl, boolean bl2) {
        if (isEditMode) {
            super.save(progressListener, bl, bl2);
            chart.save();
            try {
                CompoundTag levelTag = NbtIo.readCompressed(levelFile);
                levelTag.getCompound("Data").put("Player", player.saveWithoutId(new CompoundTag()));
                NbtIo.writeCompressed(levelTag, levelFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void onReceiveReadChart(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        String songId = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
        int difficulty = buf.readInt();
        boolean isEditMode = buf.readBoolean();
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        if (isEditMode) {
            if (player.hasPermissions(2)) {
                RhythmCraftPacket.sendAcceptEdit(player);
            }
            else {
                return;
            }
        }
        player.server.execute(
                () -> {
                    Song song = Song.idMap.get(songId);
                    if (song == null) {
                        return;
                    }
                    Chart chart = song.charts.get(difficulty);
                    if (chart == null) {
                        return;
                    }
                    try {
                        //存储PlayerList的玩家数据
                        PlayerListAccessor accessor = (PlayerListAccessor) player.server.getPlayerList();
                        accessor.invokeSave(player);
                        //记录玩家数据用于对单人玩家数据的偷梁换柱（X
                        playerData.put(player.getUUID(), player.saveWithoutId(new CompoundTag()));
                        //开始游戏
                        PlayingChart playingChart = new PlayingChart(player, chart, isEditMode);
                        playingCharts.put(player.getUUID(), playingChart);
                    } catch (IOException e) {
                        playerData.remove(player.getUUID());
                        playingCharts.remove(player.getUUID());
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
        );
    }
    public static void onReceiveReady(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        player.server.execute(
                () -> {
                    PlayingChart playingChart = playingCharts.get(player.getUUID());
                    if (playingChart == null) {
                        return;
                    }
                    playingChart.start();
                }
        );
    }
    public static void onReceiveLoadBack(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        ServerPlayer player = (ServerPlayer) packetContext.getPlayer();
        player.server.execute(
                () -> {
                    UUID uuid = player.getUUID();
                    if (!playerData.containsKey(uuid)) {
                        return;
                    }
                    CompoundTag compoundTag = playerData.get(uuid);
                    player.load(compoundTag);
                    DataResult dataResult = DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, compoundTag.get("Dimension")));
                    ResourceKey<Level> resourceKey = (ResourceKey<Level>) dataResult.result().orElse(Level.OVERWORLD);
                    ServerLevel serverLevel = server.getLevel(resourceKey);
                    player.teleportTo(serverLevel, player.getX(), player.getY(), player.getZ(),
                            player.getYRot(), player.getXRot());
                    playingCharts.remove(uuid);
                    playerData.remove(uuid);
                    Registry.particles.remove(uuid.toString());
                }
        );
    }

    public static void onReceivePauseChange(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        PlayingChart playingChart = PlayingChart.playingCharts.get(packetContext.getPlayer().getUUID());
        long tick = buf.readLong();
        playingChart.isPaused = !playingChart.isPaused;
        playingChart.player.server.execute(() -> {
            if (playingChart.isPaused) {
                playingChart.seek(tick);
                playingChart.minecart.setLockedDeltaMovement(Vec3.ZERO);
            }
            else {
                playingChart.minecart.setLockedDeltaMovement(playingChart.speed);
            }
        });
    }
    public static void onReceiveProgressChange(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        PlayingChart playingChart = PlayingChart.playingCharts.get(packetContext.getPlayer().getUUID());
        long tick = buf.readLong();
        playingChart.tickCount = tick;
        playingChart.seek(tick);
    }

}
