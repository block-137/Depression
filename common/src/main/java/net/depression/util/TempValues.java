package net.depression.util;

import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.UUID;

public class TempValues {
    public static boolean isCalledByDepressedPlayer = false;
    public static int playerMentalHealthLevel = 0;
    public static Entity broadcastEntity;
    public static boolean isStepSound = false;

    public static List<String> broadcastDamageSource;
    //以下是客户端侧特有的临时值
}
