package net.depression.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;


public class TextButton extends Button {
    public TextButton(int i, int j, int k, int l, Component component, OnPress onPress) {
        super(i, j, k, l, component, onPress, NO_TOOLTIP);
    }

}
