package net.depression.item.diary;

import net.depression.server.Registry;
import net.depression.server.StatManager;
import net.minecraft.stats.Stats;

public class ConditionComponents {
    public static final ConditionComponent HEALTHY_1_WEATHER = new ConditionComponent(player -> {
        if (player.level().isRainingAt(player.blockPosition())) {
            return "'diary.depression.healthy_1.weather_rain'";
        }
        else {
            return "'diary.depression.healthy_1.weather_clear'";
        }
    });
    public static final ConditionComponent HEALTHY_1_BREED = new ConditionComponent(Stats.ANIMALS_BRED, 1, "'diary.depression.healthy_1.breed'");
    public static final ConditionComponent HEALTHY_1_EAT = new ConditionComponent(player -> {
        if (Registry.statManager.get(player.getUUID()).hasAte) {
            return "'diary.depression.healthy_1.eat'";
        }
        else {
            return "";
        }
    });
    public static final ConditionComponent HEALTHY_1_KILL_MOBS = new ConditionComponent(Stats.MOB_KILLS, 15, "'diary.depression.healthy_1.kill_mobs'");
    public static final ConditionComponent HEALTHY_2_BREED = new ConditionComponent(Stats.ANIMALS_BRED, 1, "'diary.depression.healthy_2.breed'");
    public static final ConditionComponent HEALTHY_2_WEATHER = new ConditionComponent(player -> {
        if (player.level().isRainingAt(player.blockPosition())) {
            return "'diary.depression.healthy_2.weather_rain'";
        }
        else {
            return "'diary.depression.healthy_2.weather_clear'";
        }
    });
    public static final ConditionComponent HEALTHY_2_MOVE_IN_WEATHER = new ConditionComponent(player -> {
        int statsNum = player.getStats().getValue(Stats.CUSTOM, Stats.WALK_ONE_CM);
        StatManager statManager = Registry.statManager.get(player.getUUID());
        statsNum -= statManager.getStat(Stats.WALK_ONE_CM); //减去昨天的旧统计数据
        if (player.level().isRainingAt(player.blockPosition())) {
            if (statsNum >= 200) {
                return "'diary.depression.healthy_2.move_in_rain'";
            }
            else {
                return "'diary.depression.healthy_2.stay_in_house'";
            }
        }
        else {
            if (statsNum >= 200) {
                return "'diary.depression.healthy_2.move_in_clear'";
            }
            else {
                return "";
            }
        }
    });
    public static final ConditionComponent MILD_DEPRESSION_1_WEATHER = new ConditionComponent(player -> {
        if (player.level().isRainingAt(player.blockPosition())) {
            return "'diary.depression.mild_depression_1.weather_rain'";
        }
        else {
            return "'diary.depression.mild_depression_1.weather_clear'";
        }
    });
    public static final ConditionComponent MILD_DEPRESSION_1_MOVE_IN_WEATHER = new ConditionComponent(player -> {
        int statsNum = player.getStats().getValue(Stats.CUSTOM, Stats.WALK_ONE_CM);
        StatManager statManager = Registry.statManager.get(player.getUUID());
        statsNum -= statManager.getStat(Stats.WALK_ONE_CM); //减去昨天的旧统计数据
        if (player.level().isRainingAt(player.blockPosition())) {
            if (statsNum >= 200) {
                return "'diary.depression.mild_depression_1.move_in_rain'";
            }
            else {
                return "";
            }
        }
        else {
            if (statsNum >= 200) {
                return "'diary.depression.mild_depression_1.move_in_clear'";
            }
            else {
                return "";
            }
        }
    });
    public static final ConditionComponent MILD_DEPRESSION_1_MOVE = new ConditionComponent(Stats.WALK_ONE_CM, 200, "'diary.depression.mild_depression_1.move'");
    public static final ConditionComponent MILD_DEPRESSION_2_WEATHER = new ConditionComponent(player -> {
        if (player.level().isRainingAt(player.blockPosition())) {
            return "'diary.depression.mild_depression_2.weather_rain'";
        }
        else {
            return "'diary.depression.mild_depression_2.weather_clear'";
        }
    });
    public static final ConditionComponent MILD_DEPRESSION_2_EAT = new ConditionComponent(player -> {
        if (Registry.statManager.get(player.getUUID()).hasAte) {
            return "'diary.depression.mild_depression_2.eat'";
        }
        else {
            return "";
        }
    });
    public static final ConditionComponent MILD_DEPRESSION_2_HURT = new ConditionComponent(Stats.DAMAGE_TAKEN, 5, "'diary.depression.mild_depression_2.hurt'");
    //Todo 下面这里的条件以后要改成实体击杀
    public static final ConditionComponent MODERATE_DEPRESSION_1_KILL_ENTITIES = new ConditionComponent(Stats.MOB_KILLS, 1, "'diary.depression.moderate_depression_1.kill_entities'");
    public static final ConditionComponent MODERATE_DEPRESSION_1_WEATHER = new ConditionComponent(player -> {
        if (player.level().isRainingAt(player.blockPosition())) {
            return "'diary.depression.moderate_depression_1.weather_rain'";
        }
        else {
            return "'diary.depression.moderate_depression_1.weather_clear'";
        }
    });
    public static final ConditionComponent MODERATE_DEPRESSION_1_EAT = new ConditionComponent(player -> {
        if (Registry.statManager.get(player.getUUID()).hasAte) {
            return "'diary.depression.moderate_depression_1.eat'";
        }
        else {
            return "";
        }
    });
    public static final ConditionComponent MODERATE_DEPRESSION_1_HURT = new ConditionComponent(Stats.DAMAGE_TAKEN, 5, "'diary.depression.moderate_depression_1.hurt'");
    public static final ConditionComponent MODERATE_DEPRESSION_1_MOB_NEARBY = new ConditionComponent(player -> {
        if (player.level().getEntities(player, player.getBoundingBox().inflate(10, 10, 10)).size() >= 3) {
            return "'diary.depression.moderate_depression_1.mob_nearby'";
        }
        else {
            return "";
        }
    });
    public static final ConditionComponent MODERATE_DEPRESSION_2_EAT = new ConditionComponent(player -> {
        if (Registry.statManager.get(player.getUUID()).hasAte) {
            return "'diary.depression.moderate_depression_2.eat'";
        }
        else {
            return "";
        }
    });
    public static final ConditionComponent MODERATE_DEPRESSION_2_HURT = new ConditionComponent(Stats.DAMAGE_TAKEN, 5, "'diary.depression.moderate_depression_2.hurt'");
    public static final ConditionComponent MAJOR_DEPRESSIVE_DISORDER_2_EAT = new ConditionComponent(player -> {
        if (Registry.statManager.get(player.getUUID()).hasAte) {
            return "'diary.depression.major_depressive_disorder_2.eat'";
        }
        else {
            return "";
        }
    });
}
