package net.depression.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Depression.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> DEPRESSION_TAB = TABS.register("depression_tab",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                    .icon(() -> new ItemStack(ModItems.DIARY.get()))
                    .title(Component.translatable("itemGroup." + Depression.MOD_ID + ".items_tab"))
                    .displayItems(((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.DIARY.get());
                    })).build());

    public static void register() {
        TABS.register();
    }
}
