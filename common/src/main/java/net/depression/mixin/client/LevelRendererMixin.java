package net.depression.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.client.ClientPTSDManager;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow @Nullable private ClientLevel level;

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void renderFalseEntities(PoseStack poseStack, float f, long var3, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci, ProfilerFiller profilerFiller, Vec3 vec3, double d, double e, double g, Matrix4f matrix4f2, boolean bl2, Frustum frustum, float h, boolean bl3, boolean bl4, MultiBufferSource.BufferSource bufferSource) {
        if (level == null) {
            return;
        }
        String dimensionType = level.dimension().location().toString();
        if (ClientPTSDManager.falseEntities.containsKey(dimensionType)) {
            ClientPTSDManager.falseEntities.get(dimensionType).forEach(pair -> {
                Entity entity = pair.getFirst();
                BlockPos blockPos = entity.blockPosition();
                entityRenderDispatcher.render(entity, entity.getX() - d, entity.getY() - e, entity.getZ() - g, entity.getYRot(), 0, poseStack, bufferSource,
                        LightTexture.pack(entity.isOnFire() ? 15 : level.getBrightness(LightLayer.BLOCK, blockPos), level.getBrightness(LightLayer.SKY, blockPos)));
            });
        }
    }
}
