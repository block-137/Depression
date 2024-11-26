package net.depression.mixin;

import net.depression.server.Registry;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(ServerPlayer serverPlayer, CallbackInfo ci) {
        if (Registry.isPending(serverPlayer)) {
            Registry.addPendingChunkMap(serverPlayer, (ChunkMap) (Object) this);
            ci.cancel();
        }
    }
}
