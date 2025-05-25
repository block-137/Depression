package net.depression.mixin.rhythmcraft;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Lifecycle;
import net.depression.rhythmcraft.PlayingChart;
import net.depression.world.dimension.ModDimensions;
import net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow @Final private Map<ResourceKey<Level>, ServerLevel> levels;

    @Shadow @Final protected WorldData worldData;

    @Shadow @Final private Executor executor;

    @Shadow @Final protected LevelStorageSource.LevelStorageAccess storageSource;

    protected MinecraftServerMixin() {
    }

    @Shadow public abstract RegistryAccess.Frozen registryAccess();

    @Shadow private boolean allowFlight;
    @Shadow public abstract boolean isSingleplayer();

    @Shadow private PlayerList playerList;

    @Shadow public abstract boolean isSingleplayerOwner(GameProfile gameProfile);

    @Shadow @Nullable public abstract GameProfile getSingleplayerProfile();

    @Inject(method = "saveAllChunks", at = @At(value = "RETURN"))
    private void onSaveAllChunks(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
        for (PlayingChart playingChart : PlayingChart.playingCharts.values()) {
            playingChart.save(null, bl2, playingChart.noSave && !bl3);
        }
    }
    @ModifyArg(method = "saveAllChunks", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;saveDataTag(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/storage/WorldData;Lnet/minecraft/nbt/CompoundTag;)V", ordinal = 0)
            , index = 2)
    private CompoundTag onSaveSinglePlayerData(CompoundTag compoundTag) {
        GameProfile gameProfile = this.getSingleplayerProfile();
        if (gameProfile == null) {
            return compoundTag;
        }
        if (PlayingChart.playerData.containsKey(gameProfile.getId())) {
            return PlayingChart.playerData.get(gameProfile.getId());
        }
        return compoundTag;
    }
    @Inject(method = "createLevels", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/border/WorldBorder;applySettings(Lnet/minecraft/world/level/border/WorldBorder$Settings;)V"))
    private void getArguments(ChunkProgressListener chunkProgressListener, CallbackInfo ci) {
        Registry<LevelStem> registry = this.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
        LevelStem levelStem = registry.get(ModDimensions.CHART_STEM);
        RandomSequences randomSequences = this.levels.get(Level.OVERWORLD).getRandomSequences();
        PlayingChart.server = (MinecraftServer) (Object) this;
        PlayingChart.executor = this.executor;
        PlayingChart.worldData = this.worldData;
        PlayingChart.chartStem = levelStem;
        PlayingChart.chunkProgressListener = chunkProgressListener;
        PlayingChart.randomSequences = randomSequences;
    }

    @Inject(method = "tickChildren", at = @At(value = "HEAD"))
    private void tickPlayingCharts(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        for (PlayingChart playingChart : PlayingChart.playingCharts.values()) {
            playingChart.tick(booleanSupplier);
        }
    }

    @Inject(method = "pollTaskInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getAllLevels()Ljava/lang/Iterable;"), cancellable = true)
    private void pollTaskPlayingCharts(CallbackInfoReturnable<Boolean> cir) {
        for (PlayingChart playingChart : PlayingChart.playingCharts.values()) {
            if (playingChart.getChunkSource().pollTask()) {
                cir.setReturnValue(true);
            }
        }
    }
}
