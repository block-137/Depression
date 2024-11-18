package net.depression.mental;

import java.util.LinkedHashMap;
import java.util.Random;

public class MentalIllnessPool {
    private final Random random = new Random();
    private final LinkedHashMap<Integer, Double> illnessPool = new LinkedHashMap<>();
    private double totalWeight = 0;
    public void addIllness(int id, double weight) {
        illnessPool.put(id, weight);
        totalWeight += weight;
    }
    public int getIllness() {
        double randomValue = random.nextDouble() * totalWeight;
        for (int id : illnessPool.keySet()) {
            randomValue -= illnessPool.get(id);
            if (randomValue <= 0) {
                return id;
            }
        }
        return 0;
    }
    public int getIllness(int multiplyId, double multiplier) {
        double randomValue = random.nextDouble() * totalWeight;
        double multiplyIdWeight = illnessPool.get(multiplyId);
        double increasedWeight = multiplyIdWeight * multiplier - multiplyIdWeight;
        double leftTotalWeight = totalWeight - multiplyIdWeight;
        for (int id : illnessPool.keySet()) {
            double weight = illnessPool.get(id);
            randomValue -= id == multiplyId ? multiplyIdWeight * multiplier : weight - (increasedWeight * weight / leftTotalWeight);
            if (randomValue <= 0) {
                return id;
            }
        }
        return 0;
    }
}
