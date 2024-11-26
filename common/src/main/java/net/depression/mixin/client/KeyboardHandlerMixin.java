package net.depression.mixin.client;

import net.depression.client.ClientMentalIllness;
import net.depression.client.ClientMentalStatus;
import net.depression.client.DepressionClient;
import net.depression.screen.MentalTraitInfoScreen;
import net.depression.screen.UncloseableScreen;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Unique
    private static final ConcurrentHashMap<Integer, Integer> pressedKeys = new ConcurrentHashMap<>(); //key-scancode map
    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract void keyPress(long l, int i, int j, int k, int m);

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int i, CallbackInfo ci) {
        if (action == 0) {
            pressedKeys.remove(key);
            return;
        }
        if (minecraft.screen != null) {
            if (minecraft.screen instanceof UncloseableScreen) {
                ci.cancel();
                return;
            }
            return;
        }
        ClientMentalStatus mentalStatus = DepressionClient.clientMentalStatus;
        ClientMentalIllness illness = mentalStatus.mentalIllness;
        if (minecraft.player != null && mentalStatus.mentalHealthId == 3 && illness.isCloseEye && illness.elapsedTime >= -60 && illness.elapsedTime <= 60) {
            minecraft.player.playSound(SoundEvents.WOOD_BREAK);
            for (Map.Entry<Integer, Integer> entry : pressedKeys.entrySet()) {
                keyPress(window, entry.getKey(), entry.getValue(), 0, 0);
            }
            ci.cancel();
        }
        else {
            pressedKeys.put(key, scancode);
        }
    }
}
