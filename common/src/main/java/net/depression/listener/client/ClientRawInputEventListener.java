package net.depression.listener.client;

import dev.architectury.event.EventResult;
import net.depression.client.ClientMentalIllness;
import net.depression.client.ClientMentalStatus;
import net.depression.client.DepressionClient;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public class ClientRawInputEventListener {
    public static EventResult onInput(Minecraft minecraft, Integer button) {
        if (minecraft.screen != null) {
            return EventResult.pass();
        }
        ClientMentalStatus mentalStatus = DepressionClient.clientMentalStatus;
        ClientMentalIllness illness = mentalStatus.mentalIllness;
        if (mentalStatus.mentalHealthId == 3 && illness.isCloseEye && illness.elapsedTime >= -60 && illness.elapsedTime <= 60) {
            minecraft.player.playSound(SoundEvents.WOOD_BREAK);
            return EventResult.interruptFalse();
        }
        else {
            if (button != null) {
                ClientTickEventListener.pressedButtons.add(button);
            }
            return EventResult.pass();
        }
    }

    public static EventResult onMouseScrolled(Minecraft minecraft, double v) {
        return onInput(minecraft, null);
    }

    public static EventResult onMouseClicked(Minecraft minecraft, int button, int action, int mods) {
        if (button == 1) {
            return EventResult.pass();
        }
        if (action != 0) {
            return onInput(minecraft, button);
        }
        else {
            ClientTickEventListener.pressedButtons.remove(button);
        }
        return EventResult.pass();
    }
}
