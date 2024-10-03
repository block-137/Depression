package net.depression.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.depression.Depression;
import net.depression.block.ModBlocks;
import net.depression.mixin.SingleJigsawAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class VillageAdditions {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Depression.MOD_ID, Registry.VILLAGER_PROFESSION_REGISTRY);
    public static final ResourceKey<PoiType> COMPUTER_POI_KEY = createKey("computer_poi");
    public static final RegistrySupplier<VillagerProfession> PSYCHOLOGIST = VILLAGER_PROFESSIONS.register("psychologist",
            () -> new VillagerProfession("psychologist", x -> x.value().is(ModBlocks.COMPUTER.get().defaultBlockState()),
                    x -> x.value().is(ModBlocks.COMPUTER.get().defaultBlockState()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER));
    public static void addClinic(String biome, int weight) {
        ResourceLocation pool = new ResourceLocation("village/" + biome + "/houses");
        StructureTemplatePool old = BuiltinRegistries.TEMPLATE_POOL.get(pool);

        int id = BuiltinRegistries.TEMPLATE_POOL.getId(old);

        List<StructurePoolElement> shuffled;
        if (old != null) {
            shuffled = old.getShuffledTemplates(RandomSource.create(0));
        } else {
            shuffled = ImmutableList.of();
        }
        Object2IntMap<StructurePoolElement> newPieces = new Object2IntLinkedOpenHashMap<>();
        for(StructurePoolElement p : shuffled)
            newPieces.computeInt(p, (StructurePoolElement pTemp, Integer i) -> (i==null ? 0 : i) + 1);
        newPieces.put(SingleJigsawAccess.construct(
                Either.left(new ResourceLocation(Depression.MOD_ID, "village/" + biome + "/houses/" + biome + "_psychological_clinic")), ProcessorLists.EMPTY, StructureTemplatePool.Projection.RIGID
        ), weight);
        List<Pair<StructurePoolElement, Integer>> newPieceList = newPieces.object2IntEntrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getIntValue()))
                .collect(Collectors.toList());

        ResourceLocation name = old.getName();
        ((WritableRegistry<StructureTemplatePool>)BuiltinRegistries.TEMPLATE_POOL).registerOrOverride(
                OptionalInt.of(id),
                ResourceKey.create(BuiltinRegistries.TEMPLATE_POOL.key(), name),
                new StructureTemplatePool(pool, name, newPieceList),
                Lifecycle.stable()
        );
    }

    private static ResourceKey<PoiType> createKey(String path) {
        return ResourceKey.create(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, new ResourceLocation(Depression.MOD_ID, path));
    }

    public static void register() {
        VILLAGER_PROFESSIONS.register();
        addClinic("plains", 8);
        addClinic("desert", 8);
        addClinic("savanna", 8);
        addClinic("taiga", 8);
        addClinic("snowy", 8);
    }
}
