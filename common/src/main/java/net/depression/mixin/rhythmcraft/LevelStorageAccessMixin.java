package net.depression.mixin.rhythmcraft;

import net.minecraft.util.DirectoryLock;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public abstract class LevelStorageAccessMixin {
    @Shadow @Final private DirectoryLock lock;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(LevelStorageSource levelStorageSource, String string, Path path, CallbackInfo ci) throws IOException {
        this.lock.close();
    }

    @Inject(method = "checkLock", at = @At("HEAD"), cancellable = true)
    public void onCheckLock(CallbackInfo ci) {
        ci.cancel();
    }
}
