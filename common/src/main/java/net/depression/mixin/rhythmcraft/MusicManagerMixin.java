package net.depression.mixin.rhythmcraft;

import net.depression.client.DepressionClient;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin {
    @Inject(method = "startPlaying", at = @At("HEAD"), cancellable = true)
    private void onStartPlaying(Music music, CallbackInfo ci) {
        if (DepressionClient.oggStreamPlayer.isPlaying) {
            ci.cancel();
        }
    }
}
