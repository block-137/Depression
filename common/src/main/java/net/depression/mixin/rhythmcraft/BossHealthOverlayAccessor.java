package net.depression.mixin.rhythmcraft;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BossHealthOverlay.class)
public interface BossHealthOverlayAccessor {
    @Invoker("drawBar")
    public void invokeDrawBar(GuiGraphics guiGraphics, int i, int j, BossEvent bossEvent);
}
