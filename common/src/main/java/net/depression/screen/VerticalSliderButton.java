package net.depression.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.Depression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VerticalSliderButton extends AbstractSliderButton {
    public static final ResourceLocation SCROLL_BAR_BASE = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/scroll_bar_base.png");
    public static final ResourceLocation SCROLL_BAR = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/scroll_bar.png");
    public static final ResourceLocation SCROLL_BAR_HOVER = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/scroll_bar_hover.png");
    private int pixelValue; //滑块滑动的像素值
    private int screenValue; //滑块滑动的屏幕值
    private final int maxScreenValue;
    private final int sliderHeight;
    private final int halfSliderHeight;
    public VerticalSliderButton(int i, int j, int k, int l, Component component, double d, int maxScreenValue) {
        super(i, j, k, l, component, d);
        this.sliderHeight = height * height / maxScreenValue;
        this.maxScreenValue = maxScreenValue - height;
        this.halfSliderHeight = sliderHeight / 2;
    }
    @Override
    protected void updateMessage() {}
    @Override
    protected void applyValue() {
        pixelValue = (int) (value * (this.height - sliderHeight));
        screenValue = (int) (value * maxScreenValue);
    }
    public int getScreenValue() {
        return screenValue;
    }
    @Override
    public void renderBg(PoseStack poseStack, Minecraft minecraft, int i, int j) {
        RenderSystem.setShaderTexture(0, SCROLL_BAR_BASE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(poseStack, this.x , this.y, 0,0, 0, this.width, this.height, 8, 256);
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        RenderSystem.setShaderTexture(0, isHoveredOrFocused() ? SCROLL_BAR_HOVER : SCROLL_BAR);
        blit(poseStack, this.x , this.y + pixelValue, 1,0, 0, this.width, sliderHeight, 8, 256);
    }
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseY);
    }
    @Override
    public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.setValueFromMouse(mouseY);
    }
    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
    }
    public void mouseScrolled(double delta) {
        this.value = Math.max(0, Math.min(this.value + (delta > 0 ? -0.1 : 0.1), 1));
        this.applyValue();
    }

    private void setValueFromMouse(double mouseY) {
        double sliderMin = this.y + this.halfSliderHeight;
        double sliderMax = this.y + this.height - this.halfSliderHeight;
        value = (mouseY - sliderMin) / (sliderMax - sliderMin);
        value = Math.max(0, Math.min(value, 1));
        this.applyValue();
    }
}
