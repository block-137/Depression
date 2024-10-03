package net.depression.fabric;

import net.depression.Depression;
import net.depression.fabric.config.ServerConfig;
import net.depression.fabric.world.FabricVillageAdditions;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.npc.VillagerTrades;

public final class DepressionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Depression.init();
        FabricVillageAdditions.register();
        ServerConfig.load();
    }
}
