package net.depression.util;

import net.depression.client.DepressionClient;
import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
    public static int getPlayerMentalHealthLevel(Player player) {
        if (player instanceof ServerPlayer) {
            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
            if (mentalStatus == null) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                mentalStatus = new MentalStatus(serverPlayer);
                Registry.mentalStatus.put(player.getUUID(), mentalStatus);
            }
            return mentalStatus.mentalIllness.mentalHealthLevel;
        }
        else {
            return DepressionClient.clientMentalStatus.mentalHealthLevel;
        }
    }

    public static Date getGameDate(long worldTime) {
        return new Date(worldTime * 3600L - 62167420800000L); //在MC 1 tick 相当于MC过去了 3600 ms，而1年1月1日至1970年1月1日的毫秒数为62167420800000
    }

    public static String textDateFormat(String rawText, long worldTime) { //仅用于重度抑郁日记的年月日格式化
        Date date = getGameDate(worldTime);
        String replacement = new SimpleDateFormat("yy").format(date);
        rawText = rawText.replace("yy", replacement);
        replacement = new SimpleDateFormat("MM").format(date);
        rawText = rawText.replace("MM", replacement);
        replacement = new SimpleDateFormat("dd").format(date);
        rawText = rawText.replace("dd", replacement);
        return rawText;
    }
}
