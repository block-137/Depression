package net.depression.forge.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import dev.architectury.platform.Platform;
import net.depression.client.ClientMentalStatus;

import java.io.File;
import java.io.IOException;

public class ClientConfig {
    public static void load() {
        File folder = new File(Platform.getConfigFolder() + "/depression");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //读取client-config.toml
        File file = new File(folder, "client-config.toml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfig config = FileConfig.of(file);
        config.load();

        if (!config.contains("emotion_display_offset.x")) {
            config.set("emotion_display_offset.x", -8);
        }
        if (!config.contains("emotion_display_offset.y")) {
            config.set("emotion_display_offset.x", -51);
        }

        ClientMentalStatus.EMOTION_DISPLAY_OFFSET_X = config.get("emotion_display_offset.x");
        ClientMentalStatus.EMOTION_DISPLAY_OFFSET_Y = config.get("emotion_display_offset.y");

        config.save();
        config.close();
    }
}
