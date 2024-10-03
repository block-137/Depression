package net.depression.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.depression.block.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final CreativeModeTab BLOCKS_TAB = CreativeTabRegistry.create(new ResourceLocation(Depression.MOD_ID, "blocks_tab"),
                () -> new ItemStack(ModBlocks.COMPUTER.get()));
    public static final CreativeModeTab ITEMS_TAB = CreativeTabRegistry.create(new ResourceLocation(Depression.MOD_ID, "items_tab"),
                    () -> new ItemStack(ModItems.DIARY.get()));

    public static void register() {
    }
}
