package net.depression.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.client.ClientPTSDManager;
import net.depression.client.DepressionClient;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.rhythmcraft.PlayingChart;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow @Nullable private ClientLevel level;

    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow @Final private RenderBuffers renderBuffers;

    @Shadow protected abstract boolean shouldShowEntityOutlines();

    @Shadow @Nullable private PostChain entityEffect;

    @Shadow @Final private Minecraft minecraft;
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"))
    private void renderFalseEntities(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        if (level == null) {
            return;
        }
        String dimensionType = level.dimension().location().toString();
        Vec3 vec3 = camera.getPosition();
        double d = vec3.x();
        double e = vec3.y();
        double g = vec3.z();
        if (ClientPTSDManager.falseEntities.containsKey(dimensionType)) {
            ClientPTSDManager.falseEntities.get(dimensionType).forEach(pair -> {
                Entity entity = pair.getFirst();
                BlockPos blockPos = entity.blockPosition();
                entityRenderDispatcher.render(entity, entity.getX() - d, entity.getY() - e, entity.getZ() - g, entity.getYRot(), 0, poseStack, renderBuffers.bufferSource(),
                        LightTexture.pack(entity.isOnFire() ? 15 : level.getBrightness(LightLayer.BLOCK, blockPos), level.getBrightness(LightLayer.SKY, blockPos)));
            });
        }
        /*
        ClientPlayingChart clientPlayingChart = DepressionClient.playingChart;
        if (clientPlayingChart != null && clientPlayingChart.highlightedNotes != null) {
            for (Shulker shulker : clientPlayingChart.highlightedNotes.values()) {
                BlockPos blockPos = shulker.blockPosition();
                OutlineBufferSource outlineBufferSource = renderBuffers.outlineBufferSource();
                int i = shulker.getTeamColor();
                outlineBufferSource.setColor(FastColor.ARGB32.red(i), FastColor.ARGB32.green(i), FastColor.ARGB32.blue(i), 255);
                entityRenderDispatcher.render(shulker, shulker.getX() - d, shulker.getY() - e, shulker.getZ() - g, shulker.getYRot(), 0, poseStack, outlineBufferSource,
                        LightTexture.pack(15, level.getBrightness(LightLayer.SKY, blockPos)));

            }
        }*/
    }
}
