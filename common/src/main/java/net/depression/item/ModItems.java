package net.depression.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.depression.effect.ModEffects;
import net.depression.tag.ModBannerPatternTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Depression.MOD_ID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Item> DIARY = ITEMS.register("diary",
            () -> new DiaryItem(new Item.Properties().stacksTo(1).tab(ModCreativeTabs.ITEMS_TAB)));
    public static final RegistrySupplier<Item> MENTAL_HEALTH_SCALE = ITEMS.register("mental_health_scale",
            () -> new MentalHealthScaleItem(new Item.Properties().tab(ModCreativeTabs.ITEMS_TAB)));
    public static final RegistrySupplier<Item> MILD_DEPRESSION_TABLET = ITEMS.register("mild_depression_tablet",
            () -> new AntiDepressantItem(0));

    public static final RegistrySupplier<Item> MODERATE_DEPRESSION_TABLET = ITEMS.register("moderate_depression_tablet",
            () -> new AntiDepressantItem(1));

    public static final RegistrySupplier<Item> MDD_CAPSULE = ITEMS.register("mdd_capsule",
            () -> new AntiDepressantItem(2));

    public static final RegistrySupplier<Item> INSOMNIA_TABLET = ITEMS.register("insomnia_tablet",
            () -> new MedicineItem("depression:insomnia_tablet", ModEffects.SLEEPINESS.get(), 6000, 0, 300, 600, "item.depression.insomnia_tablet.desc"));

    public static final RegistrySupplier<Item> RIBBON_BANNER_PATTERN = ITEMS.register("ribbon_banner_pattern",
            () -> new BannerPatternItem(ModBannerPatternTags.PATTERN_RIBBON, new Item.Properties().stacksTo(1).tab(ModCreativeTabs.ITEMS_TAB)));

    public static void register() {
        ITEMS.register();
    }
}
