package net.depression.mixin;

import net.depression.client.DepressionClient;
import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.depression.util.TempValues;
import net.depression.util.Tools;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /*修改LivingEntity的useItemRemaining成员以在玩家食欲不振的时候增加食物的使用时间
      由于其为protected，无法使用Redirect直接修改，
      写反射的话，在实际运行环境又需要换回反混淆前的名字，
      故再写一个Mixin使得ItemStack.getUseDuration()的返回值翻倍 */
    @Inject(method = "startUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I"))
    public void startUsingItem(InteractionHand interactionHand, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof Player) {
            ItemStack itemStack = livingEntity.getItemInHand(interactionHand);
            TempValues.playerMentalHealthLevel = Tools.getPlayerMentalHealthLevel((Player) livingEntity);
            if (itemStack.getItem().getFoodProperties() != null && TempValues.playerMentalHealthLevel >= 2) { //如果吃的是食物且玩家食欲不振
                TempValues.isCalledByDepressedPlayer = true;
            }
            else {
                TempValues.isCalledByDepressedPlayer = false;
            }
        }
    }

}
