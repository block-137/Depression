package net.depression.fabric;

import net.depression.Depression;
import net.depression.fabric.world.FabricVillageAdditions;
import net.fabricmc.api.ModInitializer;
<<<<<<< HEAD
import net.minecraft.world.entity.npc.VillagerTrades;
=======
import net.minecraft.core.Registry;
>>>>>>> 79040de (0.1.4 Update)

public final class DepressionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        // Run our common setup.
        Depression.init();
        FabricVillageAdditions.register();

    }
}
