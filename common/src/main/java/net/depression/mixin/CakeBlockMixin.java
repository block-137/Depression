package net.depression.mixin;

import net.depression.mental.MentalStatus;
import net.depression.network.MentalStatusPacket;
import net.depression.server.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
    @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static void eat(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Player player, CallbackInfoReturnable<InteractionResult> cir) {
        if (levelAccessor.isClientSide()) {
            return;
        }
        MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
        if (mentalStatus == null) {
            mentalStatus = new MentalStatus((ServerPlayer) player);
            Registry.mentalStatus.put(player.getUUID(), mentalStatus);
        }
        mentalStatus.mentalHeal(6 * MentalStatus.FOOD_HEAL_RATE);
        MentalStatusPacket.sendToPlayer((ServerPlayer) player, mentalStatus);
    }
}
