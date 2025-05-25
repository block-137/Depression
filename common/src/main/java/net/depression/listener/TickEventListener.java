package net.depression.listener;

import net.depression.rhythmcraft.PlayingChart;
import net.depression.server.Registry;
import net.depression.world.ParticleFormulaInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CommandBlock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class TickEventListener {
    public static void onServerTick(MinecraftServer server) {
        Registry.loadPendingPlayers();
    }
    public static void onServerLevelTick(ServerLevel serverLevel) {
        String id = serverLevel instanceof PlayingChart playingChart ? playingChart.player.getStringUUID() : serverLevel.dimensionTypeId().location().toString();
        LinkedList<ParticleFormulaInstance> list = Registry.particles.getOrDefault(id, new LinkedList<>());
        list.removeIf(instance -> !instance.tick());
    }
}
