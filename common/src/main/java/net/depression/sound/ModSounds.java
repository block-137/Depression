package net.depression.sound;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Depression.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> WRITE_DIARY = SOUNDS.register("write_diary",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Depression.MOD_ID, "write_diary")));

    public static void register() {
        SOUNDS.register();
    }
}
