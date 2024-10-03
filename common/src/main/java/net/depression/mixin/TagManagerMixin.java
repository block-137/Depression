package net.depression.mixin;

import net.depression.world.VillageAdditions;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TagManager.class)
public abstract class TagManagerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(RegistryAccess registryAccess, CallbackInfo ci) {
        VillageAdditions.init(registryAccess);
    }
}
