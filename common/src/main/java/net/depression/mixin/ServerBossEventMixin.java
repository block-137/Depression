package net.depression.mixin;

import net.depression.server.Registry;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerBossEvent.class)
public abstract class ServerBossEventMixin {
    @Inject(method = "addPlayer", at = @At("HEAD"))
    private void onAddPlayer(ServerPlayer serverPlayer, CallbackInfo ci) {
        Registry.eventAddPlayer((ServerBossEvent) (Object) this, serverPlayer);
    }
    @Inject(method = "removePlayer", at = @At("HEAD"))
    private void onRemovePlayer(ServerPlayer serverPlayer, CallbackInfo ci) {
        Registry.eventRemovePlayer((ServerBossEvent) (Object) this, serverPlayer);
    }
}
