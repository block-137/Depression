package net.depression.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.Depression;
import net.depression.mental.MentalTrait;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.awt.*;
import java.util.ArrayList;

public class MentalTraitSelectionScreen extends Screen {
    public static final ResourceLocation FRAME = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/frame_128.png");
    public static final int frameLength = 148;
    public static final int halfFrameLength = 74;
    public final int screenHeight = 128;
    public final int halfScreenHeight = 64;
    public int baseX;
    public int baseY;
    public VerticalSliderButton sliderButton;
    public ArrayList<MentalTraitButton> mentalTraitButtons = new ArrayList<>();
    public MentalTraitSelectionScreen() {
        super(Component.literal(""));
        int i = 0;
        for (MentalTrait trait : MentalTrait.mentalTraits.values()) {
            mentalTraitButtons.add(new MentalTraitButton(this, 32 * i, trait.id));
            ++i;
        }
        sliderButton = new VerticalSliderButton(0, 0, 8, screenHeight, Component.literal(""), 0, i * 32);
    }
    public int getSliderValue() {
        return sliderButton.getScreenValue();
    }
    @Override
    protected void init() {
        for (MentalTraitButton button : mentalTraitButtons) {
            addRenderableWidget(button);
        }
        addRenderableWidget(sliderButton);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        sliderButton.mouseScrolled(delta);
        return true;
    }
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        setSlider();
        renderDirtBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        int x = window.getGuiScaledWidth() / 2;
        int y = window.getGuiScaledHeight() / 2;
        RenderSystem.setShaderTexture(0, FRAME);
        blit(poseStack, x - halfFrameLength, y - halfFrameLength, 1, 0, 0, frameLength, frameLength, frameLength,frameLength);
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        drawCenteredString(poseStack, minecraft.font, Component.translatable("screen.depression.mental_trait.title"), x, y - halfFrameLength - 24, 0xFFFFFF);
        drawCenteredString(poseStack, minecraft.font, Component.translatable("screen.depression.mental_trait.subtitle"), x, y - halfFrameLength - 12, 0xFFFFFF);
    }
    private void setSlider() {
        Window window = Minecraft.getInstance().getWindow();
        int x = window.getGuiScaledWidth() / 2;
        int y = window.getGuiScaledHeight() / 2;
        baseX = x - screenHeight / 2;
        baseY = y - screenHeight / 2;
        sliderButton.setX(x + 56);
        sliderButton.setY(y - 64);
    }
}
