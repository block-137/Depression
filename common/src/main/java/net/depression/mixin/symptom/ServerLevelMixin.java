package net.depression.mixin.symptom;

import net.depression.Depression;
import net.depression.mental.PTSDManager;
import net.depression.util.TempValues;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"))
    private void playSeededSound(Player player, Entity entity, SoundEvent soundEvent, SoundSource soundSource, float f, float g, long l, CallbackInfo ci) {
        onPlaySound(player, soundEvent);
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"))
    private void playSeededSound(Player player, double d, double e, double f, SoundEvent soundEvent, SoundSource soundSource, float g, float h, long l, CallbackInfo ci) {
        onPlaySound(player, soundEvent);
    }

    @Unique
    private void onPlaySound(@Nullable Player player, SoundEvent soundEvent) {
        String id = soundEvent.getLocation().toString();
        if (PTSDManager.soundEventMap.containsKey(id)) {
             TempValues.broadcastDamageSource = PTSDManager.soundEventMap.get(id);
             if (player != null) {
                 PTSDManager.onPlaySound((ServerPlayer) player);
             }
        }
    }
}
