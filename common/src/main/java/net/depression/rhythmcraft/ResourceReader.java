package net.depression.rhythmcraft;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.blaze3d.audio.OggAudioStream;
import dev.architectury.platform.Platform;
import net.depression.Depression;
import net.depression.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class ResourceReader {
    public static void read() {
        // 读取音乐资源
        File songFolder = new File(Platform.getGameFolder() + "/rc_songs");
        Path songFolderPath = songFolder.toPath();
        for (File file : songFolder.listFiles()) {
            if (file.getName().endsWith(".toml")) {
                String id = file.getName().substring(0, file.getName().length() - 5);
                try {
                    FileConfig config = FileConfig.of(file);
                    config.load();
                    String name = config.get("name");
                    String author = config.get("author");
                    String illustrator = config.get("illustrator");
                    double duration = ServerConfig.readDouble(config, "duration");
                    Song song = new Song(songFolderPath, id, name, author, illustrator);
                    song.durationInSeconds = duration;
                    song.durationInTicks = (long) (duration * 20d);
                    Song.idMap.put(id, song);

                    Song.nameList.add(song); //添加到歌曲列表
                }
                catch (Exception e) {
                    Depression.LOGGER.error("Failed to load song " + file.getName());
                    e.printStackTrace();
                }
            }
        }

        // 读取谱面资源
        File chartFolder = new File(Platform.getGameFolder() + "/rc_charts");
        for (File idDirectory : chartFolder.listFiles()) {
            if (idDirectory.isDirectory()) { // 遍历所有谱面文件夹
                String id = idDirectory.getName();
                Song song = Song.idMap.get(id);
                if (song == null) {
                    Depression.LOGGER.error("Failed to load song " + id + ": song not found");
                    continue;
                }
                for (File chartFile : idDirectory.listFiles()) { // 遍历谱面文件夹内的所有谱面
                    if (chartFile.getName().endsWith(".toml")) {
                        try {
                            Chart chart = new Chart(FileConfig.of(chartFile), idDirectory, song);
                            song.charts.set(chart.difficulty, chart);

                            Song.difficultyList.get(chart.difficulty).add(chart.song); //添加到难度列表
                        }
                        catch (Exception e) {
                            Depression.LOGGER.error("Failed to load chart " + chartFile.getName());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // 排序歌曲
        Song.nameList.sort(Comparator.comparing(a -> a.name));
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Song.difficultyList.get(i).sort(Comparator.comparing(a -> a.charts.get(finalI).level));
        }
    }
}
