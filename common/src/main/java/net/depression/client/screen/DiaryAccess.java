package net.depression.client.screen;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.world.item.ItemStack;

public class DiaryAccess extends BookViewScreen.WrittenBookAccess {

    public DiaryAccess(ItemStack itemStack) {
        super(itemStack);
    }
}
