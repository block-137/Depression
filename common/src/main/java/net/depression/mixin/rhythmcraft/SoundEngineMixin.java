package net.depression.mixin.rhythmcraft;

import com.llamalad7.mixinextras.sugar.Local;
import net.depression.Depression;
import net.depression.client.ClientPTSDManager;
import net.depression.client.DepressionClient;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Inject(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F", at = @At(value = "HEAD"), cancellable = true)
    private void onCalculateVolume(SoundInstance soundInstance, CallbackInfoReturnable<Float> cir) {
        if (DepressionClient.playingChart != null && soundInstance.getLocation().getPath().contains("minecart")) {
            cir.setReturnValue(0F);
        }
    }
    @Inject(method = "updateCategoryVolume", at = @At("HEAD"))
    private void onUpdateCategoryVolume(SoundSource soundSource, float f, CallbackInfo ci) {
        OggStreamPlayer oggPlayer = DepressionClient.oggStreamPlayer;
        if (soundSource == SoundSource.MUSIC) {
            oggPlayer.setVolumeLinear(f);
        }
    }
}
