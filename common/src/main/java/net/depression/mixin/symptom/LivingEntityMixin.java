package net.depression.mixin.symptom;

import net.depression.item.MedicineItem;
import net.depression.util.TempValues;
import net.depression.util.Tools;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /*修改LivingEntity的useItemRemaining成员以在玩家食欲不振的时候增加食物的使用时间
      由于其为protected，无法使用Redirect直接修改，
      写反射的话，在实际运行环境又需要换回反混淆前的名字，
      故再写一个Mixin使得ItemStack.getUseDuration()的返回值翻倍
      我知道你想说可以用Shadow，但会调用getUseDuration()的不止LivingEntity一个类（甚至可以说有很多），
      而其它的我懒得找了。所以就用了这么简陋的标记法*/
    @Inject(method = "startUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseDuration()I"))
    public void startUsingItem(InteractionHand interactionHand, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof Player) {
            ItemStack itemStack = livingEntity.getItemInHand(interactionHand);
            TempValues.playerMentalHealthLevel = Tools.getPlayerMentalHealthLevel((Player) livingEntity);
            Item item = itemStack.getItem();
            if (item.getFoodProperties() != null && !(item instanceof MedicineItem) && TempValues.playerMentalHealthLevel >= 2) { //如果吃的是食物且玩家食欲不振
                TempValues.isCalledByDepressedPlayer = true;
                return;
            }
        }
        TempValues.isCalledByDepressedPlayer = false;
    }

}
