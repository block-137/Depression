package net.depression.util;

import net.depression.client.DepressionClient;
import net.depression.mental.MentalStatus;
import net.depression.server.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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
}
