package net.depression.mixin;

import net.depression.mental.MentalStatus;
import net.depression.network.MentalStatusPacket;
import net.depression.server.Registry;
import net.depression.server.StatManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide())
            return;
        StatManager stat = Registry.statManager.get(player.getUUID());
        if (stat == null) {
            stat = new StatManager();
            Registry.statManager.put(player.getUUID(), stat);
        }
        if (player.level().getDayTime() % 24000 == 0) {  //每天0时更新一次旧统计数据
            stat.updateStat((ServerPlayer) player);
        }

        if (player.isCreative() || player.isSpectator() || player.isDeadOrDying())
            return;
        MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
        if (mentalStatus == null) {
            mentalStatus = new MentalStatus((ServerPlayer) player);
            Registry.mentalStatus.put(player.getUUID(), mentalStatus);
        }
        mentalStatus.tick((ServerPlayer) player);
    }

    @Inject(method = "stopSleepInBed", at = @At("TAIL"))
    private void stopSleepInBed(boolean bl, boolean bl2, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide() || bl || bl2 || player.isCreative()) {
            return;
        }
        MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
        if (mentalStatus == null) {
            mentalStatus = new MentalStatus((ServerPlayer) player);
            Registry.mentalStatus.put(player.getUUID(), mentalStatus);
        }
        Boolean isInsomnia = mentalStatus.mentalIllness.isInsomnia;
        if (isInsomnia != null && isInsomnia) { //如果失眠，则直接返回，不进行睡眠治疗
            return;
        }
        if (mentalStatus.emotionValue < -5 * Math.max(mentalStatus.mentalHealthValue, 10d) / 100d) {
            mentalStatus.emotionValue = 0;
        }
        else {
            mentalStatus.mentalHeal(5);
        }
    }

    @Inject(method = "eat", at = @At("HEAD"))
    private void eat(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        if (level.isClientSide()) {
            return;
        }
        ServerPlayer player = (ServerPlayer) (Object) this;
        StatManager stat = Registry.statManager.get(player.getUUID());
        stat.hasAte = true;
        MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
        if (mentalStatus == null) {
            mentalStatus = new MentalStatus(player);
            Registry.mentalStatus.put(player.getUUID(), mentalStatus);
        }
        if (mentalStatus.mentalIllness.mentalHealthLevel == 3) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        }
        FoodProperties foodProperties = itemStack.getItem().getFoodProperties();
        mentalStatus.mentalHeal(foodProperties.getNutrition() / 2f
                * (1+foodProperties.getSaturationModifier()*2f) //获得饱食度+饱和度
                * foodProperties.getSaturationModifier() //乘以营养等级
                * MentalStatus.FOOD_HEAL_RATE); //乘以食物治疗倍率
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void actuallyHurt(DamageSource damageSource, float f, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        //PTSD的范围是0-10
        f = Math.min(f, player.getHealth());
        double damageRate = f / player.getMaxHealth() * 20;
        double healthRate = player.getHealth() / player.getMaxHealth();
        double minValue = healthRate * 3;
        damageRate -= minValue;
        if (damageRate <= 0) {
            return;
        }
        damageRate *= Math.sqrt(damageRate);
        Entity entity = damageSource.getEntity();
        Entity directEntity = damageSource.getDirectEntity();
        MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
        if (mentalStatus == null) {
            mentalStatus = new MentalStatus((ServerPlayer) player);
            Registry.mentalStatus.put(player.getUUID(), mentalStatus);
        }
        if (entity != null) {
            String encodeId = entity.getEncodeId();
            String directEncodeId = directEntity.getEncodeId();
            if (!encodeId.equals(directEncodeId)) { //如果直接造成伤害的实体与间接造成伤害的实体不是同一个实体的话，就分开造成心理伤害
                mentalStatus.mentalHurt(encodeId, damageRate*0.8d);
                mentalStatus.mentalHurt(directEncodeId, damageRate*0.2d);
            }
            else {
                mentalStatus.mentalHurt(encodeId, damageRate);
            }
        }
        else {
            mentalStatus.mentalHurt(damageSource.getMsgId(), damageRate);
        }
        MentalStatusPacket.sendToPlayer((ServerPlayer) player, mentalStatus);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide())
            return;
        MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
        if (mentalStatus == null) {
            mentalStatus = new MentalStatus((ServerPlayer) player);
            Registry.mentalStatus.put(player.getUUID(), mentalStatus);
        }
        mentalStatus.readNbt(tag);

        StatManager statManager = Registry.statManager.get(player.getUUID()); //读取玩家的旧统计数据
        if (statManager == null) {
            statManager = new StatManager();
            Registry.statManager.put(player.getUUID(), statManager);
        }
        if (tag.contains("old_stats")) {
            CompoundTag oldStats = tag.getCompound("old_stats");
            statManager.readNbt(oldStats);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide())
            return;
        UUID uuid = player.getUUID();
        MentalStatus mentalStatus = Registry.mentalStatus.get(uuid);
        if (mentalStatus == null) {
            return;
        }

        mentalStatus.writeNbt(tag);

        StatManager stat = Registry.statManager.get(uuid);
        if (stat == null) {
            return;
        }
        CompoundTag oldStats; //保存玩家的旧统计数据
        if (tag.contains("old_stats")) {
            oldStats = tag.getCompound("old_stats");
        }
        else {
            oldStats = new CompoundTag();
        }
        stat.writeNbt(oldStats);
        tag.put("old_stats", oldStats);

        if (Registry.quitPlayers.contains(uuid)) { //若玩家已经退出游戏，删除玩家的心理状态
            Registry.quitPlayers.remove(uuid);
            Registry.mentalStatus.remove(uuid);
            Registry.statManager.remove(uuid); //也删除玩家的旧统计数据
        }
    }
}
