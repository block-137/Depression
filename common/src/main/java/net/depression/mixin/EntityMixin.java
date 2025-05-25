package net.depression.mixin;

import net.depression.Depression;
import net.depression.mental.MentalStatus;
import net.depression.rhythmcraft.PlayingChart;
import net.depression.server.Registry;
import net.depression.util.TempValues;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @Shadow public abstract void tick();

    @Shadow public abstract boolean is(Entity entity);

    @Shadow @Final private EntityType<?> type;

    @Inject(method = "playStepSound", at = @At("HEAD"))
    private void playStepSound(CallbackInfo ci) {
        TempValues.isStepSound = true;
    }
    @Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void playSound(CallbackInfo ci) {
        if (level().isClientSide()) {
            return;
        }
        if (TempValues.isStepSound) { //如果是脚步声的话就不触发PTSD，因为无法判断是什么生物
            TempValues.isStepSound = false;
            return;
        }
        TempValues.broadcastEntity = (Entity) (Object) this;
    }

    @Inject(method = "save", at = @At("HEAD"), cancellable = true)
    private void onSave(CompoundTag compoundTag, CallbackInfoReturnable<Boolean> cir) {
        if (level().isClientSide()) {
            return;
        }
        if (level() instanceof PlayingChart && type == EntityType.MINECART) {
            cir.setReturnValue(false);
        }
    }
}
