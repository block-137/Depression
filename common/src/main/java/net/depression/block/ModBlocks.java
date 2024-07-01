package net.depression.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.depression.item.ModCreativeTabs;
import net.depression.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Depression.MOD_ID, Registries.BLOCK);
    public static final RegistrySupplier<Block> COMPUTER = registerBlock("computer", ComputerBlock::new);

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().arch$tab(ModCreativeTabs.BLOCKS_TAB)));
        return toReturn;
    }

    public static void register() {
        BLOCKS.register();
    }
}
