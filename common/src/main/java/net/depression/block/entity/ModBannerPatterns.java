package net.depression.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;

public class ModBannerPatterns {

    public static final DeferredRegister<BannerPattern> BANNER_PATTERNS = DeferredRegister.create(Depression.MOD_ID, Registry.BANNER_PATTERN_REGISTRY);
    public static final RegistrySupplier<BannerPattern> RIBBON = BANNER_PATTERNS.register("ribbon", () -> new BannerPattern("depression:ribbon"));

    public static void register() {
        BANNER_PATTERNS.register();
    }

}
