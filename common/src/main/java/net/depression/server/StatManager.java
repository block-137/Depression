package net.depression.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

public class StatManager { //这个类用来存储玩家的旧的统计数据以及自定义的统计数据
    public int mob_killed;
    public int animal_bred;
    public int distance_moved;
    public int damage_taken;
    public boolean hasAte;

    public boolean getStat(String stat) {
        return switch (stat) {
            case "has_ate" -> hasAte;
            default -> false;
        };
    }

    public int getStat(ResourceLocation stat) { //获取玩家的某项统计数据
        return switch (stat.toString()) {
            case "minecraft:mob_kills" -> mob_killed;
            case "minecraft:animals_bred" -> animal_bred;
            case "minecraft:walk_one_cm" -> distance_moved;
            case "minecraft:damage_taken" -> damage_taken;
            default -> 0;
        };
    }

    public void updateStat(ServerPlayer player) {
        mob_killed = player.getStats().getValue(Stats.CUSTOM, Stats.MOB_KILLS);
        animal_bred = player.getStats().getValue(Stats.CUSTOM, Stats.ANIMALS_BRED);
        distance_moved = player.getStats().getValue(Stats.CUSTOM, Stats.WALK_ONE_CM);
        damage_taken = player.getStats().getValue(Stats.CUSTOM, Stats.DAMAGE_TAKEN);
        hasAte = false;
    }

    public void readNbt(CompoundTag tag) {
        mob_killed = tag.getInt("mob_killed");
        animal_bred = tag.getInt("animal_bred");
        distance_moved = tag.getInt("distance_moved");
        damage_taken = tag.getInt("damage_taken");
        hasAte = tag.getBoolean("has_ate");
    }

    public void writeNbt(CompoundTag tag) {
        tag.putInt("mob_killed", mob_killed);
        tag.putInt("animal_bred", animal_bred);
        tag.putInt("distance_moved", distance_moved);
        tag.putInt("damage_taken", damage_taken);
        tag.putBoolean("has_ate", hasAte);
    }
}
