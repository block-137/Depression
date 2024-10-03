package net.depression.mixin.client;

import net.depression.Depression;
import net.depression.client.screen.DiaryAccess;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(BookViewScreen.class)
public abstract class BookViewScreenMixin extends Screen {
    @Unique private static final ResourceLocation DIARY_LOCATION = new ResourceLocation(Depression.MOD_ID, "textures/gui/diary.png");
    @Shadow private BookViewScreen.BookAccess bookAccess;

    @Shadow private int cachedPage;

    @Shadow private int currentPage;

    @Shadow private List<FormattedCharSequence> cachedPageComponents;

    @Shadow private Component pageMsg;

    @Shadow protected abstract int getNumPages();

    @Shadow @Nullable
    public abstract Style getClickedComponentStyleAt(double d, double e);

    protected BookViewScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void renderBackground(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (!(bookAccess instanceof DiaryAccess)) {
            return;
        }
        super.renderBackground(guiGraphics, i, j, f);
        guiGraphics.blit(DIARY_LOCATION, (this.width - 192) / 2, 2, 0, 0, 192, 192);
        ci.cancel();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (!(bookAccess instanceof DiaryAccess)) {
            return;
        }
        super.render(guiGraphics, i, j, f);
        int k = (this.width - 192) / 2;
        if (this.cachedPage != this.currentPage) {
            String text = this.bookAccess.getPage(this.currentPage).getString();

            //读取空格数量，判断是否为拉丁语系
            int spaceCount = 0;
            for (char c : text.toCharArray()) {
                if (c == ' ') {
                    ++spaceCount;
                }
            }
            boolean isLatin = spaceCount > 24;

            StringSplitter splitter = font.getSplitter();
            List<FormattedCharSequence> list = new ArrayList<>();
            int index;
            int displayIndex;
            while (!text.isEmpty()) {
                if (isLatin) {
                    index = splitter.findLineBreak(text, 114, Style.EMPTY);
                    displayIndex = index;
                }
                else {
                    index = splitter.formattedIndexByWidth(text, 114, Style.EMPTY);
                    displayIndex = index;
                    for (int l = 0; l < index; ++l) { //寻找是否有换行符
                        if (text.charAt(l) == '\n') {
                            index = l + 1;
                            displayIndex = l;
                            break;
                        }
                    }
                }
                if (index < text.length() && text.charAt(index) == '\n') { //如果断的位置正好是换行符
                    ++index;
                }
                list.add(Language.getInstance().getVisualOrder(FormattedText.of(text.substring(0, displayIndex))));
                text = text.substring(index);
            }
            this.cachedPageComponents = list;


            this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
        }

        this.cachedPage = this.currentPage;
        int m = this.font.width(this.pageMsg);
        guiGraphics.drawString(this.font, this.pageMsg, k - m + 192 - 44, 18, 0, false);
        Objects.requireNonNull(this.font);
        int n = Math.min(128 / 9, this.cachedPageComponents.size());

        for(int o = 0; o < n; ++o) {
            FormattedCharSequence formattedCharSequence = this.cachedPageComponents.get(o);
            Font var10001 = this.font;
            int var10003 = k + 36;
            Objects.requireNonNull(this.font);
            guiGraphics.drawString(var10001, formattedCharSequence, var10003, 32 + o * 9, 0, false);
        }

        Style style = this.getClickedComponentStyleAt(i, j);
        if (style != null) {
            guiGraphics.renderComponentHoverEffect(this.font, style, i, j);
        }
        ci.cancel();
    }
}
