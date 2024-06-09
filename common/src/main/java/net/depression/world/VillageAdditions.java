package net.depression.world;

import net.minecraft.core.RegistryAccess;

public class VillageAdditions {
    public static void init(RegistryAccess registryAccess) {
        /*
        Registry<StructureTemplatePool> registry =  registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
        StructureTemplatePool structureTemplatePool = registry.get(new ResourceLocation("village/plains/houses"));
        if (structureTemplatePool == null) {
             throw new IllegalStateException("Failed to find village pool plains/houses");
        }
        StructureTemplatePoolAccess poolAccess = (StructureTemplatePoolAccess) structureTemplatePool;
        List<Pair<StructurePoolElement, Integer>> rawTemplates = poolAccess.getRawTemplates() instanceof ArrayList ?
                poolAccess.getRawTemplates() : new ArrayList<>(poolAccess.getRawTemplates());

        SinglePoolElement addedElement = SinglePoolElement.single(new ResourceLocation(Depression.MOD_ID, "village/plains/houses/test_house").toString()).apply(StructureTemplatePool.Projection.RIGID);
        rawTemplates.add(Pair.of(addedElement, 4)); //4是权重
        poolAccess.getTemplates().add(addedElement);

        poolAccess.setRawTemplates(rawTemplates);
         */
    }
}
