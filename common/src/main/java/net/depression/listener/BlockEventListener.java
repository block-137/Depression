package net.depression.listener;

import dev.architectury.event.EventResult;
import dev.architectury.utils.value.IntValue;
import net.depression.mental.MentalStatus;
import net.depression.network.CloseEyePacket;
import net.depression.network.MentalStatusPacket;
import net.depression.server.Registry;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;


public class BlockEventListener {
    public static EventResult onBlockBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player, IntValue intValue) {
        if (player.isCreative()) {
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
            mentalStatus.mentalHeal(blockID, MentalStatus.breakHealBlock.get(blockID));
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
            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
            if (mentalStatus == null) {
                mentalStatus = new MentalStatus(player);
                Registry.mentalStatus.put(player.getUUID(), mentalStatus);
            }
            mentalStatus.mentalIllness.trigMentalFatigue();
        }
        return EventResult.pass();
    }
}
