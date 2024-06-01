package net.depression.forge;

import dev.architectury.platform.forge.EventBuses;
import net.depression.Depression;
import net.depression.forge.config.ServerConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Depression.MOD_ID)
public final class DepressionForge {
    public DepressionForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(Depression.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        // Run our common setup.
        Depression.init();
        ServerConfig.load();
    }
}
