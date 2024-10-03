package net.depression.item.diary;

import net.depression.server.Registry;
import net.depression.server.StatManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

import java.util.function.Function;

public class ConditionComponent {
    private final Function<ServerPlayer, String> function;
    public ConditionComponent(Function<ServerPlayer, String> component) {
        this.function = component;
    }

    public ConditionComponent(ResourceLocation stat, int value, String string) {
        function = player -> {
            int statsNum = player.getStats().getValue(Stats.CUSTOM, stat);
            StatManager statManager = Registry.statManager.get(player.getUUID());
            statsNum -= statManager.getStat(stat); //减去昨天的旧统计数据
            if (statsNum >= value) {
                return string;
            }
            else {
                return "";
            }
        };
    }

    public ConditionComponent(ResourceLocation stat, int value, String string, boolean reverse) {
        if (reverse) {
            function = player -> {
                int statsNum = player.getStats().getValue(Stats.CUSTOM, stat);
                StatManager statManager = Registry.statManager.get(player.getUUID());
                statsNum -= statManager.getStat(stat);
                if (statsNum <= value) {
                    return string;
                }
                else {
                    return "";
                }
            };
        }
        else {
            function = player -> {
                int statsNum = player.getStats().getValue(Stats.CUSTOM, stat);
                StatManager statManager = Registry.statManager.get(player.getUUID());
                statsNum -= statManager.getStat(stat);
                if (statsNum >= value) {
                    return string;
                }
                else {
                    return "";
                }
            };
        }
    }

    public String get(ServerPlayer player) {
        return function.apply(player);
    }
}
