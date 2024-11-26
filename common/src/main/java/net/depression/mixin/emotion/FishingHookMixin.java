package net.depression.mixin.emotion;

import com.llamalad7.mixinextras.sugar.Local;
import net.depression.mental.MentalStatus;
import net.depression.network.ActionbarHintPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"))
    private void onRetrieve(ItemStack itemStack, CallbackInfoReturnable<Integer> cir, @Local Player player, @Local(ordinal = 1) ItemStack itemStack2) {
        if (player.getLevel().isClientSide()) {
            return;
        }
        double healValue = MentalStatus.onLoot(player, itemStack2);
        if (healValue > 0.25) {
            ActionbarHintPacket.sendFishHealPacket((ServerPlayer) player, itemStack2.getHoverName());
        }
    }
}
