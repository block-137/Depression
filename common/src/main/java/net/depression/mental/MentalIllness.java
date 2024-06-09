package net.depression.mental;

import net.depression.network.ActionbarHintPacket;
import net.depression.network.CloseEyePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Random;

public class MentalIllness {
    public int mentalHealthLevel; //精神健康等级
    public Boolean isInsomnia;
    private int sleepAttemptCount = 0;
    private Long nextCloseEyeTime;
    public final Random random = new Random();
    private final MentalStatus mentalStatus;
    private ServerPlayer player;

    //以下是临时变量
    public ItemStack lastEatItem;

    public MentalIllness(ServerPlayer player, MentalStatus mentalStatus) {
        this.player = player;
        this.mentalStatus = mentalStatus;
    }

    public void tick(ServerPlayer player) {
        this.player = player;
        mentalHealthLevel = getMentalHealthLevel(mentalStatus.mentalHealthValue);
        if (isInsomnia != null && isInsomnia && mentalHealthLevel == 0) {
            isInsomnia = false;
        }
        //重置失眠状态
        Level level = player.level();
        if (level.getDayTime() == 12000) {
            sleepAttemptCount = 0;
            setIsInsomnia();
        }
        //处理是否失眠
        if (player.isSleepingLongEnough()) {
            if (isInsomnia && random.nextDouble() < getInsomniaChance()) { //失眠概率随睡眠次数递减
                player.stopSleeping();
                ++sleepAttemptCount;
                mentalStatus.mentalHurt(2.5d);
                ActionbarHintPacket.sendInsomniaPacket(player);
            }
        }
        if (mentalHealthLevel >= 3) {
            if (nextCloseEyeTime == null) {
                setNextCloseEyeTime();
            }
            else if (mentalStatus.tickCount >= nextCloseEyeTime) {
                CloseEyePacket.sendToPlayer(player);
                setNextCloseEyeTime();
            }
        }
    }

    public void trigMentalFatigue() {
        if (mentalHealthLevel > 0) {
            boolean ifTrig;
            switch (mentalHealthLevel) {
                case 1 -> {
                    ifTrig = random.nextDouble() < 0.01d;
                }
                case 2 -> {
                    ifTrig = random.nextDouble() < 0.02d;
                }
                case 3 -> {
                    ifTrig = random.nextDouble() < 0.03d;
                }
                default -> {
                    ifTrig = false;
                }
            }
            if (ifTrig) {
                int duration;
                int amplifier;
                switch (mentalHealthLevel) {
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
                        duration = 0;
                        amplifier = 0;
                    }
                }
                Collection<MobEffectInstance> effects = player.getActiveEffects();
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
        switch (mentalHealthLevel) {
            case 1 -> isInsomnia = random.nextDouble() < 0.5d; //若轻度抑郁，则50%概率失眠
            case 2 -> isInsomnia = random.nextDouble() < 0.75d; //若中度抑郁，则75%概率失眠
            case 3 -> isInsomnia = true;
            default -> isInsomnia = false;
        }
    }

    public void setNextCloseEyeTime() {
        nextCloseEyeTime = (30 + dice(6, 10)) * 20L + mentalStatus.tickCount;
    }

    public double getInsomniaChance() {
        switch (mentalHealthLevel) {
            case 1 -> {
                return 1d - sleepAttemptCount * 0.16d;
            }
            case 2 -> {
                return 1d - sleepAttemptCount * 0.12d;
            }
            case 3 -> {
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
    public static int getMentalHealthLevel(double mentalHealthValue) {
        if (mentalHealthValue >= 70d && mentalHealthValue <= 100d) {
            return 0; // 健康：绿色
        } else if (mentalHealthValue >= 40d && mentalHealthValue < 70d) {
            return 1; // 轻度抑郁：黄色
        } else if (mentalHealthValue >= 20d && mentalHealthValue < 40d) {
            return 2; // 中度抑郁：红色
        } else if (mentalHealthValue >= 0d && mentalHealthValue < 20d) {
            return 3; // 重度抑郁：灰色
        } else {
            return 4; // TODO:4代表双相，目前还没做
        }
    }
    public void readNbt(CompoundTag tag) {
        //读取当天是否失眠
        if (tag.contains("is_insomnia")) {
            isInsomnia = tag.getBoolean("is_insomnia");
        }
        //读取睡眠尝试次数
        sleepAttemptCount = tag.getInt("sleep_attempt_count");
        //读取下次闭眼时间
        if (tag.contains("next_close_eye_time")) {
            nextCloseEyeTime = mentalStatus.tickCount + tag.getLong("next_close_eye_time"); //读取闭眼的时刻而不是剩余时间
        }
    }

    public void writeNbt(CompoundTag tag) {
        //写入当天是否失眠
        if (isInsomnia != null) {
            tag.putBoolean("is_insomnia", isInsomnia);
        }
        //写入睡眠尝试次数
        tag.putInt("sleep_attempt_count", sleepAttemptCount);
        //写入下次闭眼时间
        if (nextCloseEyeTime != null) {
            tag.putLong("next_close_eye_time", nextCloseEyeTime - mentalStatus.tickCount); //存储闭眼的剩余时间而不是时刻
        }
    }
}
