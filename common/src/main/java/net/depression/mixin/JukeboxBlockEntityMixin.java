package net.depression.mixin;

import net.depression.mental.MentalStatus;
import net.depression.network.MentalStatusPacket;
import net.depression.server.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin {
    @Unique
    private long tickCount = -1;
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(Level level, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (level.isClientSide())
            return;
        if (++tickCount % 20 != 0)
            return;
        JukeboxBlockEntity jukebox = (JukeboxBlockEntity) (Object) this;
        if (!jukebox.isRecordPlaying()) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.position().distanceTo(jukebox.getBlockPos().getCenter()) <= 65) {
                MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                if (mentalStatus == null) {
                    mentalStatus = new MentalStatus(player);
                    Registry.mentalStatus.put(player.getUUID(), mentalStatus);
                }
                mentalStatus.mentalHeal(jukebox.getFirstItem().getItem().arch$registryName().toString(), 1);
                MentalStatusPacket.sendToPlayer(player, mentalStatus);
            }
        }
    }
}
