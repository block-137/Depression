package net.depression.mixin.emotion;

import com.llamalad7.mixinextras.sugar.Local;
import net.depression.mental.MentalStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"))
    private void onRetrieve(ItemStack itemStack, CallbackInfoReturnable<Integer> cir, @Local Player player, @Local(ordinal = 1) ItemStack itemStack2) {
        if (player.getLevel().isClientSide()) {
            return;
        }
        String id = itemStack2.getItem().arch$registryName().toString();
        if (MentalStatus.fishHealValue.containsKey(id)) {
            MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player); //获取玩家的心理状态(如果没有则创建一个新的心理状态
            double healValue = MentalStatus.fishHealValue.get(id);
            if (itemStack.isEnchanted()) { //如果钓到的东西有附魔
                for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(itemStack).entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    if (enchantment.isCurse()) {
                        healValue -= (double) entry.getValue() / (double) enchantment.getRarity().getWeight();
                    }
                    else {
                        healValue += (double) entry.getValue() / (double) enchantment.getRarity().getWeight();
                    }
                }
            }
            mentalStatus.mentalHeal(id, healValue);
        }
    }
}
