package net.depression.listener;

import dev.architectury.event.EventResult;
import net.depression.mental.MentalStatus;
import net.depression.network.MentalStatusPacket;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EntityEventListener {
    public static EventResult onEntityDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity.level().isClientSide()) {
            return EventResult.pass();
        }
        Entity entity = damageSource.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player) livingEntity;
            if (player.isCreative()) {
                return EventResult.pass();
            }
            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
            Entity directEntity = damageSource.getDirectEntity();
            if (entity != null) {
                String encodeId = entity.getEncodeId();
                String directEncodeId = directEntity.getEncodeId();
                if (!encodeId.equals(directEncodeId)) { //如果直接造成伤害的实体与间接造成伤害的实体不是同一个实体的话，就分开造成心理伤害
                    mentalStatus.mentalHurt(encodeId, 10d);
                    mentalStatus.mentalHurt(directEncodeId, 2.5d);
                }
                else {
                    mentalStatus.mentalHurt(encodeId, 10d);
                }
            }
            else {
                mentalStatus.mentalHurt(damageSource.getMsgId(), 10d);
            }
        }
        else {
            String encodeId = livingEntity.getEncodeId();
            if (entity != null && entity instanceof Player && MentalStatus.killHealEntity.containsKey(encodeId)) {
                ServerPlayer player = (ServerPlayer) entity;
                MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                mentalStatus.mentalHeal(encodeId, MentalStatus.killHealEntity.get(encodeId));
                MentalStatusPacket.sendToPlayer(player, mentalStatus);
            }
        }
        return EventResult.pass();
    }
}
