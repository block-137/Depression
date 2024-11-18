package net.depression.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.depression.Depression;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MentalTraitButton extends ImageButton {
    protected static final WidgetSprites SPRITES = new WidgetSprites(new ResourceLocation("widget/button"), new ResourceLocation("widget/button_disabled"), new ResourceLocation("widget/button_highlighted"));
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
        super(0, 0, screen.screenHeight - 8, screen.screenHeight / 4, // i:x  j:y  k:长  l:宽 m:图标起始x坐标 n:图标起始y坐标 o:图标的y轴偏移量
                new WidgetSprites(new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/" + id + ".png"),
                        new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/" + id + ".png")), // 图标的大小
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
        Minecraft.getInstance().setScreen(new MentalTraitInfoScreen(screen, id, this.sprites.enabled()));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        int screenY = (baseY - screen.getSliderValue()); // 计算按钮在屏幕上的位置
        if (screenY + height > 0 && screenY < screen.screenHeight) { // 只渲染在屏幕内的按钮
            setX(screen.baseX);
            setY(screen.baseY + screenY);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            if (screenY < 0) { //如果按钮被上面遮挡
                if (height + screenY > 1) {  //是否要画背景 ==2画的时候会崩溃
                    guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), screen.baseY, this.getWidth(), height + screenY);
                }

                if (screenY + disFrame + 24 > 0) { //是否要画框
                    int frameStart = screenY + disFrame < 0 ? -screenY - disFrame : 0;
                    guiGraphics.blit(FRAME_LOCATION, this.getX() + disFrame, Math.max(this.getY() + disFrame, screen.baseY), 0,
                            0, frameStart, 24, 24 - frameStart, 24, 24);

                    if (screenY + dis + 16 > 0) {  //是否要画图像
                        int textureStart = screenY + dis < 0 ? -screenY - dis : 0;
                        guiGraphics.blit(this.sprites.enabled(), this.getX() + dis, Math.max(this.getY() + dis, screen.baseY), 0,
                                0, textureStart, 16, 16 - textureStart, 16, 16);

                        if (screenY + (height/2) + charHalfSize > 0) { //是否要画文字
                            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.getX() + charCenterOffset, this.getY() + (height / 2) - charHalfSize, 0xFFFFFF);
                        }
                    }
                }
            }
            else if (screenY + height > screen.screenHeight) { //如果按钮被下面遮挡
                if (screen.screenHeight - screenY > 1) { //是否要画背景 ==2画的时候会崩溃
                    guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), screen.baseY + screenY, this.getWidth(), screen.screenHeight - screenY);
                }

                if (screenY + disFrame < screen.screenHeight) { //是否要画框
                    guiGraphics.blit( FRAME_LOCATION, this.getX() + disFrame, this.getY() + disFrame, 0,
                            0, 0, 24, Math.min(24, screen.screenHeight - screenY - disFrame), 24, 24);

                    if (screenY + dis < screen.screenHeight) {  //是否要画图像
                        guiGraphics.blit(this.sprites.enabled(), this.getX() + 8, this.getY() + 8, 0,
                                0, 0, 16, Math.min(16, screen.screenHeight - screenY - dis), 16, 16);

                        if (screenY + (height/2) - charHalfSize < screen.screenHeight) { //是否要画文字
                            guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.getX() + charCenterOffset, this.getY() + (height / 2) - charHalfSize, 0xFFFFFF);
                        }
                    }
                }
            }
            else {
                guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), screen.baseY + screenY, this.getWidth(), height);
                guiGraphics.blit( FRAME_LOCATION, this.getX() + disFrame, this.getY() + disFrame, 0, 0, 24, 24, 24, 24, 24);
                guiGraphics.blit( this.sprites.enabled(), this.getX() + dis, this.getY() + dis, 0, 0, 16, 16, 16, 16, 16);
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("depression.mental_trait." + id), this.getX() + charCenterOffset, this.getY() + (height / 2) - charHalfSize, 0xFFFFFF);
            }
        }
    }
}
