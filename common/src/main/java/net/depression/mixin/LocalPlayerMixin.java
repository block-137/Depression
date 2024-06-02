package net.depression.mixin;

import dev.architectury.event.EventResult;
import net.depression.Depression;
import net.depression.client.ClientMentalIllness;
import net.depression.client.DepressionClient;
import net.depression.listener.ClientRawInputEventListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "move", at = @At("TAIL"))
    private void move(MoverType moverType, Vec3 vec3, CallbackInfo ci) {
        if (moverType == MoverType.SELF) {
            ClientMentalIllness illness = DepressionClient.clientMentalStatus.mentalIllness;
            if (illness.isCloseEye && illness.elapsedTime >= -60 && illness.elapsedTime <= 60) {
                LocalPlayer player = (LocalPlayer) (Object) this;
                player.moveTo(ClientMentalIllness.curPosition);
            }
        }
    }
}
