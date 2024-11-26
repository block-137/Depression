package net.depression.listener;

import dev.architectury.event.EventResult;
import net.depression.Depression;
import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class PlayerEventListener {
    public static void onPlayerQuit(ServerPlayer player) { //如果玩家退出 那么将玩家加入退出玩家列表
        Registry.quitPlayers.add(player.getUUID());
    }
    public static void onSmeltItem(Player player, ItemStack item) {
        if (player.level().isClientSide() || player.isCreative()) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        String id = item.getItem().arch$registryName().toString();

        if (MentalStatus.smeltHealItem.containsKey(id)) {
            MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(serverPlayer);
            mentalStatus.mentalHeal(id, MentalStatus.smeltHealItem.get(id), item.getCount());
        }
    }
    public static EventResult onAttackEntity(Player player, Level level, Entity entity, InteractionHand hand, EntityHitResult entityHitResult) {
        if (level.isClientSide() || player.isCreative() || !(entity instanceof LivingEntity)) {
            return EventResult.pass();
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(serverPlayer);
        if (mentalStatus.emotionValue >= -2) {
            mentalStatus.combatCountdown = 10;
        }
        mentalStatus.mentalIllness.trigMentalFatigue();
        return EventResult.pass();
    }

    public static void onPlayerAdvancement(ServerPlayer player, AdvancementHolder advancement) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        String id = advancement.id().toString();
        if (MentalStatus.healAdvancement.containsKey(id)) {
            MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
            mentalStatus.mentalHeal(MentalStatus.healAdvancement.get(id));
        }
    }
}
