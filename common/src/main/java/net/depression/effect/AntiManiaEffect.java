package net.depression.effect;

import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AntiManiaEffect extends MobEffect {
    public AntiManiaEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tick, int amplifier) {
        return tick % 120 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof ServerPlayer player) {
            MentalStatus mentalStatus = Registry.mentalStatus.get(livingEntity.getUUID());
            if (mentalStatus == null) {
                return;
            }
            int mentalHealthId = mentalStatus.mentalIllness.mentalHealthId;
            if (mentalHealthId == 4 && player.hasEffect(ModEffects.ANTI_DEPRESSION.get())) {
                if (player.getEffect(ModEffects.ANTI_DEPRESSION.get()).getAmplifier() >= 2) {
                    mentalStatus.mentalHealthValue += (amplifier + 1) * 0.01 * mentalStatus.mentalTrait.medicineEffectMultiplier;
                }
            }
        }
    }
}