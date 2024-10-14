package net.depression.sound;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Depression.MOD_ID, Registry.SOUND_EVENT_REGISTRY);
    public static final RegistrySupplier<SoundEvent> TYPING = SOUNDS.register("typing",
            () -> new SoundEvent(new ResourceLocation(Depression.MOD_ID, "typing")));
    public static final RegistrySupplier<SoundEvent> PANT = SOUNDS.register("pant",
            () -> new SoundEvent(new ResourceLocation(Depression.MOD_ID, "pant")));
    public static final RegistrySupplier<SoundEvent> HEARTBEATS = SOUNDS.register("heartbeats",
            () -> new SoundEvent(new ResourceLocation(Depression.MOD_ID, "heartbeats")));
    public static final RegistrySupplier<SoundEvent> TINNITUS = SOUNDS.register("tinnitus",
            () -> new SoundEvent(new ResourceLocation(Depression.MOD_ID, "tinnitus")));

    public static void register() {
        SOUNDS.register();
    }
}
