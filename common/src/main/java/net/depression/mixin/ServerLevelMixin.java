package net.depression.mixin;

import net.depression.Depression;
import net.depression.mental.MentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.mental.PTSDManager;
import net.depression.network.MentalTraitPacket;
import net.depression.server.Registry;
import net.depression.util.TempValues;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "addNewPlayer", at = @At("HEAD"), cancellable = true)
    public void addNewPlayer(ServerPlayer player, CallbackInfo ci) {
        if (Registry.isPending(player)) {
            return;
        }
        MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
        if (mentalStatus.mentalTrait == null) {
            if (MentalStatus.DEFAULT_MENTAL_TRAIT != null) {
                mentalStatus.loadMentalTrait(MentalTrait.byId(MentalStatus.DEFAULT_MENTAL_TRAIT));
            }
            else if (MentalStatus.IS_RANDOM_CHOOSE_TRAIT) {
                mentalStatus.mentalTrait = MentalTrait.getRandomTrait();
            }
            else {
                MentalTraitPacket.sendToPlayer(player);
                Registry.addPendingPlayer((ServerLevel) (Object) this, player);
                ci.cancel();
            }
        }
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"))
    private void playSeededSound(Player player, double d, double e, double f, Holder<SoundEvent> holder, SoundSource soundSource, float g, float h, long l, CallbackInfo ci) {
        onPlaySound(player, holder);
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"))
    private void playSeededSound(Player player, net.minecraft.world.entity.Entity entity, Holder<SoundEvent> holder, SoundSource soundSource, float f, float g, long l, CallbackInfo ci) {
        onPlaySound(player, holder);
    }

    @Unique
    private void onPlaySound(@Nullable Player player, Holder<SoundEvent> holder) {
        String id = holder.value().getLocation().toString();
        if (PTSDManager.soundEventMap.containsKey(id)) {
             TempValues.broadcastDamageSource = PTSDManager.soundEventMap.get(id);
             if (player != null) {
                 PTSDManager.onPlaySound((ServerPlayer) player);
             }
        }
    }
}
