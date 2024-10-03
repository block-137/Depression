package net.depression.effect;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Depression.MOD_ID, Registry.MOB_EFFECT_REGISTRY);

    public static final RegistrySupplier<MobEffect> ANTI_DEPRESSION = EFFECTS.register("anti_depression",
            () -> new AntiDepressionEffect(MobEffectCategory.NEUTRAL, 0xF4E4B2));

    public static void register() {
        EFFECTS.register();
    }
}
