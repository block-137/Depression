package net.depression.mixin.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSliderButton.class)
public abstract interface AbstractSliderButtonMixin {
    @Invoker("getTextureY")
    public int invokeGetTextureY();
    @Invoker("getHandleTextureY")
    public int invokeGetHandleTextureY();
}
