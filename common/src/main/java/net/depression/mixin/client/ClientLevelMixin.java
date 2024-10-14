package net.depression.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.depression.Depression;
import net.depression.client.ClientPTSDManager;
import net.depression.client.DepressionClient;
import net.depression.mental.PTSDManager;
import net.depression.network.PlaySoundPacket;
import net.depression.sound.ModSounds;
import net.depression.util.TempValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        ClientPTSDManager ptsdManager = DepressionClient.clientMentalStatus.ptsdManager;
        Player player = Minecraft.getInstance().player;
        long curTick = ((ClientLevel) (Object) this).getGameTime();
        String dimensionID = Minecraft.getInstance().level.dimensionTypeId().location().toString();
        ArrayDeque<Pair<Entity, Long>> falseEntities = ClientPTSDManager.falseEntities.get(dimensionID);
        if (falseEntities != null) {
            falseEntities.removeIf(pair -> curTick - pair.getSecond() > 400);
        }
        switch (ptsdManager.onsetLevel) {
            case 4:
            case 3:
            case 2:
                if ((curTick - ptsdManager.startTick) % 20 == 0) {
                    player.playSound(ModSounds.PANT.get());
                }
            case 1:
                if (ptsdManager.onsetLevel <= 1 && (curTick - ptsdManager.startTick) % 20 == 0) {
                    player.playSound(ModSounds.PANT.get(), 0.5f, 1f);
                }
                if ((curTick - ptsdManager.startTick) % ptsdManager.heartBeatTick == 0) {
                    player.playSound(ModSounds.HEARTBEATS.get(), (float) ptsdManager.heartBeatVolume, 1f);
                }
                break;
            case 0:
                break;
        }
    }
    @Inject(method = "playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", at = @At("HEAD"))
    private void playLocalSound(double d, double e, double f, SoundEvent soundEvent, SoundSource soundSource, float g, float h, boolean bl, CallbackInfo ci) {
        onPlayLocalSound(soundEvent);
    }

    @Inject(method = "playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", at = @At("HEAD"))
    private void playLocalSound(BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float f, float g, boolean bl, CallbackInfo ci) {
        onPlayLocalSound(soundEvent);
    }

    @Unique
    private void onPlayLocalSound(SoundEvent soundEvent) {
        String id = soundEvent.getLocation().toString();
        if (PTSDManager.soundEventMap.containsKey(id)) {
            PlaySoundPacket.sendToServer(id);
        }
    }
}
