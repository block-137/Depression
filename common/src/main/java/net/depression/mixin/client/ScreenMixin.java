package net.depression.mixin.client;

import net.depression.client.DepressionClient;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow protected abstract <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T guiEventListener);

    @Inject(method = "init*", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        ClientPlayingChart playingChart = DepressionClient.playingChart;
        if (playingChart != null && Minecraft.getInstance().level != null && playingChart.isEditMode && playingChart.isPlaying) {
            this.addRenderableWidget(playingChart.songProgressSlider);
            this.addRenderableWidget(playingChart.pauseButton);
            this.addRenderableWidget(playingChart.forwardButton);
            this.addRenderableWidget(playingChart.backwardButton);
        }
    }
}
