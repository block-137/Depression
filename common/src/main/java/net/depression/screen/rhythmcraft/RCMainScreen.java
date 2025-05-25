package net.depression.screen.rhythmcraft;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.depression.Depression;
import net.depression.screen.ScaleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RCMainScreen extends Screen {
    private static final ResourceLocation TITLE = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/main/title.png");
    private static final ResourceLocation SUBTITLE = new ResourceLocation(Depression.MOD_ID,"textures/rc_screen/main/subtitle.png");
    private static final ResourceLocation CURTAIN = new ResourceLocation(Depression.MOD_ID,"textures/rc_screen/main/scroll_curtain.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Depression.MOD_ID,"textures/rc_screen/main/scroll_background.png");
    ScaleButton startButton;
    long initTime;
    public RCMainScreen() {
        super(Component.literal(""));
    }
    @Override
    public void init() {
        initTime = System.currentTimeMillis();
        // i:x  j:y  k:长  l:宽 m:图标起始x坐标 n:图标起始y坐标 o:图标的y轴偏移量
        startButton = new ScaleButton(0d, 0d, 1.0, 1.0, 2, SUBTITLE, (button) -> {
            Minecraft.getInstance().setScreen(new RCSelectionScreen());
        });
        addRenderableWidget(startButton);


    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Window window = Minecraft.getInstance().getWindow();
        int x = window.getGuiScaledWidth();
        int y = window.getGuiScaledHeight();
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        int curBackgroundX = (int) ((System.currentTimeMillis() - initTime) % 4000 / 4000.0 * 2 * x);
        RenderSystem.enableBlend();
        guiGraphics.blit(BACKGROUND, 0, 0, 0, curBackgroundX, 0, x, y, 2*x, y);
        int curCurtainX = (int) ((System.currentTimeMillis() - initTime) % 4000 / 4000.0 * 1.2 * x);
        RenderSystem.enableBlend();
        guiGraphics.blit(CURTAIN, 0, 0, 1, curCurtainX, 0, x, y, (int) (1.2*x), y);
        RenderSystem.enableBlend();
        guiGraphics.blit(TITLE, 0, 0, 2, 0, 0, x, y, x, y);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

}
