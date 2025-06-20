package net.depression.rhythmcraft;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.conversion.ConvertedFormat;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.TreeMultiset;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Chart {
    public FileConfig config;
    public boolean isEdited;
    public Song song;
    public String author;
    public int difficulty;
    public int level;
    public BlockPos startingPos;
    public Direction direction;
    public boolean isNeedMinecart = true;
    public String levelPath;
    public TreeSet<BlockPos> notes;
    public TreeMap<Long, Double> speedMap = new TreeMap<>();
    public HashMap<Long, Long> timeMap = new HashMap<>();


    public Chart(FileConfig config, File idDirectory, Song song) throws IOException {
        this.config = config;
        this.song = song;
        config.load();
        author = config.get("author");
        difficulty = config.get("difficulty");
        level = config.get("level");
        List<Integer> list = config.get("starting_pos");
        startingPos = new BlockPos(list.get(0), list.get(1), list.get(2));
        direction = Direction.byName(config.get("direction"));
        if (direction == null) {
            throw new IllegalArgumentException("Invalid direction");
        }

        switch (direction) {
            case NORTH: //Negative Z
                notes = new TreeSet<>((pos1, pos2) -> {
                    int z = Integer.compare(pos2.getZ(), pos1.getZ());
                    if (z != 0) {
                        return z;
                    }
                    int x = Integer.compare(pos1.getX(), pos2.getX());
                    if (x != 0) {
                        return x;
                    }
                    return Integer.compare(pos1.getY(), pos2.getY());
                });
                break;
            case SOUTH: //Positive Z
                notes = new TreeSet<>((pos1, pos2) -> {
                    int z = Integer.compare(pos1.getZ(), pos2.getZ());
                    if (z != 0) {
                        return z;
                    }
                    int x = Integer.compare(pos1.getX(), pos2.getX());
                    if (x != 0) {
                        return x;
                    }
                    return Integer.compare(pos1.getY(), pos2.getY());
                });
                break;
            case WEST: //Negative X
                notes = new TreeSet<>((pos1, pos2) -> {
                    int x = Integer.compare(pos2.getX(), pos1.getX());
                    if (x != 0) {
                        return x;
                    }
                    int z = Integer.compare(pos1.getZ(), pos2.getZ());
                    if (z != 0) {
                        return z;
                    }
                    return Integer.compare(pos1.getY(), pos2.getY());
                });
                break;
            case EAST: //Positive X
                notes = new TreeSet<>((pos1, pos2) -> {
                    int x = Integer.compare(pos1.getX(), pos2.getX());
                    if (x != 0) {
                        return x;
                    }
                    int z = Integer.compare(pos1.getZ(), pos2.getZ());
                    if (z != 0) {
                        return z;
                    }
                    return Integer.compare(pos1.getY(), pos2.getY());
                });
                break;
            case DOWN: //Negative Y
                isNeedMinecart = false;
                notes = new TreeSet<>((pos1, pos2) -> {
                    int y = Integer.compare(pos2.getY(), pos1.getY());
                    if (y != 0) {
                        return y;
                    }
                    int x = Integer.compare(pos1.getX(), pos2.getX());
                    if (x != 0) {
                        return x;
                    }
                    return Integer.compare(pos1.getZ(), pos2.getZ());
                });
                break;
            case UP:   //Positive Y
                isNeedMinecart = false;
                notes = new TreeSet<>((pos1, pos2) -> {
                    int y = Integer.compare(pos1.getY(), pos2.getY());
                    if (y != 0) {
                        return y;
                    }
                    int x = Integer.compare(pos1.getX(), pos2.getX());
                    if (x != 0) {
                        return x;
                    }
                    return Integer.compare(pos1.getZ(), pos2.getZ());
                });
                break;
        }
        levelPath = config.get("level_file");
        for (ArrayList<Integer> note : (ArrayList<ArrayList<Integer>>) config.get("notes")) {
            notes.add(new BlockPos(note.get(0), note.get(1), note.get(2)));

            // 读取速度map
            if (config.get("speed") instanceof Config map) {
                for (Config.Entry entry : map.entrySet()) {
                    long key = Long.parseLong(entry.getKey());
                    if (entry.getValue() instanceof Double value) {
                        speedMap.put(key, value);
                    } else if (entry.getValue() instanceof Integer value) {
                        speedMap.put(key, (double) value);
                    }
                }
            }
            // 读取时间map
            if (config.get("time") instanceof Config map) {
                for (Config.Entry entry : map.entrySet()) {
                    long key = Long.parseLong(entry.getKey());
                    if (entry.getValue() instanceof Long value) {
                        timeMap.put(key, value);
                    } else if (entry.getValue() instanceof Integer value) {
                        timeMap.put(key, (long) value);
                    }
                }
            }
        }
    }

    public BlockPos getMinBoundary(ChunkPos chunkPos) { //Excluded.均为开区间边界
        return switch (direction) {
            case NORTH -> //Negative Z
                    new BlockPos(0, 0, chunkPos.getMaxBlockZ() + 1);
            case SOUTH -> //Positive Z
                    new BlockPos(0, 0, chunkPos.getMinBlockZ() - 1);
            case WEST -> //Negative X
                    new BlockPos(chunkPos.getMaxBlockX() + 1, 0, 0);
            case EAST -> //Positive X
                    new BlockPos(chunkPos.getMinBlockX() - 1, 0, 0);
            case DOWN -> //Negative Y
                    new BlockPos(0, Minecraft.getInstance().level.getMaxBuildHeight() + 1, 0);
            case UP ->   //Positive Y
                    new BlockPos(0, Minecraft.getInstance().level.getMinBuildHeight() - 1, 0);
        };
    }

    public BlockPos getMaxBoundary(ChunkPos chunkPos) { //Excluded.均为开区间边界
        return switch (direction) {
            case NORTH -> //Negative Z
                    new BlockPos(0, 0, chunkPos.getMinBlockZ() - 1);
            case SOUTH -> //Positive Z
                    new BlockPos(0, 0, chunkPos.getMaxBlockZ() + 1);
            case WEST -> //Negative X
                    new BlockPos(chunkPos.getMinBlockX() - 1, 0, 0);
            case EAST -> //Positive X
                    new BlockPos(chunkPos.getMaxBlockX() + 1, 0, 0);
            case DOWN -> //Negative Y
                    new BlockPos(0, Minecraft.getInstance().level.getMinBuildHeight() - 1, 0);
            case UP ->   //Positive Y
                    new BlockPos(0, Minecraft.getInstance().level.getMaxBuildHeight() + 1, 0);
        };
    }

    public void save() {
        if (!isEdited) {
            return;
        }
        isEdited = false;
        config.set("author", author);
        config.set("difficulty", difficulty);
        config.set("starting_pos", Arrays.asList(startingPos.getX(), startingPos.getY(), startingPos.getZ()));
        config.set("direction", direction.getName());
        config.set("level_file", levelPath);
        ArrayList<List<Integer>> notesList = new ArrayList<>();
        for (BlockPos note : notes) {
            notesList.add(Arrays.asList(note.getX(), note.getY(), note.getZ()));
        }
        config.set("notes", notesList);
        // 保存速度map
        Config speedMapTable = Config.of(config.configFormat());
        for (Map.Entry<Long, Double> entry : speedMap.entrySet()) {
            speedMapTable.add(entry.getKey().toString(), entry.getValue());
        }
        config.set("speed", speedMapTable);
        // 保存时间map
        Config timeMapTable = Config.of(config.configFormat());
        for (Map.Entry<Long, Long> entry : timeMap.entrySet()) {
            timeMapTable.add(entry.getKey().toString(), entry.getValue());
        }
        config.set("time", timeMapTable);
        config.save();
    }
}
