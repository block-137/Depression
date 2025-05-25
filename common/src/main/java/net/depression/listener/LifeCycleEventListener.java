package net.depression.listener;

import net.depression.Depression;
import net.depression.client.rhythmcraft.ClientPlayingChart;
import net.depression.item.MedicineItem;
import net.depression.mental.MentalStatus;
import net.depression.world.VillageAdditions;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;


public class LifeCycleEventListener {
    public static void onServerStart(MinecraftServer server) {
        net.depression.server.Registry.init();
        RegistryAccess registryAccess = server.registryAccess();
        Registry<Item> items = registryAccess.registryOrThrow(Registries.ITEM);
        for (Item item : items) {
            String id = item.arch$registryName().toString();
            try {
                if (MentalStatus.lootHealItem.containsKey(id)) {
                    continue;
                }
                if (item instanceof TieredItem tieredItem) {
                    Tier tier = tieredItem.getTier();
                    double value = 0.1 * Math.pow(tier.getLevel(), 3);
                    MentalStatus.lootHealItem.put(id, value);
                    for (ItemStack material : tier.getRepairIngredient().getItems()) {
                        String materialId = material.getItem().arch$registryName().toString();
                        if (!MentalStatus.lootHealItem.containsKey(materialId)) {
                            MentalStatus.lootHealItem.put(materialId, value);
                        }
                    }
                }
                if (item instanceof ArmorItem armorItem) {
                    double value = 0.4 * armorItem.getDefense() + 0.2 * armorItem.getToughness() + 0.1 * armorItem.getMaterial().getKnockbackResistance();
                    MentalStatus.lootHealItem.put(id, value);
                }
                if (item instanceof ProjectileWeaponItem projectileWeaponItem) {
                    double damage = projectileWeaponItem.getMaxDamage();
                    if (damage > 5) {
                        double value = 0.2 * (damage - 5);
                        MentalStatus.lootHealItem.put(id, value);
                    }
                }
            }
            catch (Exception e) {
                Depression.LOGGER.error("Failed to load item " + id);
                e.printStackTrace();
            }
        }
        VillageAdditions.init(registryAccess);
    }
}
