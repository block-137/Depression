package net.depression.fabric.world;

import com.google.common.collect.ImmutableSet;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.block.ModBlocks;
import net.depression.item.ModItems;
import net.depression.world.VillageAdditions;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;

public class FabricVillageAdditions {
    public static void register() {
        registerPoi(VillageAdditions.COMPUTER_POI_KEY, ModBlocks.COMPUTER);
        TradeOfferHelper.registerVillagerOffers(VillageAdditions.PSYCHOLOGIST.get(),1,
                factories-> {
                    factories.add((entity, randomSource) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD ,30),
                            new ItemStack(ModItems.MENTAL_HEALTH_SCALE.get(), 1),
                            64,5,0f));
                    factories.add((entity, randomSource) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD ,20),
                            new ItemStack(ModItems.MILD_DEPRESSION_TABLET.get(), 6),
                            10,5,0f));
                });
        TradeOfferHelper.registerVillagerOffers(VillageAdditions.PSYCHOLOGIST.get(),2,
                (factories) -> {
                    factories.add((entity, randomSource) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD, 30),
                            new ItemStack(ModItems.MODERATE_DEPRESSION_TABLET.get(), 6),
                            10, 20, 0f));
                });
        TradeOfferHelper.registerVillagerOffers(VillageAdditions.PSYCHOLOGIST.get(),3,
                (factories) -> {
                    factories.add((entity, randomSource) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD, 40),
                            new ItemStack(ModItems.MDD_CAPSULE.get(), 6),
                            10, 40, 0f));
                });
    }

    private static void registerPoi(ResourceKey<PoiType> key, RegistrySupplier<Block> block) {
        PointOfInterestHelper.register(key.location(), 1, 1, ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates()));
    }
}
