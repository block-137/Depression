package net.depression.mixin;

import net.depression.effect.ModEffects;
import net.depression.mental.MentalIllness;
import net.depression.mental.MentalStatus;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.ArrayList;

@Mixin(MilkBucketItem.class)
public abstract class MilkBucketItemMixin {
    @Unique
    private static ArrayList<MobEffectInstance> savedEffectInstances = new ArrayList<>();

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    public void saveEffectInstance(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (!level.isClientSide()) {
            if (livingEntity instanceof ServerPlayer player) {
                MentalIllness mentalIllness = MentalStatus.getMentalStatusByServerPlayer(player).mentalIllness;
                if (mentalIllness.odCount >= 2 && level.getGameTime() - mentalIllness.lastOdTime < 200) {
                    for (MobEffectInstance effectInstance : player.getActiveEffects()) {
                        savedEffectInstances.add(effectInstance);
                    }
                }
            }
            else {
                MobEffectInstance antiDepression = livingEntity.getEffect(ModEffects.ANTI_DEPRESSION.get());
                if (antiDepression != null) {
                    savedEffectInstances.add(antiDepression);
                }
                MobEffectInstance antiMania = livingEntity.getEffect(ModEffects.ANTI_MANIA.get());
                if (antiMania != null) {
                    savedEffectInstances.add(antiMania);
                }
            }
        }
    }
    @Inject(method = "finishUsingItem", at = @At("RETURN"))
    public void restoreEffectInstance(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        for (MobEffectInstance savedEffectInstance : savedEffectInstances) {
            livingEntity.addEffect(savedEffectInstance);
        }
        savedEffectInstances.clear();
    }
}
