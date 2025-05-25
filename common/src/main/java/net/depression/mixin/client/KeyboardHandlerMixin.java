package net.depression.mixin.client;

import net.depression.Depression;
import net.depression.client.ClientMentalIllness;
import net.depression.client.ClientMentalStatus;
import net.depression.client.DepressionClient;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.listener.client.ClientTickEventListener;
import net.depression.network.RhythmCraftPacket;
import net.depression.screen.MentalTraitInfoScreen;
import net.depression.screen.UncloseableScreen;
import net.depression.util.OggStreamPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int i, CallbackInfo ci) {
        if (action == 0) {
            ClientTickEventListener.pressedKeys.remove(key);
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
            if (!ClientTickEventListener.pressedKeys.containsKey(key)) {
                minecraft.player.playSound(SoundEvents.WOOD_BREAK);
            }
            ci.cancel();
        }
        else {
            ClientTickEventListener.pressedKeys.put(key, scancode);
        }
    }
}
