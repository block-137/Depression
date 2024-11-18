package net.depression.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.depression.Depression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MentalTraitButton extends ImageButton {
    public static final ResourceLocation FRAME_LOCATION = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/frame_16.png");
    private final MentalTraitSelectionScreen screen;
    private final int baseY;
    private String id;
    private final int height;
    public final int dis;
    public final int disFrame;
    private final int charHalfSize = 4;
    private final int charCenterOffset;
    public MentalTraitButton(MentalTraitSelectionScreen screen, int baseY, String id) {
        super(0, 0, screen.screenHeight - 8, screen.screenHeight / 4, 0, 0, 0, // i:x  j:y  k:长  l:宽 m:图标起始x坐标 n:图标起始y坐标 o:图标的y轴偏移量
                new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/" + id + ".png"), 16, 16, // 图标的大小
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
    }

    public void onPressButton() {
        Minecraft.getInstance().setScreen(new MentalTraitInfoScreen(screen, id, this.resourceLocation));
    }

    @Override
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        int screenY = (baseY - screen.getSliderValue()); // 计算按钮在屏幕上的位置
        if (screenY + height > 0 && screenY < screen.screenHeight) { // 只渲染在屏幕内的按钮
            setX(screen.baseX);
            setY(screen.baseY + screenY);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            if (screenY < 0) { //如果按钮被上面遮挡
                if (height + screenY > 1) {  //是否要画背景 ==2画的时候会崩溃
                    blitNineSliced(poseStack, this.getX(), screen.baseY, this.getWidth(), height + screenY, 20, 4, 200, 20, 0, this.getTextureY());
                }

                if (screenY + disFrame + 24 > 0) { //是否要画框
                    int frameStart = screenY + disFrame < 0 ? -screenY - disFrame : 0;
                    renderTexture(poseStack, FRAME_LOCATION, this.getX() + disFrame, Math.max(this.getY() + disFrame, screen.baseY), 0,
                            frameStart, this.yDiffTex, 24, 24 - frameStart, 24, 24);

                    if (screenY + dis + 16 > 0) {  //是否要画图像
                        int textureStart = screenY + dis < 0 ? -screenY - dis : 0;
                        renderTexture(poseStack, this.resourceLocation, this.getX() + dis, Math.max(this.getY() + dis, screen.baseY), this.xTexStart,
                                textureStart, this.yDiffTex, 16, 16 - textureStart, this.textureWidth, this.textureHeight);

                        if (screenY + (height/2) + charHalfSize > 0) { //是否要画文字
                            drawCenteredString(poseStack, Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.getX() + charCenterOffset, this.getY() + (height / 2) - charHalfSize, 0xFFFFFF);
                        }
                    }
                }
            }
            else if (screenY + height > screen.screenHeight) { //如果按钮被下面遮挡
                if (screen.screenHeight - screenY > 1) { //是否要画背景 ==2画的时候会崩溃
                    blitNineSliced(poseStack, this.getX(), screen.baseY + screenY, this.getWidth(), screen.screenHeight - screenY, 20, 4, 200, 20, 0, this.getTextureY());
                }

                if (screenY + disFrame < screen.screenHeight) { //是否要画框
                    renderTexture(poseStack, FRAME_LOCATION, this.getX() + disFrame, this.getY() + disFrame, 0,
                            0, this.yDiffTex, 24, Math.min(24, screen.screenHeight - screenY - disFrame), 24, 24);

                    if (screenY + dis < screen.screenHeight) {  //是否要画图像
                        renderTexture(poseStack, this.resourceLocation, this.getX() + 8, this.getY() + 8, this.xTexStart,
                                this.yTexStart, this.yDiffTex, 16, Math.min(16, screen.screenHeight - screenY - dis), this.textureWidth, this.textureHeight);

                        if (screenY + (height/2) - charHalfSize < screen.screenHeight) { //是否要画文字
                            drawCenteredString(poseStack, Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.getX() + charCenterOffset, this.getY() + (height / 2) - charHalfSize, 0xFFFFFF);
                        }
                    }
                }
            }
            else {
                blitNineSliced(poseStack, this.getX(), screen.baseY + screenY, this.getWidth(), height, 20, 4, 200, 20, 0, this.getTextureY());
                renderTexture(poseStack, FRAME_LOCATION, this.getX() + disFrame, this.getY() + disFrame, 0, 0, this.yDiffTex, 24, 24, 24, 24);
                renderTexture(poseStack, this.resourceLocation, this.getX() + dis, this.getY() + dis, this.xTexStart, this.yTexStart, this.yDiffTex, 16, 16, this.textureWidth, this.textureHeight);
                drawCenteredString(poseStack,Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.getX() + charCenterOffset, this.getY() + (height / 2) - charHalfSize, 0xFFFFFF);
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
