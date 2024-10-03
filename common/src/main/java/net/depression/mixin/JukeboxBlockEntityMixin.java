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
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin {
    @Unique
    private static long depression$tickCount = -1;
    @Inject(method = "playRecordTick", at = @At("HEAD"))
    private static void playRecordTick(Level level, BlockPos blockPos, BlockState blockState, JukeboxBlockEntity jukebox, CallbackInfo ci) {
        if (level.isClientSide())
            return;
        if (++depression$tickCount % 20 != 0)
            return;
        if (jukebox.getRecord().isEmpty()) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.position().distanceTo(Vec3.atCenterOf(jukebox.getBlockPos())) <= 65) {
                MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                if (mentalStatus == null) {
                    mentalStatus = new MentalStatus(player);
                    Registry.mentalStatus.put(player.getUUID(), mentalStatus);
                }
                mentalStatus.mentalHeal(jukebox.getRecord().getItem().arch$registryName().toString(), 1);
                MentalStatusPacket.sendToPlayer(player, mentalStatus);
            }
        }
    }
}
