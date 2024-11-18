package net.depression.mental;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MentalTrait {
    public static LinkedHashMap<String, MentalTrait> mentalTraits = new LinkedHashMap<>(); //精神特质
    public String id;
    public double killMobMultiplier = 1.0;
    public double miningMultiplier = 1.0;
    public double farmingMultiplier = 1.0;
    public double mentalHurtMultiplier = 1.0;
    public double medicineEffectMultiplier = 1.0;
    public double bipolarChanceMultiplier = 1.0;
    public double fatigueChanceMultiplier = 1.0;
    public double killAnimalHealValue = 0;
    public int initialMentalHealthValue = 100;
    public int initialMentalHealthId = 0;
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

}
