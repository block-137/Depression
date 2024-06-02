package net.depression.forge.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import dev.architectury.platform.Platform;
import net.depression.mental.MentalStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerConfig {
    private static Double readDouble(FileConfig config, String key) {
        Double value;
        Object object = config.get(key);
        if (object instanceof Integer) {
            value = ((Integer) object).doubleValue();
        }
        else {
            value = (Double) object;
        }
        return value;
    }

    public static void load() {
        File folder = new File(Platform.getConfigFolder() + "/depression");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //读取server-config.toml
        File file = new File(Platform.getConfigFolder() + "/depression/server-config.toml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfig config = FileConfig.of(file);
        config.load();

        if (!config.contains("emotion_stabilize_rate")) {
            config.set("emotion_stabilize_rate", 0.05);
        }
        if (!config.contains("mental_health_change_rate")) {
            config.set("mental_health_change_rate", 0.01);
        }
        if (!config.contains("ptsd_damage_rate")) {
            config.set("ptsd_damage_rate", 0.5);
        }
        if (!config.contains("ptsd_disperse_rate")) {
            config.set("ptsd_disperse_rate", 0.1);
        }
        if (!config.contains("boredom_decrease_tick")) {
            config.set("boredom_decrease_tick", 200);
        }
        if (!config.contains("food_heal_rate")) {
            config.set("food_heal_rate", 0.25);
        }

        MentalStatus.EMOTION_STABILIZE_RATE = readDouble(config, "emotion_stabilize_rate");
        MentalStatus.MENTAL_HEALTH_CHANGE_RATE = readDouble(config, "mental_health_change_rate");
        MentalStatus.PTSD_DAMAGE_RATE = readDouble(config, "ptsd_damage_rate");
        MentalStatus.PTSD_DISPERSE_RATE = readDouble(config, "ptsd_disperse_rate");
        MentalStatus.BOREDOM_DECREASE_TICK = config.get("boredom_decrease_tick");
        MentalStatus.FOOD_HEAL_RATE = readDouble(config, "food_heal_rate");

        config.save();
        config.close();

        //读取break-block-heal-value.toml
        File blockFile = new File(Platform.getConfigFolder() + "/depression/break-block-heal-value.toml");
        if (!blockFile.exists()) {
            try {
                blockFile.createNewFile();
                FileWriter writer = new FileWriter(blockFile);
                writer.write("""
                        "minecraft:wheat" = 0.1
                        "minecraft:carrot" = 0.1
                        "minecraft:potato" = 0.1
                        "minecraft:beetroots" = 0.1
                        "minecraft:melon" = 0.1
                        "minecraft:pumpkin" = 0.1
                        "minecraft:bamboo" = 0.1
                        "minecraft:sweet_berry_bush" = 0.1
                        "minecraft:cocoa" = 0.1
                        "minecraft:coal_ore" = 0.1
                        "minecraft:iron_ore" = 0.2
                        "minecraft:gold_ore" = 0.3
                        "minecraft:redstone_ore" = 0.2
                        "minecraft:lapis_ore" = 0.2
                        "minecraft:diamond_ore" = 0.5
                        "minecraft:emerald_ore" = 0.4
                        "minecraft:nether_quartz_ore" = 0.1
                        "minecraft:nether_gold_ore" = 0.2
                        "minecraft:deepslate_coal_ore" = 0.1
                        "minecraft:deepslate_iron_ore" = 0.2
                        "minecraft:deepslate_gold_ore" = 0.3
                        "minecraft:deepslate_redstone_ore" = 0.2
                        "minecraft:deepslate_lapis_ore" = 0.2
                        "minecraft:deepslate_diamond_ore" = 0.5
                        "minecraft:deepslate_emerald_ore" = 0.4
                        """);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfig blockConfig = FileConfig.of(blockFile);
        blockConfig.load();


        for (String block : blockConfig.valueMap().keySet()) {
            MentalStatus.breakHealBlock.put(block, readDouble(blockConfig, block));
        }

        //读取kill-entity-heal-value.toml
        File entityFile = new File(Platform.getConfigFolder() + "/depression/kill-entity-heal-value.toml");
        if (!entityFile.exists()) {
            try {
                entityFile.createNewFile();
                FileWriter writer = new FileWriter(entityFile);
                writer.write("""
                        "minecraft:endermite" = 0.125
                        "minecraft:silverfish" = 0.125
                        "minecraft:slime" = 0.125
                        "minecraft:piglin" = 0.25
                        "minecraft:vex" = 0.25
                        "minecraft:zombie" = 0.25
                        "minecraft:zombie_villager" = 0.25
                        "minecraft:magma_cube" = 0.25
                        "minecraft:phantom" = 0.25
                        "minecraft:drowned" = 0.375
                        "minecraft:husk" = 0.375
                        "minecraft:pillager" = 0.375
                        "minecraft:shulker" = 0.375
                        "minecraft:skeleton" = 0.375
                        "minecraft:spider" = 0.375
                        "minecraft:zombified_piglin" = 0.375
                        "minecraft:stray" = 0.5
                        "minecraft:vindicator" = 0.5
                        "minecraft:cave_spider" = 0.5
                        "minecraft:blaze" = 0.5
                        "minecraft:creeper" = 0.5
                        "minecraft:enderman" = 0.625
                        "minecraft:wither_skeleton" = 0.625
                        "minecraft:guardian" = 0.625
                        "minecraft:witch" = 0.75
                        "minecraft:ghast" = 0.75
                        "minecraft:piglin_brute" = 0.875
                        "minecraft:illusioner" = 0.875
                        "minecraft:evoker" = 1.0
                        "minecraft:hoglin" = 1.0
                        "minecraft:zoglin" = 1.0
                        "minecraft:ravager" = 4.0
                        "minecraft:giant" = 5.0
                        "minecraft:elder_guardian" = 20.0
                        "minecraft:warden" = 25.0
                        "minecraft:wither" = 25.0
                        "minecraft:ender_dragon" = 35.0
                        """);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfig entityConfig = FileConfig.of(entityFile);
        entityConfig.load();

        for (String entity : entityConfig.valueMap().keySet()) {
            MentalStatus.killHealEntity.put(entity, readDouble(entityConfig, entity));
        }

        //读取nearby-heal-block.toml
        File nearbyFile = new File(Platform.getConfigFolder() + "/depression/nearby-heal-block.toml");
        if (!nearbyFile.exists()) {
            try {
                nearbyFile.createNewFile();
                FileWriter writer = new FileWriter(nearbyFile);
                writer.write("""
                        # FLOWERS
                                                
                        ["minecraft:dandelion"]
                        value = 0.5
                        radius = 8
                        ["minecraft:poppy"]
                        value = 0.5
                        radius = 8
                        ["minecraft:blue_orchid"]
                        value = 0.625
                        radius = 8
                        ["minecraft:allium"]
                        value = 0.625
                        radius = 8
                        ["minecraft:azure_bluet"]
                        value = 0.5
                        radius = 8
                        ["minecraft:red_tulip"]
                        value = 0.625
                        radius = 8
                        ["minecraft:orange_tulip"]
                        value = 0.625
                        radius = 8
                        ["minecraft:white_tulip"]
                        value = 0.625
                        radius = 8
                        ["minecraft:pink_tulip"]
                        value = 0.625
                        radius = 8
                        ["minecraft:oxeye_daisy"]
                        value = 0.5
                        radius = 8
                        ["minecraft:cornflower"]
                        value = 0.5
                        radius = 8
                        ["minecraft:lily_of_the_valley"]
                        value = 0.5
                        radius = 8
                        ["minecraft:wither_rose"]
                        value = 0
                        radius = 8
                        ["minecraft:torchflower"]
                        value = 1
                        radius = 8
                        ["minecraft:sunflower"]
                        value = 0.625
                        radius = 8
                        ["minecraft:lilac"]
                        value = 0.625
                        radius = 8
                        ["minecraft:rose_bush"]
                        value = 0.625
                        radius = 8
                        ["minecraft:peony"]
                        value = 0.625
                        radius = 8
                        ["minecraft:pitcher_plant"]
                        value = 1
                        radius = 8
                                                
                        # LEAVES
                                                
                        ["minecraft:oak_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:spruce_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:birch_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:jungle_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:acacia_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:dark_oak_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:azalea_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:flowering_azalea_leaves"]
                        value = 0.5
                        radius = 8
                        ["minecraft:mangrove_leaves"]
                        value = 0.25
                        radius = 8
                        ["minecraft:cherry_leaves"]
                        value = 0.375
                        radius = 8
                                                
                        # OTHER_PLANTS
                                                
                        ["minecraft:spore_blossom"]
                        value = 0.75
                        radius = 12
                        ["minecraft:azalea"]
                        value = 0.5
                        radius = 12
                        ["minecraft:flowering_azalea"]
                        value = 0.75
                        radius = 12
                        ["minecraft:pink_petals"]
                        value = 0.75
                        radius = 6
                        ["minecraft:sea_pickle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:lily_pad"]
                        value = 0.125
                        radius = 6
                        ["minecraft:waterlily"]
                        value = 0.125
                        radius = 6
                        ["minecraft:carved_pumpkin"]
                        value = 0.125
                        radius = 6
                        ["minecraft:shroomlight"]
                        value = 0.375
                        radius = 8
                                                
                        # UTILITIES
                                                
                        ["minecraft:beacon"]
                        value = 1.5
                        radius = 12
                        ["minecraft:conduit"]
                        value = 1.25
                        radius = 12
                        ["minecraft:respawn_anchor"]
                        value = 0.375
                        radius = 12
                        ["minecraft:enchanting_table"]
                        value = 0.375
                        radius = 8
                                                
                        # CANDLES
                                                
                        ["minecraft:candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:white_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:orange_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:magenta_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:light_blue_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:yellow_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:lime_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:pink_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:gray_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:light_gray_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:cyan_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:purple_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:blue_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:brown_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:green_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:red_candle"]
                        value = 0.375
                        radius = 6
                        ["minecraft:black_candle"]
                        value = 0.375
                        radius = 6
                                                
                        # FIRELIGHTS
                                                
                        ["minecraft:campfire"]
                        value = 0.5
                        radius = 12
                        ["minecraft:soul_campfire"]
                        value = 0.5
                        radius = 12
                        ["minecraft:torch"]
                        value = 0.125
                        radius = 6
                        ["minecraft:soul_torch"]
                        value = 0.125
                        radius = 6
                        ["minecraft:jack_o_lantern"]
                        value = 0.25
                        radius = 8
                        ["minecraft:lit_pumpkin"]
                        value = 0.25
                        radius = 8
                        ["minecraft:lantern"]
                        value = 0.25
                        radius = 8
                        ["minecraft:soul_lantern"]
                        value = 0.25
                        radius = 8
                                                
                        # COPPER_BULBS
                                                
                        ["minecraft:copper_bulb"]
                        value = 0.375
                        radius = 8
                        ["minecraft:exposed_copper_bulb"]
                        value = 0.375
                        radius = 8
                        ["minecraft:weathered_copper_bulb"]
                        value = 0.375
                        radius = 8
                        ["minecraft:oxidized_copper_bulb"]
                        value = 0.375
                        radius = 8
                        ["minecraft:waxed_copper_bulb"]
                        value = 0.375
                        radius = 8
                        ["minecraft:waxed_exposed_copper_bulb"]
                        value = 0.375
                        radius = 8
                        ["minecraft:waxed_weathered_copper_bulb"]
                        value = 0.375
                        radius = 8
                        ["minecraft:waxed_oxidized_copper_bulb"]
                        value = 0.375
                        radius = 8
                                                
                        # OTHER_GLOWING_BLOCKS
                                                
                        ["minecraft:end_rod"]
                        value = 0.125
                        radius = 6
                        ["minecraft:glowstone"]
                        value = 0.25
                        radius = 8
                        ["minecraft:redstone_lamp"]
                        value = 0.25
                        radius = 8
                        ["minecraft:sea_lantern"]
                        value = 0.375
                        radius = 8
                        ["minecraft:pearlescent_froglight"]
                        value = 0.5
                        radius = 8
                        ["minecraft:verdant_froglight"]
                        value = 0.5
                        radius = 8
                        ["minecraft:ochre_froglight"]
                        value = 0.5
                        radius = 8
                                                
                        # DECORATIONS
                                                
                        ["minecraft:decorated_pot"]
                        value = 0.125
                        radius = 4
                        ["minecraft:painting"]
                        value = 0.25
                        radius = 6
                        ["minecraft:item_frame"]
                        value = 0.125
                        radius = 4
                        """);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfig nearbyConfig = FileConfig.of(nearbyFile);
        nearbyConfig.load();

        for (String block : nearbyConfig.valueMap().keySet()) {
            int radius = nearbyConfig.get(block + ".radius");
            MentalStatus.radiusMaxValue = Math.max(MentalStatus.radiusMaxValue, radius);
            MentalStatus.nearbyHealBlockValue.put(block, readDouble(nearbyConfig, block + ".value"));
            MentalStatus.nearbyHealBlockRadius.put(block, radius);
        }

        //读取smelt-item-heal-value.toml
        File smeltFile = new File(Platform.getConfigFolder() + "/depression/smelt-item-heal-value.toml");
        if (!smeltFile.exists()) {
            try {
                smeltFile.createNewFile();
                FileWriter writer = new FileWriter(smeltFile);
                writer.write("""
                        "minecraft:iron_ingot" = 0.25
                        "minecraft:gold_ingot" = 0.5
                        "minecraft:netherite_scrap" = 1.0
                        "minecraft:cooked_porkchop" = 0.25
                        "minecraft:cooked_beef" = 0.25
                        "minecraft:cooked_chicken" = 0.25
                        "minecraft:cooked_rabbit" = 0.25
                        "minecraft:cooked_mutton" = 0.25
                        "minecraft:baked_potato" = 0.25
                        "minecraft:cooked_cod" = 0.25
                        "minecraft:cooked_salmon" = 0.25
                        "minecraft:cooked_pufferfish" = 0.25
                        "minecraft:cooked_tropical_fish" = 0.25
                        "minecraft:cooked_kelp" = 0.25
                        """);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfig smeltConfig = FileConfig.of(smeltFile);
        smeltConfig.load();

        for (String item : smeltConfig.valueMap().keySet()) {
            MentalStatus.smeltHealItem.put(item, readDouble(smeltConfig, item));
        }
    }
}
