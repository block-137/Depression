package net.depression.mixin;

import net.depression.util.TempValues;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getUseDuration", at = @At("TAIL"), cancellable = true)
    public void getUseDuration(CallbackInfoReturnable<Integer> cir) {
        if (TempValues.isCalledByDepressedPlayer) { //这里的isCalledByDepressedPlayer不能重置为false，因为ItemStack.getUseDuration()会被多次调用，不重置才会正常显示动画
            ItemStack itemStack = (ItemStack) (Object) this;
            cir.setReturnValue(itemStack.getItem().getUseDuration(itemStack) * TempValues.playerMentalHealthLevel);
        }
    }
}
