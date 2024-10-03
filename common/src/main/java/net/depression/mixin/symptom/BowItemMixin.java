package net.depression.mixin.symptom;

import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class BowItemMixin {
    @Inject(method = "releaseUsing", at = @At("TAIL"))
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) livingEntity;
            if (player.isCreative()) {
                return;
            }
            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
            if (mentalStatus == null) {
                mentalStatus = new MentalStatus(player);
                Registry.mentalStatus.put(player.getUUID(), mentalStatus);
            }
            mentalStatus.mentalIllness.trigMentalFatigue();
        }
    }
}
