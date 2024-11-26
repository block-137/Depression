package net.depression.mental;

import net.depression.effect.ModEffects;
import net.depression.network.ActionbarHintPacket;
import net.depression.network.MentalStatusPacket;
import net.depression.server.Registry;
import net.depression.util.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MentalStatus {
    public static boolean isItemScanned = false;
    public static double EMOTION_STABILIZE_RATE;
    public static double MENTAL_HEALTH_CHANGE_RATE;
    public static double PTSD_DAMAGE_RATE;
    public static double PTSD_DISPERSE_RATE;
    public static double FOOD_HEAL_RATE;
    public static int BOREDOM_DECREASE_TICK;
    public static boolean IS_RANDOM_CHOOSE_TRAIT;
    public static String DEFAULT_MENTAL_TRAIT;
    public static HashMap<String, String> nearbyHealBlockType = new HashMap<>(); //精神治疗光环方块-类型
    public static HashMap<String, Double> nearbyHealBlockValue = new HashMap<>(); //精神治疗光环方块-治疗值
    public static HashMap<String, Integer> nearbyHealBlockRadius = new HashMap<>(); //精神治疗光环方块-作用半径
    public static HashMap<String, Double> lootHealItem = new HashMap<>(); //loot了会开心的物品
    public static HashMap<String, Double> foodHealValue = new HashMap<>(); //食用食物治疗值
    public static int radiusMaxValue; //精神治疗光环方块-最大的半径
    public static HashMap<String, Double> breakHealBlock = new HashMap<>(); //挖了会开心的方块
    public static HashMap<String, Double> killHealEntity = new HashMap<>(); //杀了会开心的实体
    public static HashMap<String, Double> smeltHealItem = new HashMap<>(); //熔炼了会开心的物品
    public static HashMap<String, Double> healAdvancement = new HashMap<>(); //获得成就会开心
    private final ConcurrentHashMap<String, Integer> boredom = new ConcurrentHashMap<>(); //无聊值
    public final ConcurrentHashMap<String, Double> PTSD = new ConcurrentHashMap<>(); //PTSD值
    public final PTSDManager ptsdManager = new PTSDManager(this, PTSD);
    private final ConcurrentHashMap<String, Long> PTSDTimeBuffer = new ConcurrentHashMap<>(); //PTSD时刻缓冲区（存储造成PTSD的那一个tick）
    private final ConcurrentHashMap<String, Double> PTSDValueBuffer = new ConcurrentHashMap<>(); //PTSD值缓冲区（存储造成PTSD的值）
    public final HashSet<String> playerPTSDSet = new HashSet<>(); //玩家PTSD集合
    public double emotionValue; //情绪值（-20~20），实际上是精神健康值的导数。
    public double mentalHealthValue = 100; //精神健康值（0~100）
    public MentalTrait mentalTrait;
    public int combatCountdown = 0;

    public MentalIllness mentalIllness;
    private ServerPlayer player;
    public long tickCount = -1;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private AttributeModifier speedModifier;

    private AttributeModifier attributeModifier;
    
    public MentalStatus(ServerPlayer player) {
        this.player = player;
        this.mentalIllness = new MentalIllness(player, this);
    }

    public MentalStatus(ServerPlayer player, MentalTrait mentalTrait) {
        loadMentalTrait(mentalTrait);
        this.player = player;
        this.mentalIllness = new MentalIllness(player, this);
    }

    public void loadMentalTrait(MentalTrait mentalTrait) {
        this.mentalTrait = mentalTrait;
        if (mentalTrait.initialMentalHealthValue != null) {
            this.mentalHealthValue = mentalTrait.initialMentalHealthValue;
            this.mentalIllness.mentalHealthLevel = MentalIllness.getMentalHealthLevel(mentalHealthValue);
        }
        if (mentalTrait.initialMentalHealthId != null) {
            this.mentalIllness.mentalHealthId = mentalTrait.initialMentalHealthId;
        }
    }

    public synchronized void triggerHurt(String id) {
        ptsdManager.hurt(id);
    }

    public synchronized void tick(ServerPlayer player) {
        if (mentalTrait == null) {
            return;
        }
        this.player = player;
        ++tickCount;
        //处理无聊值
        if (tickCount % BOREDOM_DECREASE_TICK == 0) {
            for (Map.Entry<String, Integer> entry : boredom.entrySet()) {
                int value = entry.getValue();
                if (value % 4 == 0) {
                    entry.setValue(value - value / 4);
                }
                else {
                    entry.setValue(value - (value / 4 + 1)); //向上取整
                }
                if (entry.getValue() <= 1) { //若无聊值归零，则移除其无聊计数
                    boredom.remove(entry.getKey());
                }
            }
        }
        //处理PTSD缓冲区
        for (Map.Entry<String, Long> entry : PTSDTimeBuffer.entrySet()) {
            if (tickCount - entry.getValue() >= 200L) { //PTSD缓冲区持续200tick后，正式形成PTSD
                String string = entry.getKey();
                Double damage = PTSDValueBuffer.get(string);
                if (damage == null) {
                    PTSDTimeBuffer.remove(string);
                    continue;
                }
                Double originValue = PTSD.get(string);
                if (originValue == null) {
                    originValue = 0d;
                    EntityType.byString(string).ifPresentOrElse(
                            entityType -> ActionbarHintPacket.sendPTSDFormPacket(player, entityType.getDescription()),
                            () -> {
                                if (playerPTSDSet.contains(string)) {
                                    ActionbarHintPacket.sendPTSDFormPacket(player, Component.literal(string));
                                }
                                else {
                                    ActionbarHintPacket.sendPTSDFormPacket(player, Component.translatable("message.depression.damagesource." + string));
                                }
                            });
                }
                PTSD.put(string, Math.min(originValue + damage, PTSDManager.PTSD_MAX_VALUE)); //保证PTSD不超过上限（32）
                PTSDTimeBuffer.remove(string);
                PTSDValueBuffer.remove(string);
            }
        }
        if (tickCount % 20L == 0L) {
            //方块光环检测
            executor.submit(this::detectNearbyHealBlock);
            //睡眠检测
            ServerLevel level = player.serverLevel();
            if (mentalIllness.isInsomnia != null && level.getDayTime() % 24000 < 12010) {
                mentalIllness.isInsomnia = null;
            }
            if (player.isSleeping()) {
                if (mentalIllness.isInsomnia == null) {
                    mentalIllness.setIsInsomnia();
                }
                if (!mentalIllness.isInsomnia) {
                    mentalHeal("sleeping", 1d);
                }
            }
            //PTSD的自然消散
            for (Map.Entry<String, Double> entry : PTSD.entrySet()) {
                String key = entry.getKey();
                double value;
                if (entry.getValue() <= PTSDManager.PTSD_1_VALUE) {
                    value = entry.getValue();
                    entry.setValue(value - PTSD_DISPERSE_RATE * (11 - value / 2)); ////PTSD值 -= 自然消散速度 * (7 - PTSD值)
                }
                else { //PTSD > 12 判定玩家是否触发症状
                    value = entry.getValue() - PTSD_DISPERSE_RATE; //PTSD值 -= 自然消散速度
                    entry.setValue(value);
                    EntityType.byString(key).ifPresentOrElse(
                            entityType -> {
                                List<? extends Entity> list = level.getEntities(entityType, this::viewDetect);
                                for (Entity entity : list) {
                                    ptsdManager.trigger(entity);
                                }
                                if (value > PTSDManager.PTSD_4_VALUE) {
                                    ptsdManager.photismId.add(key);
                                }
                                else {
                                    ptsdManager.photismId.remove(key);
                                }
                            }, () -> {
                                for (ServerPlayer serverPlayer : level.players()) {
                                    String name = serverPlayer.getDisplayName().getString();
                                    if (name.equals(key)) {
                                        if (viewDetect(serverPlayer)) {
                                            ptsdManager.trigger(key);
                                        }
                                    }
                                }
                            }
                            );

                    if (value > PTSDManager.PTSD_3_VALUE) {
                        ptsdManager.phonismId.add(key);
                    }
                    else {
                        ptsdManager.phonismId.remove(key);
                    }
                }
                if (entry.getValue() <= 0) { //如果PTSD值归零，则移除PTSD
                    String id = entry.getKey();
                    removePTSD(id);
                }
            }
            //处理精神健康值
            if (!mentalIllness.isMania) { //躁狂期间精神健康值不随情绪改变
                if (emotionValue < 0) {
                    if (mentalIllness.mentalHealthId == 4) {
                        MobEffect antiDepression = ModEffects.ANTI_DEPRESSION.get();
                        MobEffect antiMania = ModEffects.ANTI_MANIA.get();
                        if (player.hasEffect(antiDepression) && player.hasEffect(antiMania) && player.getEffect(antiMania).getAmplifier() >= 2) {
                            mentalHealthValue += emotionValue * MENTAL_HEALTH_CHANGE_RATE * mentalTrait.mentalHurtMultiplier / 4;
                        }
                    }
                    else {
                        mentalHealthValue += emotionValue * MENTAL_HEALTH_CHANGE_RATE * mentalTrait.mentalHurtMultiplier;
                    }
                }
                else {
                    mentalHealthValue += emotionValue * MENTAL_HEALTH_CHANGE_RATE;
                }
            }
            mentalHealthValue = Math.max(0d, mentalHealthValue); //保证精神健康值不超过下限
            mentalHealthValue = Math.min(100d, mentalHealthValue); //保证精神健康值不超过上限

            //情绪值自然归零（速度 0.1/s)
            if (mentalIllness.mentalHealthId < 4) {
                if (emotionValue < 0) {
                    double amplifier = 1d;
                    BlockPos respawnPos = player.getRespawnPosition();
                    if (respawnPos != null && Math.sqrt(respawnPos.distToCenterSqr(player.position())) <= 20) {
                        amplifier *= 1.5d;
                    }
                    if (player.level().getBrightness(LightLayer.SKY, player.blockPosition()) >= 13) {
                        amplifier *= 1.5d;
                    }
                    emotionValue += EMOTION_STABILIZE_RATE * Math.abs(emotionValue) / 20d * amplifier;
                    emotionValue = Math.min(0d, emotionValue); //保证情绪值归0
                } else {
                    emotionValue -= EMOTION_STABILIZE_RATE * Math.abs(emotionValue) / 20d;
                    emotionValue = Math.max(0d, emotionValue); //保证情绪值归0
                }
            }

            //更新属性
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
            AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
            if (attributeModifier != null) {
                movementSpeed.removeModifier(speedModifier);
                attackDamage.removeModifier(attributeModifier);
                attackSpeed.removeModifier(attributeModifier);
            }
            double emotionModifier = emotionValue * 1.5d / 100d;
            if (combatCountdown > 0 && emotionModifier < 0) { //如果处于战斗状态且情绪比较负面，则清除速度的负面加成
                speedModifier = new AttributeModifier("depression:speed_modifier", 0, AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            else {
                speedModifier = new AttributeModifier("depression:speed_modifier", emotionModifier - getMentalHealthModifier(), AttributeModifier.Operation.MULTIPLY_TOTAL);
            }
            if (emotionModifier < 0 && !mentalTrait.isBadEmotionLowerCombat) {
                emotionModifier = 0;
            }
            if (emotionModifier > 0 && !mentalTrait.isGoodEmotionHigherCombat) {
                emotionModifier = 0;
            }
            attributeModifier = new AttributeModifier("depression:emotion_modifier", emotionModifier - getMentalHealthModifier(), AttributeModifier.Operation.MULTIPLY_TOTAL);
            movementSpeed.addTransientModifier(speedModifier);
            attackDamage.addTransientModifier(attributeModifier);
            attackSpeed.addTransientModifier(attributeModifier);
            MentalStatusPacket.sendToPlayer(player, this);

            if (combatCountdown > 0) {
                --combatCountdown;
            }

            ptsdManager.tick(player);
        }
        mentalIllness.tick(player);
    }

    private double getMentalHealthModifier() {
        if (mentalIllness.mentalHealthId < 4) {
            return mentalIllness.mentalHealthId / 10d;
        }
        else {
            if (isMania()) {
                return 0;
            }
            else {
                return 0.3d;
            }
        }
    }

    public synchronized boolean viewDetect(Entity entity) {
        Vec3 vec3 = player.getViewVector(1.0F).normalize();
        Vec3 vec32 = new Vec3(entity.getX() - player.getX(), entity.getEyeY() - player.getEyeY(), entity.getZ() - player.getZ());
        double d = vec32.length();
        vec32 = vec32.normalize();
        double e = vec3.dot(vec32);
        return e > 1.0 - 0.5 / d ? player.hasLineOfSight(entity) : false;
    }

    public synchronized void removePTSD(String id) {
        PTSD.remove(id);
        EntityType.byString(id).ifPresentOrElse(
                entityType -> ActionbarHintPacket.sendPTSDDispersePacket(player, entityType.getDescription()),
                () -> {
                    if (playerPTSDSet.contains(id)) {
                        ActionbarHintPacket.sendPTSDDispersePacket(player, Component.literal(id));
                        playerPTSDSet.remove(id);
                    }
                    else {
                        ActionbarHintPacket.sendPTSDDispersePacket(player, Component.translatable("message.depression.damagesource." + id));
                    }
                });
    }

    public synchronized double mentalHeal(double value) {
        if (getMentalHealthId() == 4) {
            return 0;
        }
        double toReturn = value * mentalHealthValue / 100d;
        emotionValue += toReturn; //情绪值 += 治疗值 * 精神健康值 / 100
        emotionValue = Math.min(20d, emotionValue); //保证情绪值不超过上限
        return toReturn;
    }

    public synchronized double mentalHeal(String string, double value, int count) {
        Integer i = boredom.get(string);
        if (i == null) {
            i = 0;
        }
        boredom.put(string, i + count);
        return mentalHeal((Tools.getHarmonic(i+count) - Tools.getHarmonic(i)) * value);
    }
    public synchronized double mentalHeal(String string, double value) {
        if (boredom.containsKey(string)) {
            boredom.put(string, boredom.get(string) + 1);
        }
        else {
            boredom.put(string, 2);
        }
        value /= boredom.get(string) / 2d; //治疗值 /= 无聊值;
        return mentalHeal(value);
    }

    public synchronized void mentalHurt(double value) {
        if (getMentalHealthId() == 4) {
            return;
        }
        emotionValue -= value; //情绪值 -= 伤害值
        emotionValue = Math.max(-20d, emotionValue); //保证情绪值不超过下限
    }

    public synchronized void mentalHurt(Component component, double damage) {
        if (component == player.getDisplayName()) {
            return;
        }
        playerPTSDSet.add(component.getString());
        mentalHurt(component.getString(), damage);
    }
    public synchronized void mentalHurt(String string, double damage) {
        if (string == null) {
            return;
        }
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        int brightness = Math.max(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
        if (brightness <= 7 && mentalTrait.isDarknessAffectEmotion) {
            damage *= 1.3d + (7d - brightness) / 7d * 0.2d;
        }
        if (PTSDTimeBuffer.containsKey(string)) { //如果缓冲区中已经存在PTSD，则更新PTSD缓冲值
            Double originValue = PTSDValueBuffer.get(string);
            if (originValue == null) {
                originValue = 0d;
            }
            PTSDTimeBuffer.put(string, tickCount); //更新PTSD时刻缓冲区
            PTSDValueBuffer.put(string, originValue + damage); //更新PTSD值缓冲区
        }
        else {
            Double PTSDValue = PTSD.get(string);
            if (PTSDValue != null) { //如果此前有过PTSD且缓冲区没有，则计算为犯了PTSD，PTSD影响情绪
                emotionValue -= PTSDValue * PTSD_DAMAGE_RATE; //情绪值 -= PTSD值原量 * 0.25
                emotionValue = Math.max(-20d, emotionValue); //保证情绪值不超过下限
            }
            PTSDTimeBuffer.put(string, tickCount); //加入PTSD时刻缓冲区
            PTSDValueBuffer.put(string, damage); //加入PTSD值缓冲区
        }
        emotionValue -= damage;
        emotionValue = Math.max(-20d, emotionValue); //保证情绪值不超过下限
    }


    public void detectNearbyHealBlock() { //检测周围球形半径的治疗光环方块
        BlockPos pos = player.blockPosition();
        Level level = player.level();
        int r = radiusMaxValue;

        HashMap<Component, Integer> detectedBlocksCount = new HashMap<>();
        HashMap<String, Double> detectedTypesHealValue = new HashMap<>();
        double totalHealValue = 0d;

        for (int x = -r; x <= r; ++x) { // -r <= x <= r
            int yLimit = (int) Math.sqrt(r*r - x*x);
            for (int y = -yLimit; y <= yLimit; ++y) { // -sqrt(r*r - x*x) <= y <= sqrt(r*r - x*x)
                int zLimit = (int) Math.sqrt(r*r - x*x - y*y);
                for (int z = -zLimit; z <= zLimit; ++z) { // -sqrt(r*r - x*x - y*y) <= z <= sqrt(r*r - x*x - y*y)
                    BlockPos blockPos = pos.offset(x, y, z);
                    if (level.getChunk(blockPos).getStatus().isOrAfter(ChunkStatus.FULL)) { //若该区块已加载才进行计算
                        Block block = level.getBlockState(blockPos).getBlock();
                        if (block instanceof FlowerPotBlock) {
                            block = ((FlowerPotBlock) block).getContent();
                        }
                        String id = block.arch$registryName().toString();
                        Component name = block.getName();
                        Double value = getTypeHealValue(id);
                        Integer radius = nearbyHealBlockRadius.get(id);
                        if (radius != null && value != null) {
                            int distance = (int) Math.sqrt(x*x + y*y + z*z);
                            if (distance <= radius) {
                                detectedBlocksCount.put(name, detectedBlocksCount.getOrDefault(name, 0) + 1);
                                String type = nearbyHealBlockType.get(id);
                                if (type == null) {
                                    type = id;
                                }
                                detectedTypesHealValue.put(type, detectedTypesHealValue.getOrDefault(type, 0d) + value / detectedBlocksCount.get(name));
                            }
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, Double> entry : detectedTypesHealValue.entrySet()) {;
            totalHealValue += mentalHeal(entry.getKey(), entry.getValue());
        }

        if (totalHealValue > 0.25d) {
            Component maxHealName = null;
            int mostHealCount = 0;
            for (Map.Entry<Component, Integer> entry : detectedBlocksCount.entrySet()) {
                if (entry.getValue() > mostHealCount) {
                    mostHealCount = entry.getValue();
                    maxHealName = entry.getKey();
                }
            }
            if (maxHealName != null) {
                ActionbarHintPacket.sendNearbyBlockHealPacket(player, maxHealName);
            }
        }
    }

    private Double getTypeHealValue(String id) {
        Double value = nearbyHealBlockValue.get(id);
        if (value == null) {
            return null;
        }
        if (nearbyHealBlockType.containsKey(id)) {
            String type = nearbyHealBlockType.get(id);
            if (boredom.containsKey(type)) {
                return value / (boredom.get(type) / 2d);
            }
            else {
                return value;
            }
        }
        if (boredom.containsKey(id)) {
            return value / (boredom.get(id) / 2d);
        }
        else {
            return value;
        }
    }

    public boolean isMania() {
        return mentalIllness.isMania;
    }

    public int getMentalHealthId() {
        return mentalIllness.mentalHealthId;
    }


    public void readNbt(CompoundTag tag) {
        //读取精神特质
        if (tag.contains("mental_trait")) {
            mentalTrait = MentalTrait.mentalTraits.getOrDefault(tag.getString("mental_trait"), new MentalTrait("normal"));
        }
        //读取无聊值
        CompoundTag boredomTag = tag.getCompound("boredom");
        for (String key : boredomTag.getAllKeys()) {
            boredom.put(key, boredomTag.getInt(key));
        }
        //读取PTSD
        CompoundTag ptsd = tag.getCompound("PTSD");
        for (String key : ptsd.getAllKeys()) {
            PTSD.put(key, ptsd.getDouble(key));
        }
        //读取PTSD时刻缓冲区
        CompoundTag ptsdTimeBuffer = tag.getCompound("PTSD_time_buffer");
        for (String key : ptsdTimeBuffer.getAllKeys()) {
            PTSDTimeBuffer.put(key, tickCount - ptsdTimeBuffer.getLong(key)); //读取造成PTSD的时刻而不是时间
        }
        //读取PTSD值缓冲区
        CompoundTag ptsdValueBuffer = tag.getCompound("PTSD_value_buffer");
        for (String key : ptsdValueBuffer.getAllKeys()) {
            PTSDValueBuffer.put(key, ptsdValueBuffer.getDouble(key));
        }
        //读取玩家PTSD缓冲区
        if (tag.contains("player_ptsd_buffer")) {
            ListTag playerPTSDBufferTag = tag.getList("player_ptsd_buffer", 8);
            for (int i = 0; i < playerPTSDBufferTag.size(); ++i) {
                playerPTSDSet.add(playerPTSDBufferTag.getString(i));
            }
        }
        //读取情绪值
        if (tag.contains("emotion_value")) {
            emotionValue = tag.getDouble("emotion_value");
        }
        //读取精神健康值
        if (tag.contains("mental_health_value")) {
            mentalHealthValue = tag.getDouble("mental_health_value");
        }
        //读取战斗状态倒计时
        if (tag.contains("combat_countdown")) {
            combatCountdown = tag.getInt("combat_countdown");
        }
        //读取精神疾病相关内容
        CompoundTag mentalIllnessTag = tag.getCompound("mental_illness");
        mentalIllness.readNbt(mentalIllnessTag);
        //读取PTSD管理相关内容
        CompoundTag ptsdManagerTag = tag.getCompound("ptsd_manager");
        ptsdManager.readNbt(ptsdManagerTag);
    }

    public void writeNbt(CompoundTag tag) {
        //写入精神特质
        if (mentalTrait != null) {
            tag.putString("mental_trait", mentalTrait.id);
        }
        //写入无聊值
        CompoundTag boredomTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : boredom.entrySet()) {
            boredomTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("boredom", boredomTag);
        //写入PTSD
        CompoundTag ptsd = new CompoundTag();
        for (Map.Entry<String, Double> entry : PTSD.entrySet()) {
            ptsd.putDouble(entry.getKey(), entry.getValue());
        }
        tag.put("PTSD", ptsd);
        //写入PTSD时刻缓冲区
        CompoundTag ptsdTimeBuffer = new CompoundTag();
        for (Map.Entry<String, Long> entry : PTSDTimeBuffer.entrySet()) {
            ptsdTimeBuffer.putLong(entry.getKey(), tickCount - entry.getValue()); //存储造成PTSD的时间而不是时刻
        }
        tag.put("PTSD_time_buffer", ptsdTimeBuffer);
        //写入PTSD值缓冲区
        CompoundTag ptsdValueBuffer = new CompoundTag();
        for (Map.Entry<String, Double> entry : PTSDValueBuffer.entrySet()) {
            ptsdValueBuffer.putDouble(entry.getKey(), entry.getValue());
        }
        tag.put("PTSD_value_buffer", ptsdValueBuffer);
        //写入玩家PTSD缓冲区
        if (!playerPTSDSet.isEmpty()) {
            ListTag playerPTSDBufferTag = new ListTag();
            for (String string : playerPTSDSet) {
                playerPTSDBufferTag.add(StringTag.valueOf(string));
            }
            tag.put("player_ptsd_buffer", playerPTSDBufferTag);
        }
        //写入情绪值
        tag.putDouble("emotion_value", emotionValue);
        //写入精神健康值
        tag.putDouble("mental_health_value", mentalHealthValue);
        //写入战斗状态倒计时
        tag.putDouble("combat_countdown", combatCountdown);
        //写入精神疾病相关内容
        CompoundTag mentalIllnessTag = new CompoundTag();
        mentalIllness.writeNbt(mentalIllnessTag);
        tag.put("mental_illness", mentalIllnessTag);
        //写入PTSD管理相关内容
        CompoundTag ptsdManagerTag = new CompoundTag();
        ptsdManager.writeNbt(ptsdManagerTag);
        tag.put("ptsd_manager", ptsdManagerTag);
    }

    public static MentalStatus getMentalStatusByServerPlayer(final Player player) {
        final var uuid = player.getUUID();
        var instance = Registry.mentalStatus.get(uuid);
        if (instance == null && player instanceof ServerPlayer serverPlayer) {
            instance = new MentalStatus(serverPlayer);
            Registry.mentalStatus.put(uuid, instance);
        } else if(!(player instanceof ServerPlayer)) {
            throw new RuntimeException("You are not a ServerPlayer");
        }
        return instance;
    }

    public static double onLoot(Player player, ItemStack itemStack) {
        Item item = itemStack.getItem();
        String id = item.arch$registryName().toString();
        MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
        double idHealValue = 0;
        double enchantmentHealValue = 0;
        if (lootHealItem.containsKey(id)) {
            idHealValue = mentalStatus.mentalHeal(id, lootHealItem.get(id), itemStack.getCount());
        }
        if (itemStack.isEnchanted()) { //如果钓到的东西有附魔

            for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(itemStack).entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                if (enchantment.isCurse()) {
                    enchantmentHealValue -= (double) entry.getValue() / (double) enchantment.getRarity().getWeight() * level;
                }
                else {
                    enchantmentHealValue += (double) entry.getValue() / (double) enchantment.getRarity().getWeight() * level;
                }
            }
            if (enchantmentHealValue > 0) {
                mentalStatus.mentalHeal(enchantmentHealValue);
            }
        }
        return idHealValue + enchantmentHealValue;
    }
}
