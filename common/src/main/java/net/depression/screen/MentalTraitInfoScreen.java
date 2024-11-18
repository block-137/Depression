package net.depression.screen;

import com.mojang.blaze3d.platform.Window;
import net.depression.Depression;
import net.depression.client.ClientMentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.network.MentalTraitPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MentalTraitInfoScreen extends Screen {
    public static final ResourceLocation FRAME = new ResourceLocation(Depression.MOD_ID, "textures/mental_trait/info_frame.png");
    private final MentalTraitSelectionScreen screen;
    private TextButton returnButton;
    private TextButton confirmButton;
    private final String id;
    private final ResourceLocation resourceLocation;
    private final ArrayList<String> drawString = new ArrayList<>();
    private static final int frameWidth = 212;
    private static final int frameHeight = 116;
    private static final int halfFrameWidth = 106;
    private static final int halfFrameHeight = 58;
    private static final int buttonWidth = 60;
    private static final int buttonHeight = 24;
    protected MentalTraitInfoScreen(MentalTraitSelectionScreen screen, String id, ResourceLocation resourceLocation) {
        super(Component.literal(""));
        this.screen = screen;
        this.id = id;
        this.resourceLocation = resourceLocation;
        Minecraft minecraft = Minecraft.getInstance();
        returnButton = new TextButton(0, 0, buttonWidth, buttonHeight, Component.translatable("screen.depression.mental_trait.return"),
                (button -> minecraft.setScreen(screen)), TextButton.getDefaultNarration());
        confirmButton = new TextButton(0, 0, buttonWidth, buttonHeight, Component.translatable("screen.depression.mental_trait.confirm"),
                (button -> {
                    MentalTraitPacket.sendToServer(id);
                    ClientMentalStatus.isMentalTraitSelected = true;
                    minecraft.setScreen(null);
                }), TextButton.getDefaultNarration());
        String string = Component.translatable("depression.mental_trait." + id + ".desc").getString();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '\n') {
                drawString.add(builder.toString());
                builder = new StringBuilder();
            }
            else {
                builder.append(c);
            }
        }
        if (!builder.isEmpty()) {
            drawString.add(builder.toString());
        }
    }
    @Override
    protected void init() {
        addRenderableWidget(returnButton);
        addRenderableWidget(confirmButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderDirtBackground(guiGraphics);
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        int x = window.getGuiScaledWidth() / 2;
        int y = window.getGuiScaledHeight() / 2;
        returnButton.setX(x - screen.halfScreenHeight);
        returnButton.setY(y + halfFrameHeight + 2);
        confirmButton.setX(x + 4);
        confirmButton.setY(y + halfFrameHeight + 2);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.blit(FRAME, x - halfFrameWidth, y - halfFrameHeight, 1, 0, 0, frameWidth, frameHeight, frameWidth, frameHeight);
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        int top = y - halfFrameHeight - 4;
        guiGraphics.blit(MentalTraitButton.FRAME_LOCATION, x - screen.halfScreenHeight, top - 24,
                0, 0,0,24,24,24,24);
        guiGraphics.blit(resourceLocation, x - screen.halfScreenHeight + 4, top - 20,
                1, 0, 0, 16, 16, 16, 16);
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        guiGraphics.drawCenteredString(minecraft.font, Component.translatable("depression.mental_trait." + id), (x + 12), top - 16, 0xFFFFFF);
        for (int i = 0; i < drawString.size(); ++i) {
            guiGraphics.drawString(minecraft.font, drawString.get(i),
                    x - halfFrameWidth + 8 + 5, y - halfFrameHeight + 8 + 5 + (10*i), 0xFFFFFF);
        }
    }
}
