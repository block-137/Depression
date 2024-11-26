package net.depression.mixin.emotion;

import com.llamalad7.mixinextras.sugar.Local;
import net.depression.mental.MentalStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow private int badOmenLevel;
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"))
    public void onRaidWin(CallbackInfo ci, @Local(ordinal = 0) ServerPlayer serverPlayer) {
        MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(serverPlayer);
        mentalStatus.mentalHeal(4 * badOmenLevel);
    }
}
