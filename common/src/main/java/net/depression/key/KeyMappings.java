package net.depression.key;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.depression.Depression;
import net.depression.client.DepressionClient;
import net.depression.network.RhythmCraftPacket;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.KeyMapping;

public class KeyMappings {
    public static final String RC_CATEGORY = "category.depression.rhythmcraft";
    public static final KeyMapping PAUSE_MUSIC = new KeyMapping("key.depression.pause_music", InputConstants.Type.KEYSYM,  InputConstants.KEY_LALT, RC_CATEGORY);
    public static void init() {
        KeyMappingRegistry.register(PAUSE_MUSIC);
        ClientTickEvent.CLIENT_POST.register(client -> {
            OggStreamPlayer oggStreamPlayer = DepressionClient.oggStreamPlayer;
            if (PAUSE_MUSIC.consumeClick() && oggStreamPlayer.isPlaying) {
                long newTime = Math.round(oggStreamPlayer.getElapsedTimeInSeconds() * 20);
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
            }
        });
    }

}
