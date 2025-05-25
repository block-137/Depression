package net.depression.world.dimension;

import dev.architectury.registry.registries.DeferredRegister;
import net.depression.Depression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

import java.awt.*;


public class ModDimensions {
    public static final ResourceKey<Level> CHART =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(Depression.MOD_ID, "chart"));
    public static final ResourceKey<LevelStem> CHART_STEM =
            ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation(Depression.MOD_ID, "chart"));
    public static final ResourceKey<Level> TEST =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(Depression.MOD_ID, "test"));
}
