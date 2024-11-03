package net.depression.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.depression.mental.MentalStatus;
import net.depression.mental.PTSDManager;
import net.depression.server.Registry;
import net.depression.util.TempValues;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "broadcast", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void onPlaySound(CallbackInfo ci, @Local ServerPlayer player) {
        PTSDManager.onPlaySound(player);
    }

    @Inject(method = "broadcast", at = @At(value = "RETURN"))
    private void onBroadcastOver(Player player, double d, double e, double f, double g, ResourceKey<Level> resourceKey, Packet<?> packet, CallbackInfo ci) {
        if (TempValues.broadcastEntity != null) {
            TempValues.broadcastEntity = null;
        }
        if (TempValues.broadcastDamageSource != null) {
            TempValues.broadcastDamageSource = null;
        }
    }
}
