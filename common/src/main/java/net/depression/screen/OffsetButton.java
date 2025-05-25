package net.depression.screen;


import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OffsetButton extends Button {
    private Supplier<ResourceLocation> supplier;
    private boolean displayBackground;
    private int priority;
    private PosOrigin xOrigin;
    private int xOffset;
    private PosOrigin yOrigin;
    private int yOffset;
    private BiConsumer<OffsetButton, GuiGraphics> renderFunction;
    public OffsetButton(PosOrigin xOrigin, int i, PosOrigin yOrigin, int j, int k, int l, int priority, Supplier<ResourceLocation> supplier, boolean displayBackground, OnPress onPress) {
        super(0, 0, k, l, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.xOffset = i;
        this.yOffset = j;
        this.supplier = supplier;
        this.displayBackground = displayBackground;
        this.priority = priority;
    }

    public OffsetButton(PosOrigin xOrigin, int i, PosOrigin yOrigin, int j, int k, int l, int priority, Supplier<ResourceLocation> supplier, boolean displayBackground, OnPress onPress, BiConsumer<OffsetButton, GuiGraphics> renderFunction) {
        super(0, 0, k, l, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.xOrigin = xOrigin;
        this.yOrigin = yOrigin;
        this.xOffset = i;
        this.yOffset = j;
        this.supplier = supplier;
        this.displayBackground = displayBackground;
        this.priority = priority;
        this.renderFunction = renderFunction;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return false;
    }
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Window window = Minecraft.getInstance().getWindow();
        int windowX = window.getGuiScaledWidth();
        int windowY = window.getGuiScaledHeight();
        switch (xOrigin) {
            case ZERO:
                this.setX(xOffset);
                break;
            case MID:
                this.setX(windowX / 2 + xOffset);
                break;
            case END:
                this.setX(windowX + xOffset);
                break;
        }
        switch (yOrigin) {
            case ZERO:
                this.setY(yOffset);
                break;
            case MID:
                this.setY(windowY / 2 + yOffset);
                break;
            case END:
                this.setY(windowY + yOffset);
                break;
        }
        if (displayBackground) {
            super.renderWidget(guiGraphics, i, j, f);
        }
        if (renderFunction != null) {
            renderFunction.accept(this, guiGraphics);
        }
        if (supplier.get() != null) {
            guiGraphics.blit(supplier.get(), getX(), getY(), priority, 0, 0, this.width, this.height, this.width, this.height);
        }
    }
}
