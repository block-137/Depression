package net.depression.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.Depression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MentalTraitButton extends ImageButton {
    public static final ResourceLocation BUTTON_LOCATION = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/button.png");
    public static final ResourceLocation FRAME_LOCATION = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/frame_16.png");
    private final MentalTraitSelectionScreen screen;
    private final int baseY;
    private String id;
    private final int height;
    public final int dis;
    public final int disFrame;
    private final int charHalfSize = 4;
    private final int charCenterOffset;
    private ResourceLocation resourceLocation;

    public MentalTraitButton(MentalTraitSelectionScreen screen, int baseY, String id) {
        super(0, 0, screen.screenHeight - 8, screen.screenHeight / 4, 0, 0, 0, // i:x  j:y  k:长  l:宽 m:图标起始x坐标 n:图标起始y坐标 o:图标的y轴偏移量
                null, 16, 16, // 图标的大小
                (button) -> ((MentalTraitButton) button).onPressButton(),
                Component.translatable("depression.mental_trait." + id));
        this.baseY = baseY;
        this.screen = screen;
        this.width = screen.screenHeight - 8;
        this.height = screen.screenHeight / 4;
        this.dis = (height - 16) / 2;
        this.disFrame = (height - 24) / 2;
        this.id = id;
        this.charCenterOffset = disFrame + 24 + (this.getWidth() - disFrame - 24) / 2;
        this.resourceLocation = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/" + id + ".png");
    }

    public void onPressButton() {
        Minecraft.getInstance().setScreen(new MentalTraitInfoScreen(screen, id, this.resourceLocation));
    }

    @Override
    public void renderButton(PoseStack poseStack, int i, int j, float f) {
        int screenY = (baseY - screen.getSliderValue()); // 计算按钮在屏幕上的位置
        if (screenY + height > 0 && screenY < screen.screenHeight) { // 只渲染在屏幕内的按钮
            x = screen.baseX;
            y = screen.baseY + screenY;
            int k = this.isHoveredOrFocused() ? 32 : 0;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BUTTON_LOCATION);
            if (screenY < 0) { //如果按钮被上面遮挡
                if (height + screenY > 1) {  //是否要画背景 ==2画的时候会崩溃
                    blit(poseStack, x, screen.baseY, 0, 0, k, this.getWidth(), height + screenY, 120, 64);
                    //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
                }
                if (screenY + disFrame + 24 > 0) { //是否要画框
                    int frameStart = screenY + disFrame < 0 ? -screenY - disFrame : 0;
                    RenderSystem.setShaderTexture(0, FRAME_LOCATION);
                    blit(poseStack, this.x + disFrame, Math.max(this.y + disFrame, screen.baseY), 0,
                            0, frameStart, 24, 24 - frameStart, 24, 24);

                    if (screenY + dis + 16 > 0) {  //是否要画图像
                        int textureStart = screenY + dis < 0 ? -screenY - dis : 0;
                        RenderSystem.setShaderTexture(0, resourceLocation);
                        blit(poseStack, this.x + dis, Math.max(this.y + dis, screen.baseY), 0,
                                0, textureStart, 16, 16 - textureStart, 16, 16);

                        if (screenY + (height/2) + charHalfSize > 0) { //是否要画文字
                            drawCenteredString(poseStack, Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.x + charCenterOffset, this.y + (height / 2) - charHalfSize, 0xFFFFFF);
                        }
                    }
                }
            }
            else if (screenY + height > screen.screenHeight) { //如果按钮被下面遮挡
                if (screen.screenHeight - screenY > 1) { //是否要画背景 ==2画的时候会崩溃
                    blit(poseStack, x, screen.baseY + screenY, 0, 0, k, this.getWidth(), screen.screenHeight - screenY, 120, 64);
                }

                if (screenY + disFrame < screen.screenHeight) { //是否要画框
                    RenderSystem.setShaderTexture(0, FRAME_LOCATION);
                    blit(poseStack, this.x + disFrame, this.y + disFrame, 0,
                            0, 0, 24, Math.min(24, screen.screenHeight - screenY - disFrame), 24, 24);

                    if (screenY + dis < screen.screenHeight) {  //是否要画图像
                        RenderSystem.setShaderTexture(0, resourceLocation);
                        blit(poseStack,this.x + 8, this.y + 8, 0,
                                0, 0, 16, Math.min(16, screen.screenHeight - screenY - dis), 16, 16);

                        if (screenY + (height/2) - charHalfSize < screen.screenHeight) { //是否要画文字
                            drawCenteredString(poseStack, Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.x + charCenterOffset, this.y + (height / 2) - charHalfSize, 0xFFFFFF);
                        }
                    }
                }
            }
            else {
                blit(poseStack, x, screen.baseY + screenY, 0, 0, k, this.getWidth(), height, 120, 64);
                RenderSystem.setShaderTexture(0, FRAME_LOCATION);
                blit(poseStack,  x + disFrame, y + disFrame, 0, 0, 0, 24, 24, 24, 24);
                RenderSystem.setShaderTexture(0, resourceLocation);
                blit(poseStack, x + dis, y + dis, 0, 0, 0, 16, 16, 16, 16);
                drawCenteredString(poseStack, Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.x + charCenterOffset, this.y + (height / 2) - charHalfSize, 0xFFFFFF);
            }
        }
    }
    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        return 46 + i * 20;
    }
}
