package net.depression.world.dimension;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public class ModDimensionTypes {
    public static final ResourceKey<DimensionType> CHART_TYPE =
            ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(Depression.MOD_ID, "chart"));

}
