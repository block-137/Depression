package net.depression.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;


public class TextButton extends Button {
    public TextButton(int i, int j, int k, int l, Component component, OnPress onPress, CreateNarration createNarration) {
        super(i, j, k, l, component, onPress, createNarration);
    }

    public static CreateNarration getDefaultNarration() {
        return DEFAULT_NARRATION;
    }
}
