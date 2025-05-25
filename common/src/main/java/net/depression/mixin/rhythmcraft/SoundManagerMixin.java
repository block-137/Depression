package net.depression.mixin.rhythmcraft;

import net.depression.Depression;
import net.depression.client.DepressionClient;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {
    @Inject(method = "pause", at = @At("HEAD"))
    private void onPause(CallbackInfo ci) {
        OggStreamPlayer oggPlayer = DepressionClient.oggStreamPlayer;
        if (oggPlayer.isPlaying) {
            oggPlayer.isEscPaused = true;
            oggPlayer.pause();
        }
    }

    @Inject(method = "resume", at = @At("HEAD"))
    private void onResume(CallbackInfo ci) {
        OggStreamPlayer oggPlayer = DepressionClient.oggStreamPlayer;
        oggPlayer.isEscPaused = false;
        if (oggPlayer.isPaused) {
            oggPlayer.resume();
        }
    }
}
