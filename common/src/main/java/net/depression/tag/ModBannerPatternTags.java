package net.depression.tag;

import net.depression.Depression;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

public class ModBannerPatternTags {
    public static final TagKey<BannerPattern> PATTERN_RIBBON = TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(Depression.MOD_ID, "ribbon"));
}
