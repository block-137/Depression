package net.depression.listener;

import dev.architectury.event.EventResult;
import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
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
            MentalStatus mentalStatus = Registry.mentalStatus.get(serverPlayer.getUUID());
            if (mentalStatus == null) {
                mentalStatus = new MentalStatus(serverPlayer);
                Registry.mentalStatus.put(serverPlayer.getUUID(), mentalStatus);
            }
            for (int i = 0; i < item.getCount(); i++) {
                mentalStatus.mentalHeal(id, MentalStatus.smeltHealItem.get(id)); //TODO:应当直接计算收敛值，但是我懒，反正上限也就64，以后再优化
            }
        }
    }
    public static EventResult onAttackEntity(Player player, Level level, Entity entity, InteractionHand hand, EntityHitResult entityHitResult) {
        if (level.isClientSide() || player.isCreative()) {
            return EventResult.pass();
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        MentalStatus mentalStatus = Registry.mentalStatus.get(serverPlayer.getUUID());
        if (mentalStatus == null) {
            mentalStatus = new MentalStatus(serverPlayer);
            Registry.mentalStatus.put(serverPlayer.getUUID(), mentalStatus);
        }
        mentalStatus.mentalIllness.trigMentalFatigue();
        return EventResult.pass();
    }
}
