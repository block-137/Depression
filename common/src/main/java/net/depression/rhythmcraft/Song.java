package net.depression.rhythmcraft;

import com.mojang.blaze3d.platform.NativeImage;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Song {
    public static ConcurrentHashMap<String, Song> idMap = new ConcurrentHashMap<>();
    public static ArrayList<Song> nameList = new ArrayList<>();
    public static ArrayList<ArrayList<Song>> difficultyList = new ArrayList<>(6) {{
        for (int i = 0; i < 6; ++i) {
            add(new ArrayList<>());
        }
    }};

    public String id;
    public String name;
    public String author;
    public String illustrator;
    public Path path;
    public ResourceLocation cover;
    public long durationInTicks;
    public double durationInSeconds;

    public ArrayList<Chart> charts = new ArrayList<>(6) {{
        for (int i = 0; i < 6; ++i) {
            add(null);
        }
    }};

    public Song(Path folderPath, String id, String name, String author, String illustrator) throws IOException, UnsupportedAudioFileException {
        this.id = id;
        this.name = name;
        this.author = author;
        this.illustrator = illustrator;
        this.path = folderPath.resolve(id + ".ogg");
        if (Platform.getEnv() == EnvType.CLIENT) {
            Minecraft.getInstance().execute(() -> {
                try {
                    File coverFile = folderPath.resolve(id + ".png").toFile();
                    NativeImage coverImage = NativeImage.read(new FileInputStream(coverFile));
                    this.cover = Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(coverImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
