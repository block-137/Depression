package net.depression.listener.client;

import dev.architectury.event.EventResult;
import net.depression.client.ClientMentalIllness;
import net.depression.client.ClientMentalStatus;
import net.depression.client.DepressionClient;
import net.depression.mental.MentalStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public class ClientRawInputEventListener {
    public static EventResult onInput(Minecraft minecraft) {
        if (minecraft.screen != null) {
            return EventResult.pass();
        }
        ClientMentalStatus mentalStatus = DepressionClient.clientMentalStatus;
        ClientMentalIllness illness = mentalStatus.mentalIllness;
        if (mentalStatus.mentalHealthLevel == 3 && illness.isCloseEye && illness.elapsedTime >= -60 && illness.elapsedTime <= 60) {
            minecraft.player.playSound(SoundEvents.WOOD_BREAK);
            return EventResult.interruptFalse();
        }
        else {
            return EventResult.pass();
        }
    }

    public static EventResult onMouseScrolled(Minecraft minecraft, double v, double v1) {
        return onInput(minecraft);
    }

    public static EventResult onMouseClicked(Minecraft minecraft, int button, int action, int mods) {
        if (action != 0) {
            return onInput(minecraft);
        }
        return EventResult.pass();
    }

}
