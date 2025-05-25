package net.depression.listener;

import dev.architectury.event.EventResult;
import net.depression.mental.MentalStatus;
import net.depression.mental.PTSDManager;
import net.depression.network.ActionbarHintPacket;
import net.depression.network.MentalStatusPacket;
import net.depression.rhythmcraft.PlayingChart;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;

import java.util.HashSet;
import java.util.List;

public class EntityEventListener {
    public static HashSet<String> petIds = new HashSet<>();
    public static EventResult onEntityDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity.level().isClientSide()) {
            return EventResult.pass();
        }
        Entity entity = damageSource.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) livingEntity;
            if (player.isCreative()) {
                return EventResult.pass();
            }
            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
            mentalStatus.ptsdManager.clear(player);
            Entity directEntity = damageSource.getDirectEntity();
            double multiplier = Registry.playerEventMap.containsKey(player.getUUID()) ? 2d : 1d;
            if (entity != null) {
                String encodeId = entity.getEncodeId();
                String directEncodeId = directEntity.getEncodeId();
                if (encodeId == null) {
                    return EventResult.pass();
                }
                if (!encodeId.equals(directEncodeId)) { //如果直接造成伤害的实体与间接造成伤害的实体不是同一个实体的话，就分开造成心理伤害
                    mentalStatus.mentalHurt(encodeId, 10d * multiplier);
                    mentalStatus.mentalHurt(directEncodeId, 2.5d * multiplier);
                }
                else {
                    mentalStatus.mentalHurt(encodeId, 10d * multiplier);
                }
            }
            else {
                mentalStatus.mentalHurt(damageSource.getMsgId(), 10d * multiplier);
            }
        }
        else {
            if (livingEntity instanceof TamableAnimal tamableAnimal) {
                LivingEntity owner = tamableAnimal.getOwner();
                if (owner instanceof ServerPlayer player && petIds.contains(livingEntity.getEncodeId())) {
                    MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
                    mentalStatus.mentalHurt(5d);
                }
            }
            String encodeId = livingEntity.getEncodeId();
            if (entity != null && entity instanceof Player) {
                ServerPlayer player = (ServerPlayer) entity;
                MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                double healValue = 0d;
                if (MentalStatus.killHealEntity.containsKey(encodeId)) {
                    healValue = mentalStatus.mentalHeal(encodeId, MentalStatus.killHealEntity.get(encodeId) * mentalStatus.mentalTrait.killMobMultiplier);
                }
                if (encodeId != null && mentalStatus.PTSD.containsKey(encodeId)) {
                    mentalStatus.PTSD.put(encodeId, mentalStatus.PTSD.get(encodeId) - PTSDManager.KILL_PTSD_DECREASE);
                }
                else if (livingEntity instanceof Animal) {
                    healValue = mentalStatus.mentalHeal(encodeId, mentalStatus.mentalTrait.killAnimalHealValue);
                }
                if (healValue > 0.25) {
                    ActionbarHintPacket.sendKillEntityHealPacket(player, livingEntity.getName());
                }
                if (healValue != 0) {
                    MentalStatusPacket.sendToPlayer(player, mentalStatus);
                }
            }
        }
        return EventResult.pass();
    }

    public static EventResult onEntityCheckSpawn(LivingEntity livingEntity, LevelAccessor levelAccessor, double v, double v1, double v2, MobSpawnType mobSpawnType, BaseSpawner baseSpawner) {
        if (livingEntity.level() instanceof PlayingChart && mobSpawnType == MobSpawnType.NATURAL) {
            return EventResult.interruptFalse();
        }
        return EventResult.pass();
    }
}
