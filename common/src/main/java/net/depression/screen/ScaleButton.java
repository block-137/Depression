package net.depression.screen;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class ScaleButton extends Button {
    private ResourceLocation resourceLocation;
    private int priority;
    private double xScale;
    private double yScale;
    private double widthScale;
    private double heightScale;
    public ScaleButton(double i, double j, double k, double l, int priority, ResourceLocation resourceLocation, Button.OnPress onPress) {
        super(0, 0, 0, 0, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.xScale = i;
        this.yScale = j;
        this.widthScale = k;
        this.heightScale = l;
        this.resourceLocation = resourceLocation;
        this.priority = priority;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Window window = Minecraft.getInstance().getWindow();
        int windowX = window.getGuiScaledWidth();
        int windowY = window.getGuiScaledHeight();
        this.setX((int) (windowX * xScale));
        this.setY((int) (windowY * yScale));
        this.width = (int) (windowX * widthScale);
        this.height = (int) (windowY * heightScale);
        if (resourceLocation != null) {
            guiGraphics.blit(resourceLocation, getX(), getY(), priority, 0, 0, this.width, this.height, this.width, this.height);
        }
    }
}
