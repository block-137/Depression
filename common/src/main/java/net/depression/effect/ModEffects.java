package net.depression.effect;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Depression.MOD_ID, Registries.MOB_EFFECT);

    public static final RegistrySupplier<MobEffect> ANTI_DEPRESSION = EFFECTS.register("anti_depression",
            () -> new AntiDepressionEffect(MobEffectCategory.BENEFICIAL, 0xF4E4B2));

    public static final RegistrySupplier<MobEffect> SLEEPINESS = EFFECTS.register("sleepiness",
            () -> new SleepinessEffect(MobEffectCategory.NEUTRAL, 0x2244AA));

    public static void register() {
        EFFECTS.register();
    }
}
