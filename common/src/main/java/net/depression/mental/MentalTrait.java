package net.depression.mental;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class MentalTrait {
    public static LinkedHashMap<String, MentalTrait> mentalTraits = new LinkedHashMap<>(); //精神特质
    public static Random random = new Random();
    public String id;
    public double killMobMultiplier = 1.0;
    public double miningMultiplier = 1.0;
    public double farmingMultiplier = 1.0;
    public double mentalHurtMultiplier = 1.0;
    public double medicineEffectMultiplier = 1.0;
    public double bipolarChanceMultiplier = 1.0;
    public double fatigueChanceMultiplier = 1.0;
    public double killAnimalHealValue = 0;
    public Integer initialMentalHealthValue;
    public Integer initialMentalHealthId;
    public boolean isDarknessAffectEmotion = true;
    public boolean isBadEmotionLowerCombat = true;
    public boolean isGoodEmotionHigherCombat = true;

    public MentalTrait(String id) {
        this.id = id;
        mentalTraits.put(id, this);
    }

    public static MentalTrait byId(String id) {
        return mentalTraits.get(id);
    }

    public static MentalTrait getRandomTrait() {
        int index = (int) (random.nextDouble() * mentalTraits.size());
        for (MentalTrait trait : mentalTraits.values()) {
            if (index == 0) {
                return trait;
            }
            index--;
        }
        return mentalTraits.values().iterator().next();
    }
}
