package net.depression.effect;

import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AntiDepressionEffect extends MobEffect {
    public AntiDepressionEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public boolean shouldApplyEffectTickThisTick(int tick, int amplifier) {
        return tick % 120 == 0;
    }
    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof ServerPlayer) {
            MentalStatus mentalStatus = Registry.mentalStatus.get(livingEntity.getUUID());
            if (mentalStatus == null) {
                return;
            }
            int mentalHealthLevel = mentalStatus.mentalIllness.mentalHealthLevel;
            if (1 <= mentalHealthLevel && mentalHealthLevel <= 3) {
                mentalStatus.mentalHealthValue += (amplifier + 1) * 0.05;
            }
        }
    }
}
