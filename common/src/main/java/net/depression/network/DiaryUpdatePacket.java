package net.depression.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.depression.Depression;
import net.depression.item.diary.ConditionComponents;
import net.depression.server.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DiaryUpdatePacket {
    public static final ResourceLocation DIARY_UPDATE_PACKET = new ResourceLocation(Depression.MOD_ID, "diary_update_packet");
    public static Charset charset = StandardCharsets.UTF_8;

    public static void sendToServer(String content) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeCharSequence(content, charset);
        NetworkManager.sendToServer(DIARY_UPDATE_PACKET, buf);
    }

    public static void sendToPlayer(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        double mentalHealthValue = Registry.mentalStatus.get(player.getUUID()).mentalHealthValue;
        String content = "";
        if (85 <= mentalHealthValue && mentalHealthValue <= 100) { //健康1
            content = "　　'diary.depression.healthy_1.1'  　　"
                    + ConditionComponents.HEALTHY_1_WEATHER.get(player)
                    + ConditionComponents.HEALTHY_1_BREED.get(player)
                    + ConditionComponents.HEALTHY_1_EAT.get(player)
                    + "  　　'diary.depression.healthy_1.2'"
                    +ConditionComponents.HEALTHY_1_KILL_MOBS.get(player);
        }
        else if (70 <= mentalHealthValue && mentalHealthValue < 85) { //健康2
            content = "　　'diary.depression.healthy_2.1'  　　"
                    + ConditionComponents.HEALTHY_2_BREED.get(player)
                    + ConditionComponents.HEALTHY_2_WEATHER.get(player)
                    + ConditionComponents.HEALTHY_2_MOVE_IN_WEATHER.get(player)
                    + "  　　'diary.depression.healthy_2.2'";
        }
        else if (55 <= mentalHealthValue && mentalHealthValue < 70) { //轻度抑郁1
            content = "　　'diary.depression.mild_depression_1.1'  　　"
                    + ConditionComponents.MILD_DEPRESSION_1_WEATHER.get(player)
                    + ConditionComponents.MILD_DEPRESSION_1_MOVE_IN_WEATHER.get(player)
                    + "  　　'diary.depression.mild_depression_1.2'"
                    + ConditionComponents.MILD_DEPRESSION_1_MOVE.get(player);
        }
        else if (40 <= mentalHealthValue && mentalHealthValue < 55) { //轻度抑郁2
            content = "　　'diary.depression.mild_depression_2.1'  　　"
                    + ConditionComponents.MILD_DEPRESSION_2_WEATHER.get(player)
                    + ConditionComponents.MILD_DEPRESSION_2_EAT.get(player)
                    + "  　　'diary.depression.mild_depression_2.2'"
                    + ConditionComponents.MILD_DEPRESSION_2_HURT.get(player);
        }
        else if (30 <= mentalHealthValue && mentalHealthValue < 40) { //中度抑郁1
            content = "　　'diary.depression.moderate_depression_1.1'  　　"
                    + ConditionComponents.MODERATE_DEPRESSION_1_KILL_ENTITIES.get(player)
                    + ConditionComponents.MODERATE_DEPRESSION_1_WEATHER.get(player)
                    + ConditionComponents.MODERATE_DEPRESSION_1_EAT.get(player)
                    + ConditionComponents.MODERATE_DEPRESSION_1_HURT.get(player)
                    + "  　　'diary.depression.moderate_depression_1.2'"
                    + ConditionComponents.MODERATE_DEPRESSION_1_MOB_NEARBY.get(player);
        }
        else if (20 <= mentalHealthValue && mentalHealthValue < 30) { //中度抑郁2
            content = "　　'diary.depression.moderate_depression_2.1'  "
                    + "　　'diary.depression.moderate_depression_2.2'"
                    + ConditionComponents.MODERATE_DEPRESSION_2_EAT
                    + "  　　'diary.depression.moderate_depression_2.3'"
                    + ConditionComponents.MODERATE_DEPRESSION_2_HURT;
        }
        else if (10 <= mentalHealthValue && mentalHealthValue < 20) { //重度抑郁1
            content = "　　'diary.depression.major_depressive_disorder_1.1'";
        }
        else if (0 <= mentalHealthValue && mentalHealthValue < 10) { //重度抑郁2
            content = "　　'diary.depression.major_depressive_disorder_2.1'"
                    + ConditionComponents.MAJOR_DEPRESSIVE_DISORDER_2_EAT.get(player)
                    + "  　　'diary.depression.major_depressive_disorder_2.2'";
        }
        buf.writeCharSequence(content, charset);
        NetworkManager.sendToPlayer(player, DIARY_UPDATE_PACKET, buf);
    }
}
