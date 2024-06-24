package net.depression.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.depression.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Depression.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> ITEMS_TAB = TABS.register("items_tab",
            () -> CreativeTabRegistry.create(Component.translatable("itemGroup." + Depression.MOD_ID + ".items_tab"),
                    () -> new ItemStack(ModItems.DIARY.get())));

    public static final RegistrySupplier<CreativeModeTab> BLOCKS_TAB = TABS.register("blocks_tab",
            () -> CreativeTabRegistry.create(Component.translatable("itemGroup." + Depression.MOD_ID + ".blocks_tab"),
                    () -> new ItemStack(ModBlocks.COMPUTER.get())));
    public static void register() {
        TABS.register();
    }
}
