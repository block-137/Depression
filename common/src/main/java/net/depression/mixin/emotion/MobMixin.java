package net.depression.mixin.emotion;

import net.depression.mental.MentalStatus;
import net.depression.network.ActionbarHintPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (mob.level().isClientSide()) {
            return;
        }
        if (mob instanceof TamableAnimal tamableAnimal && mob.tickCount % 20 == 0) {
            if (tamableAnimal.isTame() && tamableAnimal.getOwner() instanceof ServerPlayer serverPlayer
                    && mob.position().distanceTo(serverPlayer.position()) <= 4d) {
                MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(serverPlayer);
                double healValue = mentalStatus.mentalHeal(tamableAnimal.getEncodeId(), 1.5);
                if (healValue > 0.5) {
                    ActionbarHintPacket.sendPetHealPacket(serverPlayer, tamableAnimal.getType().getDescription());
                }
            }
        }
    }
}
