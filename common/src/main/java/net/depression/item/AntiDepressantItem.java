package net.depression.item;

import net.depression.effect.ModEffects;

public class AntiDepressantItem extends MedicineItem {
    public AntiDepressantItem(int level) {
        super(ModEffects.ANTI_DEPRESSION.get(), 24000, level);
    }
}
