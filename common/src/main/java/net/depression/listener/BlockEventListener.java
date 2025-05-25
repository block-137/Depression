package net.depression.listener;

import dev.architectury.event.EventResult;
import dev.architectury.utils.value.IntValue;
import net.depression.Depression;
import net.depression.mental.MentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.network.ActionbarHintPacket;
import net.depression.network.CloseEyePacket;
import net.depression.network.MentalStatusPacket;
import net.depression.rhythmcraft.PlayingChart;
import net.depression.server.Registry;
import net.depression.world.dimension.ModDimensions;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.ConfirmExperimentalFeaturesScreen;
import net.minecraft.client.gui.screens.worldselection.ExperimentsScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.WorldData;

import java.util.UUID;


public class BlockEventListener {
    public static EventResult onBlockBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player, IntValue intValue) {
        UUID playerUUID = player.getUUID();
        if (PlayingChart.playingCharts.containsKey(playerUUID)) {
            PlayingChart playingChart = PlayingChart.playingCharts.get(playerUUID);
            return playingChart.onBlockBreak(pos);
        }
        if (player.isCreative() || !player.hasCorrectToolForDrops(state)) { //如果是创造模式或者没有用合适的工具挖，就不计入
            return EventResult.pass();
        }

        Block block = state.getBlock();
        String blockID = block.arch$registryName().toString();
        MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
        mentalStatus.mentalIllness.trigMentalFatigue();
        if (MentalStatus.breakHealBlock.containsKey(blockID)) {
            if (block instanceof CropBlock) {
                CropBlock cropBlock = (CropBlock) block;
                if (!cropBlock.isMaxAge(state)) {
                    return EventResult.pass();
                }
            }
            else {
                ItemStack item = player.getMainHandItem();
                if (item.isEnchanted()) {
                    for (Tag tag : item.getEnchantmentTags()) {
                        if (tag.getAsString().contains("minecraft:silk_touch")) {
                            return EventResult.pass();
                        }
                    }
                }
            }
            MentalTrait mentalTrait = mentalStatus.mentalTrait;
            double healValue = mentalStatus.mentalHeal(blockID, MentalStatus.breakHealBlock.get(blockID)
                    * (state.is(BlockTags.MINEABLE_WITH_PICKAXE) ? mentalTrait.miningMultiplier : mentalTrait.farmingMultiplier));
            if (healValue > 0.25) {
                ActionbarHintPacket.sendBreakBlockHealPacket(player, block.getName());
            }
            MentalStatusPacket.sendToPlayer(player, mentalStatus);
        }
        return EventResult.pass();
    }

    public static EventResult onBlockPlace(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            if (player.isCreative()) {
                return EventResult.pass();
            }
            MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
            mentalStatus.mentalIllness.trigMentalFatigue();
        }
        return EventResult.pass();
    }
}
