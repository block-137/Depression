package net.depression.world;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.depression.Depression;
import net.depression.block.ModBlocks;
import net.depression.mixin.StructureTemplatePoolAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.ArrayList;
import java.util.List;

public class VillageAdditions {
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Depression.MOD_ID, Registries.VILLAGER_PROFESSION);
    public static final ResourceKey<PoiType> COMPUTER_POI_KEY = createKey("computer_poi");
    public static final RegistrySupplier<VillagerProfession> PSYCHOLOGIST = VILLAGER_PROFESSIONS.register("psychologist",
            () -> new VillagerProfession("psychologist", x -> x.value().is(ModBlocks.COMPUTER.get().defaultBlockState()),
                    x -> x.value().is(ModBlocks.COMPUTER.get().defaultBlockState()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER));

    public static void init(RegistryAccess registryAccess) {
        Registry<StructureTemplatePool> registry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        for (int i = 0; i < 8; ++i) { //加8次提高刷新率；这里for循环必须写在这里才会生效，不能写在addClinic方法里，我也不知道为什么
            addClinic(registry, "plains", 16);
            addClinic(registry, "desert", 16);
            addClinic(registry, "savanna", 16);
            addClinic(registry, "taiga", 16);
            addClinic(registry, "snowy", 16);
        }
    }
    public static void addClinic(Registry<StructureTemplatePool> registry, String biome, int weight) {
        StructureTemplatePool structureTemplatePool = registry.get(new ResourceLocation("village/" + biome + "/houses"));
        if (structureTemplatePool == null) {
            throw new IllegalStateException("Failed to find village pool " + biome + "/houses");
        }
        StructureTemplatePoolAccess poolAccess = (StructureTemplatePoolAccess) structureTemplatePool;
        List<Pair<StructurePoolElement, Integer>> rawTemplates = poolAccess.getRawTemplates() instanceof ArrayList ?
                poolAccess.getRawTemplates() : new ArrayList<>(poolAccess.getRawTemplates());

        SinglePoolElement addedElement = SinglePoolElement.single(new ResourceLocation(Depression.MOD_ID, "village/" + biome + "/houses/" + biome + "_psychological_clinic").toString()).apply(StructureTemplatePool.Projection.RIGID);
        rawTemplates.add(Pair.of(addedElement, weight));
        poolAccess.getTemplates().add(addedElement);
        poolAccess.setRawTemplates(rawTemplates);
    }

    private static ResourceKey<PoiType> createKey(String path) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(Depression.MOD_ID, path));
    }

    public static void register() {
        VILLAGER_PROFESSIONS.register();
    }
}
