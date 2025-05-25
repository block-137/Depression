package net.depression.mental;

import dev.architectury.networking.NetworkManager;
import net.depression.client.ClientMentalStatus;
import net.depression.mixin.MobAccess;
import net.depression.network.ActionbarHintPacket;
import net.depression.network.PTSDOnsetPacket;
import net.depression.server.Registry;
import net.depression.sound.ModSounds;
import net.depression.util.TempValues;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PTSDManager {
    public static final double PTSD_MAX_VALUE = 40d;
    public static final double PTSD_4_VALUE = 36d;
    public static final double PTSD_3_VALUE = 32d;
    public static final double PTSD_2_VALUE = 26d;
    public static final double PTSD_1_VALUE = 20d;
    public static double KILL_PTSD_DECREASE;
    public static double ONSET_EMOTION_DECREASE;
    public static HashMap<String, List<String>> soundEventMap = new HashMap<>(); // SoundEvent ID -> DamageSource ID
    public static HashMap<String, List<SoundEvent>> damageSourceMap = new HashMap<>(); // DamageSource ID -> SoundEvent
    private final MentalStatus mentalStatus;
    private final ConcurrentHashMap<String, Double> PTSD;
    private double currentMaxPTSDValue;
    private final ConcurrentHashMap<String, Integer> contactValue = new ConcurrentHashMap<>(); //与PTSD源接触不受伤害的总时长（单位：秒）
    private final ConcurrentHashMap<String, Integer> remainingValue = new ConcurrentHashMap<>(); //与PTSD接触后的倒计时（单位：秒）
    private final ConcurrentHashMap<String, ArrayDeque<Entity>> entities = new ConcurrentHashMap<>(); //PTSD源
    private Integer phonismCountdown;
    private Integer photismCountdown;
    public final LinkedHashSet<String> phonismId = new LinkedHashSet<>(); //可以造成幻听的伤害源ID
    public final LinkedHashSet<String> photismId = new LinkedHashSet<>();
    private final Random random = new Random();

    public PTSDManager(MentalStatus mentalStatus, ConcurrentHashMap<String, Double> PTSD) {
        this.mentalStatus = mentalStatus;
        this.PTSD = PTSD;
    }

    public static void addEntry(String damageSource, String soundEvent) {
        soundEventMap.computeIfAbsent(soundEvent, k -> new ArrayList<>()).add(damageSource);
        damageSourceMap.computeIfAbsent(damageSource, k -> new ArrayList<>())
                .add(SoundEvent.createVariableRangeEvent(new ResourceLocation(soundEvent)));
    }

    public synchronized void tick(ServerPlayer player) { //每秒调用一次
        boolean ifSendRelief = false;
        currentMaxPTSDValue = 0;
        for (String key : remainingValue.keySet()) {
            Double ptsdValue = PTSD.get(key);
            if (ptsdValue == null) {
                remainingValue.remove(key);
                contactValue.remove(key);
                entities.remove(key);
                continue;
            }
            Integer contactCount = contactValue.get(key);
            if (contactCount == null) {
                remainingValue.remove(key);
                entities.remove(key);
                continue;
            }
            double initialDecreaseValue = (ptsdValue > PTSD_1_VALUE ? -0.1d : 0d);
            double decreaseValue = initialDecreaseValue + contactCount * 0.005;
            ptsdValue -= Math.min(decreaseValue, 0.1d);
            ptsdValue = Math.min(ptsdValue, PTSD_MAX_VALUE);
            PTSD.put(key, ptsdValue);
            if (decreaseValue > 0.05 && initialDecreaseValue + (contactCount - 1) * 0.005 <= 0.05) {
                EntityType.byString(key).ifPresentOrElse(
                        entityType -> ActionbarHintPacket.sendPTSDRemissionPacket(player, entityType.getDescription()),
                        () -> {
                            if (mentalStatus.playerPTSDSet.contains(key)) {
                                ActionbarHintPacket.sendPTSDRemissionPacket(player, Component.literal(key));
                            }
                            else {
                                ActionbarHintPacket.sendPTSDRemissionPacket(player, Component.translatable("message.depression.damagesource." + key));
                            }
                        }
                );
            }
            if (ptsdValue < 0) { //PTSD值小于0就移除
                remainingValue.remove(key);
                contactValue.remove(key);
                entities.remove(key);
                mentalStatus.removePTSD(key);
                continue;
            }
            int remainingTime = remainingValue.get(key);
            if (remainingTime > 1) {
                remainingValue.put(key, remainingTime - 1);
                contactValue.put(key, contactValue.get(key) + 1);
                currentMaxPTSDValue = Math.max(currentMaxPTSDValue, ptsdValue);
            }
            else {
                remainingValue.remove(key);
                contactValue.remove(key);
                entities.remove(key);
                currentMaxPTSDValue = 0;
                for (String key2 : remainingValue.keySet()) { //要是移除了一个，就重新检查谁造成的PTSD是最大的
                    double ptsdValue2 = PTSD.get(key2);
                    if (ptsdValue2 > PTSD_1_VALUE) {
                        currentMaxPTSDValue = Math.max(currentMaxPTSDValue, ptsdValue2);
                    }
                }
                if (currentMaxPTSDValue <= PTSD_1_VALUE) {
                    ifSendRelief = true;
                }
            }
        }
        double distance = 24;
        if (!ifSendRelief) {
            for (String key : entities.keySet()) {
                ArrayDeque<Entity> list = entities.get(key);
                list.removeIf(entity -> entity == null || entity.isRemoved()); //移除已经死亡的实体
                for (Entity entity : list) {
                    double dis = player.distanceTo(entity);
                    distance = Math.min(distance, dis);
                }
            }
        }
        if (currentMaxPTSDValue > PTSD_4_VALUE) {
            mentalStatus.mentalHurt(ONSET_EMOTION_DECREASE * 4);
            if (photismCountdown == null) {
                photismCountdown = 30 + (int) mentalStatus.emotionValue;
            }
            else if (--photismCountdown == 0) {
                phonismOnset(player);   //幻视的同时也会幻听
                if (!photismId.isEmpty()) {
                    int index = random.nextInt(photismId.size());
                    for (String key : photismId) {
                        if (index-- == 0) {
                            PTSDOnsetPacket.sendPhotismPacket(player, key);
                            break;
                        }
                    }
                }
                photismCountdown = 30 + (int) mentalStatus.emotionValue;
            }
            player.playSound(ModSounds.PANT.get());
            PTSDOnsetPacket.sendToPlayer(player, 4, distance);
        }
        else if (currentMaxPTSDValue > PTSD_3_VALUE) {
            mentalStatus.mentalHurt(ONSET_EMOTION_DECREASE * 3);
            if (phonismCountdown == null) {
                phonismCountdown = 60 + (int) mentalStatus.emotionValue * 2;
            }
            else if (--phonismCountdown == 0) {
                phonismOnset(player);
                phonismCountdown = 60 + (int) mentalStatus.emotionValue * 2;
            }
            player.playSound(ModSounds.PANT.get());
            PTSDOnsetPacket.sendToPlayer(player, 3, distance);
        }
        else if (currentMaxPTSDValue > PTSD_2_VALUE) {
            mentalStatus.mentalHurt(ONSET_EMOTION_DECREASE * 2);
            player.playSound(ModSounds.PANT.get());
            PTSDOnsetPacket.sendToPlayer(player, 2, distance);
        }
        else if (currentMaxPTSDValue > PTSD_1_VALUE) {
            mentalStatus.mentalHurt(ONSET_EMOTION_DECREASE * 1);
            player.playSound(ModSounds.PANT.get(), 0.5f, 1f);
            PTSDOnsetPacket.sendToPlayer(player, 1, distance);
        }
        else if (ifSendRelief) {
            PTSDOnsetPacket.sendToPlayer(player, 0, -1);
        }
    }

    public void phonismOnset(ServerPlayer player) {
        if (phonismId.isEmpty()) {
            return;
        }
        int index = random.nextInt(phonismId.size());
        for (String key : phonismId) {
            if (index-- == 0) {
                if (damageSourceMap.containsKey(key)) {
                    List<SoundEvent> soundEvents = damageSourceMap.get(key);
                    int index2 = random.nextInt(soundEvents.size());
                    for (SoundEvent soundEvent : soundEvents) {
                        if (index2-- == 0) {
                            player.playNotifySound(soundEvent, player.getSoundSource(), 1f, 1f);
                            break;
                        }
                    }
                }
                else {
                    EntityType.byString(key).ifPresent(entityType -> {
                        if (Mob.class.isAssignableFrom(entityType.getBaseClass())) {
                            SoundEvent soundEvent = ((MobAccess) entityType.create(player.level())).invokeGetAmbientSound();
                            if (soundEvent != null) {
                                player.playNotifySound(soundEvent, player.getSoundSource(), 1f, 1f);
                            }
                        }
                    });
                }
                break;
            }
        }
    }

    public boolean hasRemaining() {
        return !remainingValue.isEmpty();
    }

    public void clear(ServerPlayer player) {
        contactValue.clear();
        remainingValue.clear();
        entities.clear();
        currentMaxPTSDValue = 0;
        phonismCountdown = null;
        photismCountdown = null;
        phonismId.clear();
        photismId.clear();
        PTSDOnsetPacket.sendToPlayer(player, 0, -1);
    }

    public void hurt(String key) {
        if (contactValue.containsKey(key)) {
            contactValue.put(key, 0);
            remainingValue.put(key, 30); //30秒
        }
    }


    public static void onPlaySound(ServerPlayer player) {
        if (TempValues.broadcastEntity != null) {
            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
            if (mentalStatus != null) {
                mentalStatus.ptsdManager.trigger(TempValues.broadcastEntity);
            }
        }
        if (TempValues.broadcastDamageSource != null) {
            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
            if (mentalStatus != null) {
                for (String damageSource : TempValues.broadcastDamageSource) {
                    mentalStatus.ptsdManager.trigger(damageSource);
                }
            }
        }
    }

    public static void receivePlaySoundPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        PTSDManager manager = Registry.mentalStatus.get(packetContext.getPlayer().getUUID()).ptsdManager;
        String soundEvent = buf.readCharSequence(buf.readableBytes(), StandardCharsets.UTF_8).toString();
        for (String damageSource : soundEventMap.get(soundEvent)) {
            manager.trigger(damageSource);
        }
    }

    public void trigger(String key) {
        if (remainingValue.containsKey(key)) {
            remainingValue.put(key, 30); //30秒
        }
        else if (PTSD.containsKey(key)) {
            Double value = PTSD.get(key);
            currentMaxPTSDValue = Math.max(currentMaxPTSDValue, value);
            if (value > 6) {
                if (!contactValue.containsKey(key)) {
                    contactValue.put(key, 0);
                }
                remainingValue.put(key, 30); //30秒
            }
        }
    }

    public void trigger(Entity entity) {
        String key = entity.getEncodeId();
        if (key == null) {
            return;
        }
        if (remainingValue.containsKey(key)) {
            remainingValue.put(key, 30); //30秒
            entities.computeIfAbsent(key, k -> new ArrayDeque<>()).add(entity);
        }
        else if (PTSD.containsKey(key)) {
            Double value = PTSD.get(key);
            currentMaxPTSDValue = Math.max(currentMaxPTSDValue, value);
            if (value > 6) {
                if (!contactValue.containsKey(key)) {
                    contactValue.put(key, 0);
                    entities.computeIfAbsent(key, k -> new ArrayDeque<>()).add(entity);
                }
                remainingValue.put(key, 30); //30秒
            }
        }
    }

    public void readNbt(CompoundTag tag) {
        if (tag.contains("contact_value")) {
            CompoundTag contactValueTag = tag.getCompound("contact_value");
            for (String key : contactValueTag.getAllKeys()) {
                contactValue.put(key, contactValueTag.getInt(key));
            }
        }
        if (tag.contains("remaining_value")) {
            CompoundTag remainingValueTag = tag.getCompound("remaining_value");
            for (String key : remainingValueTag.getAllKeys()) {
                remainingValue.put(key, remainingValueTag.getInt(key));
            }
        }
        if (tag.contains("phonism_countdown")) {
            phonismCountdown = tag.getInt("phonism_countdown");
        }
        if (tag.contains("photism_countdown")) {
            photismCountdown = tag.getInt("photism_countdown");
        }
    }

    public void writeNbt(CompoundTag tag) {
        if (!contactValue.isEmpty()) {
            CompoundTag contactValueTag = new CompoundTag();
            for (String key : contactValue.keySet()) {
                contactValueTag.putInt(key, contactValue.get(key));
            }
            tag.put("contact_value", contactValueTag);
        }
        if (!remainingValue.isEmpty()) {
            CompoundTag remainingValueTag = new CompoundTag();
            for (String key : remainingValue.keySet()) {
                remainingValueTag.putInt(key, remainingValue.get(key));
            }
            tag.put("remaining_value", remainingValueTag);
        }
        if (phonismCountdown != null) {
            tag.putInt("phonism_countdown", phonismCountdown);
        }
        else {
            tag.remove("phonism_countdown");
        }
        if (photismCountdown != null) {
            tag.putInt("photism_countdown", photismCountdown);
        }
        else {
            tag.remove("photism_countdown");
        }
    }
}
