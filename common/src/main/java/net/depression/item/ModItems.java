package net.depression.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Depression.MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> DIARY = ITEMS.register("diary",
            () -> new DiaryItem(new Item.Properties().stacksTo(1)));

    public static void register() {
        ITEMS.register();
    }
}
