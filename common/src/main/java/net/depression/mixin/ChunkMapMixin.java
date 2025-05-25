package net.depression.mixin;

import net.depression.Depression;
import net.depression.rhythmcraft.PlayingChart;
import net.depression.server.Registry;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    @Shadow @Final private ServerLevel level;

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    public void onSave(ChunkAccess chunkAccess, CallbackInfoReturnable<Boolean> cir) {
        if (this.level instanceof PlayingChart playingChart
                && !playingChart.isEditMode) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(ServerPlayer serverPlayer, CallbackInfo ci) {
        if (Registry.isPending(serverPlayer)) {
            Registry.addPendingChunkMap(serverPlayer, (ChunkMap) (Object) this);
            ci.cancel();
        }
    }
}
