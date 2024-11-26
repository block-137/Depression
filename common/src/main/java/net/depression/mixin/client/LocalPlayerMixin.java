package net.depression.mixin.client;

import net.depression.client.ClientMentalIllness;
import net.depression.client.DepressionClient;
import net.depression.client.screen.DiaryAccess;
import net.depression.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "openItemGui", at = @At("TAIL"))
    private void openItemGui(ItemStack itemStack, InteractionHand interactionHand, CallbackInfo ci) {
        if (itemStack.is(ModItems.DIARY.get())) {
            Minecraft.getInstance().setScreen(new BookViewScreen(new DiaryAccess(itemStack)));
        }
    }
}
