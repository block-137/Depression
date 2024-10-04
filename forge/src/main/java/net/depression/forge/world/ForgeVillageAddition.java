package net.depression.forge.world;

import com.google.common.collect.ImmutableSet;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.depression.block.ModBlocks;
import net.depression.item.ModItems;
import net.depression.world.VillageAdditions;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Depression.MOD_ID)
public class ForgeVillageAddition {
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Depression.MOD_ID, Registry.POINT_OF_INTEREST_TYPE_REGISTRY);
    public static final RegistrySupplier<PoiType> COMPUTER_POI = POI_TYPES.register("computer_poi",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.COMPUTER.get().getStateDefinition().getPossibleStates()), 1, 1));
    @SubscribeEvent
    public static void registerVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillageAdditions.PSYCHOLOGIST.get()) {
            List<VillagerTrades.ItemListing> level1Trades = event.getTrades().get(1);
            level1Trades.add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD ,30),
                    new ItemStack(ModItems.MENTAL_HEALTH_SCALE.get(), 1),
                    64,5,0f));
            level1Trades.add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD ,20),
                    new ItemStack(ModItems.MILD_DEPRESSION_TABLET.get(), 6),
                    64,5,0f));
            List<VillagerTrades.ItemListing> level2Trades = event.getTrades().get(2);
            level2Trades.add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD ,30),
                    new ItemStack(ModItems.INSOMNIA_TABLET.get(), 6),
                    64,20,0f));
            level2Trades.add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD ,30),
                    new ItemStack(ModItems.MODERATE_DEPRESSION_TABLET.get(), 6),
                    64,20,0f));
            List<VillagerTrades.ItemListing> level3Trades = event.getTrades().get(3);
            level3Trades.add((entity, randomSource) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD ,40),
                    new ItemStack(ModItems.MDD_CAPSULE.get(), 6),
                    64,40,0f));
        }

    }
    public static void register() {
        POI_TYPES.register();
    }
}
