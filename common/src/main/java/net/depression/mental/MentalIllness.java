package net.depression.mental;

import net.depression.effect.ModEffects;
import net.depression.item.MedicineItem;
import net.depression.network.ActionbarHintPacket;
import net.depression.network.CloseEyePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MentalIllness {
    public int mentalHealthId; //精神健康ID
    public int mentalHealthLevel; //精神健康等级
    public boolean isMania;
    public Long startIllnessTime; //（仅双相）开始时间
    public Boolean isInsomnia;
    public int sleepAttemptCount = 0;
    private Long nextCloseEyeTime;
    public final HashMap<String, Integer> medicineDelay = new HashMap<>();
    public final Random random = new Random();
    private final MentalStatus mentalStatus;
    private ServerPlayer player;

    public ArrayList<MentalIllnessPool> mentalIllnessPools = new ArrayList<>() {{
        MentalIllnessPool pool0 = new MentalIllnessPool();
        MentalIllnessPool pool1 = new MentalIllnessPool();
        MentalIllnessPool pool2 = new MentalIllnessPool();
        MentalIllnessPool pool3 = new MentalIllnessPool();
        pool0.addIllness(0, 1d);
        pool1.addIllness(1, 1d);
        pool2.addIllness(2, 1d);
        pool3.addIllness(3, 4d);
        pool3.addIllness(4, 1d);
        add(pool0);
        add(pool1);
        add(pool2);
        add(pool3);
    }};

    public MentalIllness(ServerPlayer player, MentalStatus mentalStatus) {
        this.player = player;
        this.mentalStatus = mentalStatus;
    }

    public void tick(ServerPlayer player) {
        this.player = player;
        for (String key : medicineDelay.keySet()) {
            int delay = medicineDelay.get(key);
            if (delay > 0) {
                medicineDelay.put(key, delay - 1);
            } else {
                player.addEffect(MedicineItem.effectMap.get(key));
                medicineDelay.remove(key);
            }
        }
        if (mentalHealthId == 4 && startIllnessTime == null) {
            mentalHealthLevel = 3;
            startIllnessTime = player.level().getGameTime();
        }
        if (mentalHealthId != 4 && startIllnessTime != null) {
            mentalStatus.emotionValue = 0;
            startIllnessTime = null;
        }
        int newMentalHealthLevel = getMentalHealthLevel(mentalStatus.mentalHealthValue);
        if (newMentalHealthLevel != mentalHealthLevel) {
            if (mentalHealthId == 4) { //如果双相痊愈，则将情绪归零。
                mentalStatus.emotionValue = 0;
            }
            mentalHealthLevel = newMentalHealthLevel;
            MentalIllnessPool mentalIllnessPool = mentalIllnessPools.get(mentalHealthLevel);
            double multiplier = mentalStatus.mentalTrait.bipolarChanceMultiplier;
            if (multiplier == 1 || mentalHealthLevel != 3) {
                mentalHealthId = mentalIllnessPool.getIllness();
            } else {
                mentalHealthId = mentalIllnessPool.getIllness(4, multiplier);
            }
            if (mentalHealthId == 4) {
                startIllnessTime = player.level().getGameTime();
            } else {
                startIllnessTime = null;
            }
        }
        if (startIllnessTime != null) {
            long currentTime = player.level().getGameTime();
            if ((currentTime - startIllnessTime) % 30000 == 0) {
                isMania = true;
                mentalStatus.emotionValue = 20d;
                ActionbarHintPacket.sendBipolarPacket(player, true);
            }
            if ((currentTime - startIllnessTime) % 30000 == 6000) {
                isMania = false;
                mentalStatus.emotionValue = -20d;
                ActionbarHintPacket.sendBipolarPacket(player, false);
            }
        }

        if (isInsomnia != null && isInsomnia && mentalHealthId == 0) {
            isInsomnia = false;
        }
        boolean isSleepy = player.hasEffect(ModEffects.SLEEPINESS.get());
        //处理是否失眠
        if (player.isSleepingLongEnough() && !isSleepy) {
            if (isInsomnia && random.nextDouble() < getInsomniaChance()) { //失眠概率随睡眠次数递减
                player.stopSleeping();
                ++sleepAttemptCount;
                mentalStatus.mentalHurt(2.5d);
                ActionbarHintPacket.sendInsomniaPacket(player);
            }
        }
        if ((mentalHealthId >= 3 && !isMania) || isSleepy) {
            if (nextCloseEyeTime == null) {
                setNextCloseEyeTime();
            }
            else if (mentalStatus.tickCount >= nextCloseEyeTime) {
                CloseEyePacket.sendToPlayer(player);
                setNextCloseEyeTime();
            }
        }
    }

    public double getMentalFatigueChance() {
        switch (mentalHealthId) {
            case 1:
                return 0.02;
            case 2:
                return 0.04;
            case 3:
                return 0.06;
            case 4:
                if (isMania) {
                    return 0;
                }
                else {
                    return 0.06;
                }
            default:
                return 0;
        }
    }

    public void trigMentalFatigue() {
        if (mentalHealthId > 0) {
            double chance = getMentalFatigueChance() * mentalStatus.mentalTrait.fatigueChanceMultiplier;
            MobEffectInstance antiDepression = player.getEffect(ModEffects.ANTI_DEPRESSION.get());
            if (antiDepression != null) {
                chance += (antiDepression.getAmplifier() + 1) * 0.02;
            }
            if (random.nextDouble() < chance) {
                int duration;
                int amplifier;
                switch (mentalHealthId) {
                    case 1 -> {
                        duration = 60;
                        amplifier = 0;
                    }
                    case 2 -> {
                        duration = 100;
                        amplifier = 0;
                    }
                    case 3 -> {
                        duration = 200;
                        amplifier = 1;
                    }
                    default -> {
                        duration = 60;
                        amplifier = 0;
                    }
                }
                MobEffectInstance mining_fatigue = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, amplifier, false, true, true);
                MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier, false, true, true);
                MobEffectInstance weakness = new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier, false, true, true);
                player.addEffect(mining_fatigue);
                player.addEffect(slowness);
                player.addEffect(weakness);
                ActionbarHintPacket.sendMentalFatiguePacket(player);
            }
        }
    }

    public void setIsInsomnia() { //计算是否失眠
        sleepAttemptCount = 0;
        switch (mentalHealthId) {
            case 1 -> isInsomnia = random.nextDouble() < 0.5d; //若轻度抑郁，则50%概率失眠
            case 2 -> isInsomnia = random.nextDouble() < 0.75d; //若中度抑郁，则75%概率失眠
            case 3 -> isInsomnia = true;
            case 4 -> isInsomnia = true;
            default -> isInsomnia = false;
        }
    }

    public void setNextCloseEyeTime() {
        nextCloseEyeTime = (30 + dice(6, 10)) * 20L + mentalStatus.tickCount;
    }

    public double getInsomniaChance() {
        switch (mentalHealthId) {
            case 1 -> {
                return 1d - sleepAttemptCount * 0.16d;
            }
            case 2 -> {
                return 1d - sleepAttemptCount * 0.12d;
            }
            case 3, 4 -> {
                return 1d - sleepAttemptCount * 0.08d;
            }
            default -> {
                return 0;
            }
        }
    }
    public int dice(int a, int b) {
        int ret = 0;
        for (int i = 0; i < a; ++i) {
            ret += random.nextInt(b) + 1;
        }
        return ret;
    }
    private int getMentalHealthLevel(double mentalHealthValue) {
        if (mentalHealthValue >= 70d && mentalHealthValue <= 100d) {
            return 0; // 健康：绿色
        } else if (mentalHealthValue >= 40d && mentalHealthValue < 70d) {
            return 1; // 轻度抑郁：黄色
        } else if (mentalHealthValue >= 20d && mentalHealthValue < 40d) {
            return 2; // 中度抑郁：红色
        } else if (mentalHealthValue >= 0d && mentalHealthValue < 20d) {
            return 3; // 重度抑郁：灰色
        }
        return 0;
    }
    public void readNbt(CompoundTag tag) {
        //读取精神健康ID
        mentalHealthId = tag.getInt("mental_health_id");
        //读取精神健康等级
        mentalHealthLevel = tag.getInt("mental_health_level");
        //读取当天是否失眠
        if (tag.contains("is_insomnia")) {
            isInsomnia = tag.getBoolean("is_insomnia");
        }
        //读取是否躁狂
        isMania = tag.getBoolean("is_mania");
        //读取病症开始时间
        if (tag.contains("start_illness_time")) {
            startIllnessTime = tag.getLong("start_illness_time");
        }
        //读取睡眠尝试次数
        sleepAttemptCount = tag.getInt("sleep_attempt_count");
        //读取下次闭眼时间
        if (tag.contains("next_close_eye_time")) {
            nextCloseEyeTime = mentalStatus.tickCount + tag.getLong("next_close_eye_time"); //读取闭眼的时刻而不是剩余时间
        }
        //读取药物延迟
        if (tag.contains("medicine_delay")) {
            CompoundTag medicineDelayTag = tag.getCompound("medicine_delay");
            for (String key : medicineDelayTag.getAllKeys()) {
                medicineDelay.put(key, medicineDelayTag.getInt(key));
            }
        }
    }

    public void writeNbt(CompoundTag tag) {
        //写入精神健康ID
        tag.putInt("mental_health_id", mentalHealthId);
        //写入精神健康等级
        tag.putInt("mental_health_level", mentalHealthLevel);
        //写入当天是否失眠
        if (isInsomnia != null) {
            tag.putBoolean("is_insomnia", isInsomnia);
        }
        //写入是否躁狂
        tag.putBoolean("is_mania", isMania);
        //写入病症开始时间
        if (startIllnessTime != null) {
            tag.putLong("start_illness_time", startIllnessTime);
        }
        //写入睡眠尝试次数
        tag.putInt("sleep_attempt_count", sleepAttemptCount);
        //写入下次闭眼时间
        if (nextCloseEyeTime != null) {
            tag.putLong("next_close_eye_time", nextCloseEyeTime - mentalStatus.tickCount); //存储闭眼的剩余时间而不是时刻
        }
        //写入药物延迟
        if (!medicineDelay.isEmpty()) {
            CompoundTag medicineDelayTag = new CompoundTag();
            for (String key : medicineDelay.keySet()) {
                medicineDelayTag.putInt(key, medicineDelay.get(key));
            }
            tag.put("medicine_delay", medicineDelayTag);
        }
    }
}
