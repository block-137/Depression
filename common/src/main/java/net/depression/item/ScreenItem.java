package net.depression.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class ScreenItem extends Item {
    String lore;
    Supplier<Screen> screenSupplier;

    public ScreenItem(String lore, Supplier<Screen> screenSupplier) {
        super(new Properties().tab(ModCreativeTabs.ITEMS_TAB));
        this.lore = lore;
        this.screenSupplier = screenSupplier;
    }
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable(lore).withStyle(ChatFormatting.GRAY));
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(screenSupplier.get());
        }
        itemStack.shrink(1);
        return super.use(level, player, hand);
    }
}
