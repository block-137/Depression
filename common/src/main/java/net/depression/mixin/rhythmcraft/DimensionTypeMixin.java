package net.depression.mixin.rhythmcraft;

import net.depression.world.dimension.ModDimensions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin {
    @Inject(method = "getStorageFolder", at = @At("HEAD"), cancellable = true)
    private static void getStorageFolder(ResourceKey<Level> resourceKey, Path path, CallbackInfoReturnable<Path> cir) {
        if (resourceKey == ModDimensions.CHART) {
            cir.setReturnValue(path);
        }
    }
}
