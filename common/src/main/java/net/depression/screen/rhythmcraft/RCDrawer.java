package net.depression.screen.rhythmcraft;

import net.depression.Depression;
import net.depression.rhythmcraft.Chart;
import net.depression.rhythmcraft.Song;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class RCDrawer {
    public static final ResourceLocation RC_FONT_SMALL = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/font_3_5.png");
    public static final ResourceLocation RANKINGS = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/rankings.png");
    public static final ResourceLocation RANKINGS_WITH_TEXT = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/rankings_with_text.png");
    private static final ResourceLocation SMALL_NUMBERS = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/small_numbers.png");
    private static final ResourceLocation REGULAR_NUMBERS = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/regular_numbers.png");
    private static final ResourceLocation SETTLEMENT_SMALL_NUMBERS = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement_small_numbers.png");
    private static final ResourceLocation SETTLEMENT_LARGE_NUMBERS = new ResourceLocation(Depression.MOD_ID, "textures/rc_screen/settlement_large_numbers.png");

    public static void drawStringSmall(GuiGraphics guiGraphics, String string, int x, int y, int k) {
        string = string.toUpperCase();
        for (int i = 0; i < string.length(); ++i) {
            int c = string.charAt(i) - 32;
            int textureY = c / 16;
            int textureX = c - textureY * 16;
            guiGraphics.blit(RC_FONT_SMALL, x + 4 * i, y, k, textureX * 5, textureY * 7, 5, 7, 80, 42);
        }
    }
    public static void drawStringRightAligned(GuiGraphics guiGraphics, String string, int x, int y, int k) {
        int stringWidth = 4 * string.length() + 1;
        for (int i = 0; i < string.length(); ++i) {
            int c = string.charAt(i) - 32;
            int textureY = c / 16;
            int textureX = c - textureY * 16;
            guiGraphics.blit(RC_FONT_SMALL, x - stringWidth + i * 4, y, k, textureX * 5, textureY * 7, 5, 7, 80, 42);
        }
    }
    public static void drawSettlementSmallNumber(GuiGraphics guiGraphics, int x, int y, int k, int number, int color) {
        int n1 = number / 10;
        int n2 = number % 10;
        int numberWidth = 7;
        int numberHeight = 7;
        int textureWidth = 70;
        int textureHeight = 42;
        float colorOffset = color * 7;
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        guiGraphics.blit(SETTLEMENT_SMALL_NUMBERS, x, y, k, n1 * numberWidth, colorOffset, numberWidth, numberHeight, textureWidth, textureHeight); //绘制数字
        guiGraphics.blit(SETTLEMENT_SMALL_NUMBERS, x + numberWidth, y, k, n2 * numberWidth, colorOffset, numberWidth, numberHeight, textureWidth, textureHeight); //绘制十位
    }
    public static void drawSettlementLargeNumber(GuiGraphics guiGraphics, int x, int y, int k, int number, int color) {
        String string = String.valueOf(number);
        for (int i = 0; i < string.length(); ++i) {
            int digit = string.charAt(i) - '0';
            int textureY = color * 15;
            int textureX = digit * 12;
            guiGraphics.blit(SETTLEMENT_LARGE_NUMBERS, x + i * 12, y, k, textureX, textureY, 12, 15, 120, 90);
        }
    }
    public static void drawChartLevel(GuiGraphics guiGraphics, int x, int y, int k, Song song, int difficulty) {
        for (int i = difficulty; i > 0; i--) {
            Chart chart = song.charts.get(i);
            if (chart != null) {
                int level = chart.level;
                drawSmallNumber(guiGraphics, x, y, k, level, i);
                return;
            }
        }
        //如果这歌往下没谱面就不显示数字了
    }
    public static void drawSmallNumber(GuiGraphics guiGraphics, int x, int y, int k, int number, int color) {
        int n1 = number / 10;
        int n2 = number % 10;
        int numberWidth = 5;
        int numberHeight = 7;
        int textureWidth = 50;
        int textureHeight = 42;
        float colorOffset = color * 7;
        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        guiGraphics.blit(SMALL_NUMBERS, x, y, k, n1 * 5, colorOffset, numberWidth, numberHeight, textureWidth, textureHeight); //绘制数字
        guiGraphics.blit(SMALL_NUMBERS, x + 4, y, k, n2 * 5, colorOffset, numberWidth, numberHeight, textureWidth, textureHeight); //绘制十位
    }
    public static void drawRegularNumber(GuiGraphics guiGraphics, int x, int y, int k, int number, int digits, int color) {
        int numberWidth = 7;
        int numberHeight = 9;
        int textureWidth = 70;
        int textureHeight = 54;
        int deltaX = 6;
        float colorOffset = color * 9;

        int divisor = (int) Math.pow(10, digits - 1);
        if (divisor % 10 == 9) { //防止浮点数丢精度
            ++divisor;
        }

        //k:显示优先级; f,g: （图片中的）起始偏移量; l,m: 实际显示大小; n,o: 图片大小
        for (int i = 0; i < digits; i++) {
            int n = number / divisor;
            number %= divisor;
            divisor /= 10;
            guiGraphics.blit(REGULAR_NUMBERS, x + i * deltaX, y, k, n * 7, colorOffset, numberWidth, numberHeight, textureWidth, textureHeight); //绘制数字
        }
    }

}
