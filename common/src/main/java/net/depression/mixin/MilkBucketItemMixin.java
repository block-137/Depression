package net.depression.mixin;

import net.depression.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public abstract class MilkBucketItemMixin {
    @Unique
    private static MobEffectInstance savedEffectInstance;

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void saveEffectInstance(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (!level.isClientSide()) {
            savedEffectInstance = livingEntity.getEffect(ModEffects.ANTI_DEPRESSION.get());
        }
    }
    @Inject(method = "finishUsingItem", at = @At("RETURN"))
    public void restoreEffectInstance(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (savedEffectInstance != null) {
            livingEntity.addEffect(savedEffectInstance);
        }
    }
}
