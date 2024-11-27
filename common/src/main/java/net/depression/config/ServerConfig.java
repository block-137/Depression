package net.depression.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import dev.architectury.platform.Platform;
import net.depression.Depression;
import net.depression.mental.MentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.mental.PTSDManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        Config.setInsertionOrderPreserved(true);
        File folder = new File(Platform.getConfigFolder() + "/depression");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        boolean overwrite = false;
        //读取server-config.toml
        File file = new File(Platform.getConfigFolder() + "/depression/server-config.toml");
        if (!file.exists()) {
            try {
                overwrite = true;
                file.createNewFile();
                FileWriter writer = writeServerFile(file);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfig config = FileConfig.of(file);
        config.load();
        if (config.contains("version") || config.get("version") instanceof String) {
            String fileVersion = config.get("version");
            if (!Depression.MOD_VERSION.equals(fileVersion)) {
                overwrite = true;
            }
        }
        else {
            overwrite = true;
        }

        if (overwrite) {
            try {
                FileWriter writer = writeServerFile(file);
                writer.close();
                config = FileConfig.of(file);
                config.load();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        MentalStatus.EMOTION_STABILIZE_RATE = readDouble(config, "emotion_stabilize_rate");
        MentalStatus.MENTAL_HEALTH_CHANGE_RATE = readDouble(config, "mental_health_change_rate");
        MentalStatus.PTSD_DAMAGE_RATE = readDouble(config, "ptsd_damage_rate");
        MentalStatus.PTSD_DISPERSE_RATE = readDouble(config, "ptsd_disperse_rate");
        MentalStatus.BOREDOM_DECREASE_TICK = config.get("boredom_decrease_tick");
        MentalStatus.FOOD_HEAL_RATE = readDouble(config, "food_heal_rate");
        PTSDManager.KILL_PTSD_DECREASE = readDouble(config, "kill_ptsd_decrease");
        PTSDManager.ONSET_EMOTION_DECREASE = readDouble(config, "onset_emotion_decrease");
        if (config.contains("default_mental_trait")) {
            MentalStatus.DEFAULT_MENTAL_TRAIT = config.get("default_mental_trait");
        }
        MentalStatus.IS_RANDOM_CHOOSE_TRAIT = config.get("random_choose_mental_trait");

        config.close();

        //读取break-block-heal-value.toml
        File blockFile = new File(Platform.getConfigFolder() + "/depression/break-block-heal-value.toml");
        if (!blockFile.exists()) {
            try {
                blockFile.createNewFile();
                FileWriter writer = writeBlockFile(blockFile);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeBlockFile(blockFile);
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

        //读取advancement-heal-value.toml
        File advancementFile = new File(Platform.getConfigFolder() + "/depression/advancement-heal-value.toml");
        if (!advancementFile.exists()) {
            try {
                advancementFile.createNewFile();
                FileWriter writer = writeAdvancementFile(advancementFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeAdvancementFile(advancementFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfig advancementConfig = FileConfig.of(advancementFile);
        advancementConfig.load();


        for (String advancement : advancementConfig.valueMap().keySet()) {
            MentalStatus.healAdvancement.put(advancement, readDouble(advancementConfig, advancement));
        }

        //读取kill-entity-heal-value.toml
        File entityFile = new File(Platform.getConfigFolder() + "/depression/kill-entity-heal-value.toml");
        if (!entityFile.exists()) {
            try {
                entityFile.createNewFile();
                FileWriter writer = writeEntityFile(entityFile);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeEntityFile(entityFile);
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
                FileWriter writer = writeNearbyFile(nearbyFile);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeNearbyFile(nearbyFile);
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfig nearbyConfig = FileConfig.of(nearbyFile);
        nearbyConfig.load();

        for (String key1 : nearbyConfig.valueMap().keySet()) {
            if (nearbyConfig.contains(key1 + ".value") && nearbyConfig.contains(key1 + ".radius")) {
                int radius = nearbyConfig.get(key1 + ".radius");
                MentalStatus.radiusMaxValue = Math.max(MentalStatus.radiusMaxValue, radius);
                MentalStatus.nearbyHealBlockValue.put(key1, readDouble(nearbyConfig, key1 + ".value"));
                MentalStatus.nearbyHealBlockRadius.put(key1, radius);
            }
            else {
                Object map = nearbyConfig.get(key1);
                if (map instanceof CommentedConfig) {
                    for (String key2 : ((CommentedConfig) map).valueMap().keySet()) {
                        if (key2 instanceof String) {
                            int radius = nearbyConfig.get(key1 + "." + key2 + ".radius");
                            MentalStatus.radiusMaxValue = Math.max(MentalStatus.radiusMaxValue, radius);
                            MentalStatus.nearbyHealBlockValue.put(key2, readDouble(nearbyConfig, key1 + "." + key2 + ".value"));
                            MentalStatus.nearbyHealBlockRadius.put(key2, radius);
                            MentalStatus.nearbyHealBlockType.put(key2, key1);
                        }
                    }
                }
            }
        }

        //读取smelt-item-heal-value.toml
        File smeltFile = new File(Platform.getConfigFolder() + "/depression/smelt-item-heal-value.toml");
        if (!smeltFile.exists()) {
            try {
                smeltFile.createNewFile();
                FileWriter writer = writeSmeltFile(smeltFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeSmeltFile(smeltFile);
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

        //读取damagesource-sound-map.toml
        File damageFile = new File(Platform.getConfigFolder() + "/depression/damagesource-sound-map.toml");
        if (!damageFile.exists()) {
            try {
                damageFile.createNewFile();
                FileWriter writer = writeDamageFile(damageFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeDamageFile(damageFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfig damageConfig = FileConfig.of(damageFile);
        damageConfig.load();

        for (Map.Entry<String, Object> entry : damageConfig.valueMap().entrySet()) {
            String damageSource = entry.getKey();
            Object object = entry.getValue();
            if (object instanceof List) {
                List<String> soundEvents = (List<String>) object;
                for (String soundEvent : soundEvents) {
                    PTSDManager.addEntry(damageSource, soundEvent);
                }
            }
            else {
                String soundEvent = (String) object;
                PTSDManager.addEntry(damageSource, soundEvent);
            }
        }

        //读取loot-item-heal-value.toml
        File lootFile = new File(Platform.getConfigFolder() + "/depression/loot-item-heal-value.toml");
        if (!lootFile.exists()) {
            try {
                lootFile.createNewFile();
                FileWriter writer = writeLootFile(lootFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeLootFile(lootFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfig lootConfig = FileConfig.of(lootFile);
        lootConfig.load();

        for (String id : lootConfig.valueMap().keySet()) {
            MentalStatus.lootHealItem.put(id, readDouble(lootConfig, id));
        }

        //读取food-heal-value.toml
        File foodFile = new File(Platform.getConfigFolder() + "/depression/eat-food-heal-value.toml");
        if (!foodFile.exists()) {
            try {
                foodFile.createNewFile();
                FileWriter writer = writeFoodFile(foodFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeFoodFile(foodFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfig foodConfig = FileConfig.of(foodFile);
        foodConfig.load();

        for (String id : foodConfig.valueMap().keySet()) {
            MentalStatus.foodHealValue.put(id, readDouble(foodConfig, id));
        }

        //读取mental-traits.toml
        File traitFile = new File(Platform.getConfigFolder() + "/depression/mental-traits.toml");
        if (!traitFile.exists()) {
            try {
                traitFile.createNewFile();
                FileWriter writer = writeTraitFile(traitFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (overwrite) {
            try {
                FileWriter writer = writeTraitFile(traitFile);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileConfig traitConfig = FileConfig.of(traitFile);
        traitConfig.load();

        for (String trait : traitConfig.valueMap().keySet()) {
            MentalTrait mentalTrait = new MentalTrait(trait);
            if (traitConfig.contains(trait + ".kill_mob_multiplier")) {
                mentalTrait.killMobMultiplier = readDouble(traitConfig, trait + ".kill_mob_multiplier");
            }
            if (traitConfig.contains(trait + ".mining_multiplier")) {
                mentalTrait.miningMultiplier = readDouble(traitConfig, trait + ".mining_multiplier");
            }
            if (traitConfig.contains(trait + ".farming_multiplier")) {
                mentalTrait.farmingMultiplier = readDouble(traitConfig, trait + ".farming_multiplier");
            }
            if (traitConfig.contains(trait + ".kill_animal_heal_value")) {
                mentalTrait.killAnimalHealValue = readDouble(traitConfig, trait + ".kill_animal_heal_value");
            }
            if (traitConfig.contains(trait + ".mental_hurt_multiplier")) {
                mentalTrait.mentalHurtMultiplier = readDouble(traitConfig, trait + ".mental_hurt_multiplier");
            }
            if (traitConfig.contains(trait + ".medicine_effect_multiplier")) {
                mentalTrait.medicineEffectMultiplier = readDouble(traitConfig, trait + ".medicine_effect_multiplier");
            }
            if (traitConfig.contains(trait + ".bipolar_chance_multiplier")) {
                mentalTrait.bipolarChanceMultiplier = readDouble(traitConfig, trait + ".bipolar_chance_multiplier");
            }
            if (traitConfig.contains(trait + ".fatigue_chance_multiplier")) {
                mentalTrait.fatigueChanceMultiplier = readDouble(traitConfig, trait + ".fatigue_chance_multiplier");
            }
            if (traitConfig.contains(trait + ".initial_mental_health_value")) {
                mentalTrait.initialMentalHealthValue = traitConfig.get(trait + ".initial_mental_health_value");
            }
            if (traitConfig.contains(trait + ".initial_mental_health_id")) {
                mentalTrait.initialMentalHealthId = traitConfig.get(trait + ".initial_mental_health_id");
            }
            if (traitConfig.contains(trait + ".darkness_affect_emotion")) {
                mentalTrait.isDarknessAffectEmotion = traitConfig.get(trait + ".darkness_affect_emotion");
            }
            if (traitConfig.contains(trait + ".bad_emotion_lower_combat")) {
                mentalTrait.isBadEmotionLowerCombat = traitConfig.get(trait + ".bad_emotion_lower_combat");
            }
            if (traitConfig.contains(trait + ".good_emotion_higher_combat")) {
                mentalTrait.isGoodEmotionHigherCombat = traitConfig.get(trait + ".good_emotion_higher_combat");
            }
        }
    }

    @NotNull
    private static FileWriter writeTraitFile(File traitFile) throws IOException {
        FileWriter writer = new FileWriter(traitFile);
        writer.write("""
                [normal]
                                
                [warrior]
                kill_mob_multiplier = 2
                bad_emotion_lower_combat = false
                bipolar_chance_multiplier = 3
                                
                [miner]
                mining_multiplier = 2
                darkness_affect_emotion = false
                fatigue_chance_multiplier = 3
                                
                [farmer]
                farming_multiplier = 2
                kill_animal_heal_value = 0.5
                good_emotion_higher_combat = false
                                
                [sickly]
                mental_hurt_multiplier = 2
                medicine_effect_multiplier = 3
                                
                [mdd_patient]
                initial_mental_health_value = 0
                initial_mental_health_id = 3
                                
                [bd_patient]
                initial_mental_health_value = 0
                initial_mental_health_id = 4
                """);
        return writer;
    }

    @NotNull
    private static FileWriter writeServerFile(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("""
                # Mod will overwrite all the configurations if the version isn't match with the current mod version.
                # If you want to keep your changes while updating, please change the version to the updated mod version.
                version = "0.1.5.1+1.19.2"
                emotion_stabilize_rate = 0.1
                mental_health_change_rate = 0.01
                ptsd_damage_rate = 0.25
                ptsd_disperse_rate = 0.001
                boredom_decrease_tick = 100
                food_heal_rate = 0.25
                kill_ptsd_decrease = 1
                onset_emotion_decrease = 0.05
                random_choose_mental_trait = false
                # default_mental_trait = "normal"
                """);
        return writer;
    }
    @NotNull
    private static FileWriter writeFoodFile(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("""
                "minecraft:golden_carrot" = 3.06
                "minecraft:enchanted_golden_apple" = 20
                "minecraft:golden_apple" = 6.12
                "minecraft:cooked_beef" = 3.33
                "minecraft:cooked_porkchop" = 3.33
                "minecraft:cooked_mutton" = 2.5
                "minecraft:cooked_salmon" = 2.5
                "minecraft:spider_eye" = -2.08
                "minecraft:rabbit_stew" = 4.95
                "minecraft:beetroot_soup" = 2.97
                "minecraft:mushroom_stew" = 2.97
                "minecraft:suspicious_stew" = 2.97
                "minecraft:cooked_chicken" = 1.59
                "minecraft:baked_potato" = 1.32
                "minecraft:bread" = 1.32
                "minecraft:cooked_rabbit" = 1.32
                "minecraft:cooked_cod" = 1.32
                "minecraft:carrot" = 0.79
                "minecraft:beetroot" = 0.265
                "minecraft:pumpkin_pie" = 2.88
                "minecraft:apple" = 0.96
                "minecraft:chorus_fruit" = 0.96
                "minecraft:beef" = 0.18
                "minecraft:porkchop" = 0.0
                "minecraft:rabbit" = 0.0
                "minecraft:melon_slice" = 0.48
                "minecraft:mutton" = 0.0
                "minecraft:chicken" = 0.0
                "minecraft:poisonous_potato" = -1.92
                "minecraft:dried_kelp" = 0.18
                "minecraft:potato" = 0.0
                "minecraft:honey_bottle" = 0.72
                "minecraft:rotten_flesh" = -1.92
                "minecraft:cake" = 0.96
                "minecraft:cookie" = 0.96
                "minecraft:cod" = 0.06
                "minecraft:salmon" = 0.06
                "minecraft:sweet_berries" = 0.48
                "minecraft:glow_berries" = 0.48
                "minecraft:tropical_fish" = 0.03
                "minecraft:pufferfish" = -3.0
                """);
        return writer;
    }
    @NotNull
    private static FileWriter writeLootFile(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("""
                # Fishes
                "minecraft:cod" = 0.3
                "minecraft:salmon" = 0.4
                "minecraft:tropical_fish" = 0.5
                "minecraft:pufferfish" = 0.4
                "minecraft:cooked_cod" = 0.3
                "minecraft:cooked_salmon" = 0.4
                                
                # Buckets
                "minecraft:bucket" = 2.0
                "minecraft:water_bucket" = 2.0
                "minecraft:lava_bucket" = 2.2
                "minecraft:powder_snow_bucket" = 2.0
                "minecraft:milk_bucket" = 2.0
                "minecraft:cod_bucket" = 2.3
                "minecraft:salmon_bucket" = 2.4
                "minecraft:pufferfish_bucket" = 2.4
                "minecraft:tropical_fish_bucket" = 2.5
                "minecraft:axolotl_bucket" = 4.5
                "minecraft:tadpole_bucket" = 3.5
                                
                # Misc
                "minecraft:tripwire_hook" = 0.1
                "minecraft:lead" = 0.2
                "minecraft:lily_pad" = 0.3
                "minecraft:echo_shard" = 0.3
                "minecraft:sculk_sensor" = 0.5
                "minecraft:sculk_catalyst" = 0.8
                "minecraft:experience_bottle" = 0.5
                "minecraft:name_tag" = 1.5
                "minecraft:nautilus_shell" = 1.5
                "minecraft:lodestone" = 2.5
                "minecraft:heart_of_the_sea" = 3.0
                "minecraft:saddle" = 3.0
                "minecraft:sniffer_egg" = 3.0
                "minecraft:ice" = 0.2
                "minecraft:packed_ice" = 0.4
                "minecraft:blue_ice" = 0.8
                "minecraft:obsidian" = 0.3
                "minecraft:crying_obsidian" = 0.6
                                
                # Mob Drops
                "minecraft:gunpowder" = 0.1
                "minecraft:slime_ball" = 0.2
                "minecraft:leather" = 1.0
                "minecraft:book" = 1.0
                "minecraft:blaze_powder" = 0.3
                "minecraft:blaze_rod" = 0.5
                "minecraft:magma_cream" = 0.5
                "minecraft:phantom_membrane" = 0.5
                "minecraft:ender_pearl" = 0.4
                "minecraft:ender_eye" = 0.6
                "minecraft:prismarine_shard" = 0.05
                "minecraft:prismarine_crystals" = 0.1
                "minecraft:sea_lantern" = 0.5
                "minecraft:fire_charge" = 0.3
                "minecraft:tnt" = 0.5
                                
                # Pottery Sherds
                "minecraft:angler_pottery_sherd" = 0.2
                "minecraft:archer_pottery_sherd" = 0.2
                "minecraft:arms_up_pottery_sherd" = 0.2
                "minecraft:blade_pottery_sherd" = 0.2
                "minecraft:brewer_pottery_sherd" = 0.2
                "minecraft:burn_pottery_sherd" = 0.2
                "minecraft:danger_pottery_sherd" = 0.2
                "minecraft:explorer_pottery_sherd" = 0.2
                "minecraft:friend_pottery_sherd" = 0.2
                "minecraft:heart_pottery_sherd" = 0.2
                "minecraft:heartbreak_pottery_sherd" = 0.2
                "minecraft:howl_pottery_sherd" = 0.2
                "minecraft:miner_pottery_sherd" = 0.2
                "minecraft:mourner_pottery_sherd" = 0.2
                "minecraft:plenty_pottery_sherd" = 0.2
                "minecraft:prize_pottery_sherd" = 0.2
                "minecraft:sheaf_pottery_sherd" = 0.2
                "minecraft:shelter_pottery_sherd" = 0.2
                "minecraft:skull_pottery_sherd" = 0.2
                "minecraft:snort_pottery_sherd" = 0.2
                "minecraft:flow_pottery_sherd" = 0.2
                "minecraft:guster_pottery_sherd" = 0.2
                "minecraft:scrape_pottery_sherd" = 0.2
                                
                # Smithing Templates
                "minecraft:netherite_upgrade_smithing_template" = 4.0
                "minecraft:coast_armor_trim_smithing_template" = 2.0
                "minecraft:dune_armor_trim_smithing_template" = 2.0
                "minecraft:eye_armor_trim_smithing_template" = 2.0
                "minecraft:host_armor_trim_smithing_template" = 2.0
                "minecraft:raiser_armor_trim_smithing_template" = 2.0
                "minecraft:rib_armor_trim_smithing_template" = 2.0
                "minecraft:sentry_armor_trim_smithing_template" = 2.0
                "minecraft:shaper_armor_trim_smithing_template" = 2.0
                "minecraft:silence_armor_trim_smithing_template" = 2.0
                "minecraft:snout_armor_trim_smithing_template" = 2.0
                "minecraft:spire_armor_trim_smithing_template" = 2.0
                "minecraft:tide_armor_trim_smithing_template" = 2.0
                "minecraft:vex_armor_trim_smithing_template" = 2.0
                "minecraft:ward_armor_trim_smithing_template" = 2.0
                "minecraft:wayfinder_armor_trim_smithing_template" = 2.0
                "minecraft:wild_armor_trim_smithing_template" = 2.0
                "minecraft:bolt_armor_trim_smithing_template" = 2.0
                "minecraft:flow_armor_trim_smithing_template" = 2.0
                                
                # Music Discs
                "minecraft:disc_fragment_5" = 0.518
                "minecraft:music_disc_13" = 2.0
                "minecraft:music_disc_cat" = 2.0
                "minecraft:music_disc_blocks" = 2.0
                "minecraft:music_disc_chirp" = 2.0
                "minecraft:music_disc_far" = 2.0
                "minecraft:music_disc_mall" = 2.0
                "minecraft:music_disc_mellohi" = 2.0
                "minecraft:music_disc_stal" = 2.0
                "minecraft:music_disc_strad" = 2.0
                "minecraft:music_disc_ward" = 2.0
                "minecraft:music_disc_11" = 2.0
                "minecraft:music_disc_wait" = 2.0
                "minecraft:music_disc_pigstep" = 2.0
                "minecraft:music_disc_otherside" = 2.0
                "minecraft:music_disc_5" = 2.0
                "minecraft:music_disc_relic" = 2.0
                "minecraft:music_disc_precipice" = 2.0
                "minecraft:music_disc_creator" = 2.0
                "minecraft:music_disc_creator_music_box" = 2.0
                                
                # Ores and its Related\s
                # (excluding iron, gold, netherite ingots and diamonds)
                "minecraft:coal" = 0.1
                "minecraft:redstone" = 0.1
                "minecraft:lapis_lazuli" = 0.2
                "minecraft:iron_nugget" = 0.2
                "minecraft:gold_nugget" = 0.3
                "minecraft:gold_ingot" = 1.2
                "minecraft:gilded_blackstone" = 1.2
                "minecraft:coal_block" = 0.386
                "minecraft:iron_block" = 3.086
                "minecraft:lapis_block" = 0.772
                "minecraft:gold_block" = 4.63
                "minecraft:emerald_block" = 5.787
                "minecraft:diamond_block" = 10.417
                "minecraft:amethyst_shard" = 1.0
                "minecraft:emerald" = 1.5
                "minecraft:ancient_debris" = 1.6
                "minecraft:netherite_scrap" = 1.6
                "minecraft:flint_and_steel" = 0.8
                "minecraft:flint" = 0.1
                "minecraft:quartz" = 0.1
                "minecraft:compass" = 2.0
                "minecraft:clock" = 2.0
                "minecraft:recovery_compass" = 4.0
                "minecraft:leather_horse_armor" = 1.0
                "minecraft:iron_horse_armor" = 1.5
                "minecraft:golden_horse_armor" = 2.5
                "minecraft:diamond_horse_armor" = 3.5
                                
                # Per Cobblestone = 0.1
                # Per Iron Ingot = 0.8
                # Per Gold Ingot = 1.2 (I defined it)
                # Per Diamond = 2.7
                # Per Netherite Ingot = 6.4
                                
                # Per 4 = corr * 2.5667
                # Per 7 = corr * 3.435
                # Per 9 = corr * 3.858
                # Per 1/9 = corr / 3.858 = corr * 0.26
                                
                # Food, Agris and its Related (excluding fishes)
                "minecraft:bamboo" = 0.05
                "minecraft:cocoa_beans" = 0.05
                "minecraft:apple" = 0.1
                "minecraft:bread" = 0.2
                "minecraft:potato" = 0.2
                "minecraft:baked_potato" = 0.2
                "minecraft:carrot" = 0.2
                "minecraft:wheat_seeds" = 0.1
                "minecraft:beetroot_seeds" = 0.1
                "minecraft:pumpkin_seeds" = 0.1
                "minecraft:melon_seeds" = 0.1
                "minecraft:melon_slice" = 0.1
                "minecraft:glow_berries" = 0.1
                "minecraft:pumpkin" = 0.2
                "minecraft:golden_carrot" = 1.2
                "minecraft:golden_apple" = 4.4
                "minecraft:enchanted_golden_apple" = 20.0
                "minecraft:mushroom_stew" = 0.4
                "minecraft:suspicious_stew" = 0.4
                """);
        return writer;
    }

    @NotNull
    private static FileWriter writeDamageFile(File soundFile) throws IOException {
        FileWriter writer = new FileWriter(soundFile);
        writer.write("""  
                "anvil" = [
                    "minecraft:block.anvil.hit",
                    "minecraft:block.anvil.break",
                    "minecraft:block.anvil.place",
                    "minecraft:block.anvil.step",
                    "minecraft:block.anvil.fall",
                    "minecraft:block.anvil.destroy",
                    "minecraft:block.anvil.land",
                    "minecraft:block.anvil.use"
                ]
                                
                "arrow" = [
                    "minecraft:item.crossbow.loading_start",
                    "minecraft:item.crossbow.loading_middle",
                    "minecraft:item.crossbow.loading_end",
                    "minecraft:item.crossbow.quick_charge_1",
                    "minecraft:item.crossbow.quick_charge_2",
                    "minecraft:item.crossbow.quick_charge_3",
                    "minecraft:entity.arrow.shoot",
                    "minecraft:entity.skeleton.shoot"
                ]
                                
                "badRespawnPoint" = [
                    "minecraft:entity.generic.explode",
                    "minecraft:block.respawn_anchor.charge",
                    "minecraft:block.respawn_anchor.deplete",
                    "minecraft:block.respawn_anchor.set_spawn",
                    "minecraft:block.respawn_anchor.ambient"
                ]
                                
                "drown" = [
                    "minecraft:item.bucket.fill",
                    "minecraft:item.bucket.empty",
                    "minecraft:item.bucket.fill_fish",
                    "minecraft:item.bucket.empty_fish",
                    "minecraft:item.bucket.fill_axolotl",
                    "minecraft:item.bucket.empty_axolotl",
                    "minecraft:ambient.underwater.enter",
                    "minecraft:ambient.underwater.exit",
                    "minecraft:ambient.underwater.loop",
                    "minecraft:ambient.underwater.loop.additions",
                    "minecraft:ambient.underwater.loop.additions.rare",
                    "minecraft:ambient.underwater.loop.additions.ultra_rare",
                    "minecraft:block.water.ambient",
                    "minecraft:entity.player.splash_high_speed",
                    "minecraft:entity.generic.swim",
                    "minecraft:music.under_water",
                    "minecraft:block.pointed_dripstone.drip_water",
                    "minecraft:block.pointed_dripstone.drip_water_into_cauldron"
                ]
                                      
                "fireball" = ["minecraft:entity.blaze.shoot", "minecraft:block.fire.item.firecharge.use", "minecraft:entity.generic.explode"]
                                
                "fireworks" = [
                    "minecraft:entity.firework_rocket.blast",
                    "minecraft:entity.firework_rocket.blast_far",
                    "minecraft:entity.firework_rocket.large_blast",
                    "minecraft:entity.firework_rocket.large_blast_far",
                    "minecraft:entity.firework_rocket.launch",
                    "minecraft:entity.firework_rocket.shoot",
                    "minecraft:entity.firework_rocket.twinkle",
                    "minecraft:entity.firework_rocket.twinkle_far"
                ]
                                
                "flyIntoWall" = "minecraft:item.elytra.flying"
                                
                "freeze" = [
                    "minecraft:block.powder_snow.break",
                    "minecraft:block.powder_snow.fall",
                    "minecraft:block.powder_snow.hit",
                    "minecraft:block.powder_snow.place",
                    "minecraft:block.powder_snow.step",
                    "minecraft:item.bucket.empty_powder_snow",
                    "minecraft:item.bucket.fill_powder_snow"
                ]
                                
                "inFire" = [
                    "minecraft:entity.generic.explode",
                    "minecraft:entity.blaze.shoot",
                    "minecraft:block.fire.ambient",
                    "minecraft:block.fire.extinguish",
                    "minecraft:block.fire.item.firecharge.use",
                    "minecraft:block.fire.item.flintandsteel.use",
                    "minecraft:block.lava.ambient",
                    "minecraft:block.lava.extinguish",
                    "minecraft:block.lava.pop",
                    "minecraft:item.bucket.empty_lava",
                    "minecraft:item.bucket.fill_lava"
                ]
                                
                "onFire" = [
                    "minecraft:entity.generic.explode",
                    "minecraft:entity.blaze.shoot",
                    "minecraft:block.fire.ambient",
                    "minecraft:block.fire.extinguish",
                    "minecraft:block.fire.item.firecharge.use",
                    "minecraft:block.fire.item.flintandsteel.use",
                    "minecraft:block.pointed_dripstone.drip_lava",
                    "minecraft:block.pointed_dripstone.drip_lava_into_cauldron"
                ]
                                
                "lava" = [
                    "minecraft:block.lava.ambient",
                    "minecraft:block.lava.extinguish",
                    "minecraft:block.lava.pop",
                    "minecraft:item.bucket.empty_lava",
                    "minecraft:item.bucket.fill_lava",
                    "minecraft:block.pointed_dripstone.drip_lava",
                    "minecraft:block.pointed_dripstone.drip_lava_into_cauldron"
                ]
                                
                "magic" = [
                    "minecraft:block.conduit.activate",
                    "minecraft:block.conduit.ambient",
                    "minecraft:block.conduit.ambient.short",
                    "minecraft:block.conduit.attack.target",
                    "minecraft:block.conduit.deactivate",
                    "minecraft:entity.generic.drink",
                    "minecraft:entity.witch.drink",
                    "minecraft:entity.wandering_trader.drink_potion",
                    "minecraft:entity.entity.potion.throw",
                    "minecraft:entity.entity.potion.splash",
                    "minecraft:entity.entity.witch.throw"
                ]
                                
                "indirectMagic" = [
                    "minecraft:entity.generic.drink",
                    "minecraft:entity.witch.drink",
                    "minecraft:entity.wandering_trader.drink_potion",
                    "minecraft:entity.entity.potion.throw",
                    "minecraft:entity.entity.potion.splash",
                    "minecraft:entity.entity.witch.throw"
                ]
                                
                "stalagmite" = [
                    "minecraft:block.pointed_dripstone.break",
                    "minecraft:block.pointed_dripstone.fall",
                    "minecraft:block.pointed_dripstone.hit",
                    "minecraft:block.pointed_dripstone.place",
                    "minecraft:block.pointed_dripstone.step",
                    "minecraft:block.pointed_dripstone.drip_water",
                    "minecraft:block.pointed_dripstone.drip_water_into_cauldron",
                    "minecraft:block.pointed_dripstone.drip_lava",
                    "minecraft:block.pointed_dripstone.drip_lava_into_cauldron"
                ]
                                
                "fallingStalactite" = [
                    "minecraft:block.pointed_dripstone.break",
                    "minecraft:block.pointed_dripstone.fall",
                    "minecraft:block.pointed_dripstone.hit",
                    "minecraft:block.pointed_dripstone.place",
                    "minecraft:block.pointed_dripstone.step",
                    "minecraft:block.pointed_dripstone.drip_water",
                    "minecraft:block.pointed_dripstone.drip_water_into_cauldron",
                    "minecraft:block.pointed_dripstone.drip_lava",
                    "minecraft:block.pointed_dripstone.drip_lava_into_cauldron"
                ]
                                
                "sweetBerryBush" = [
                    "minecraft:block.sweet_berry_bush.break",
                    "minecraft:block.sweet_berry_bush.place",
                    "minecraft:block.sweet_berry_bush.pick_berries",
                    "minecraft:entity.player.hurt_sweet_berry_bush"
                ]
                                
                "thorns" = "minecraft:subtitles.enchant.thorns.hit"
                                
                "trident" = [
                    "minecraft:item.trident.riptide_1",
                    "minecraft:item.trident.riptide_2",
                    "minecraft:item.trident.riptide_3",
                    "minecraft:item.trident.thunder"
                ]
                                
                "wither" = [
                    "minecraft:entity.wither.ambient",
                    "minecraft:entity.wither.break_block",
                    "minecraft:entity.wither.death",
                    "minecraft:entity.wither.hurt",
                    "minecraft:entity.wither.shoot",
                    "minecraft:entity.wither.spawn"
                ]
                                           
                "axolotl" = ["minecraft:entity.axolotl.idle_water", "minecraft:entity.axolotl.idle_air", "minecraft:entity.axolotl.attack"]
                "bogged" = ["minecraft:entity.bogged.ambient", "minecraft:entity.bogged.step"]
                "bee" = ["minecraft:entity.bee.loop", "minecraft:entity.bee.loop_aggressive", "minecraft:entity.bee.sting"]
                "blaze" = ["minecraft:entity.blaze.ambient", "minecraft:entity.blaze.burn"]
                "cave_spider" = ["minecraft:entity.spider.ambient", "minecraft:entity.spider.step"]
                "creaking" = ["minecraft:entity.creaking.activate", "minecraft:entity.creaking.ambient", "minecraft:entity.creaking.attack", "minecraft:entity.creaking.step", "minecraft:entity.creaking.unfreeze"]
                "creeper" = ["minecraft:entity.creeper.primed", "minecraft:entity.creeper.hurt"]
                "dolphin" = ["minecraft:entity.dolphin.ambient", "minecraft:entity.dolphin.ambient_water"]
                "drowned" = ["minecraft:entity.drowned.ambient", "minecraft:entity.drowned.ambient_water", "minecraft:entity.drowned.step"]
                "elder_guardian" = ["minecraft:entity.elder_guardian.ambient", "minecraft:entity.elder_guardian.curse", "minecraft:entity.elder_guardian.attack", "minecraft:entity.guardian.ambient", "minecraft:entity.guardian.attack"]
                "ender_dragon" = ["minecraft:entity.ender_dragon.ambient", "minecraft:entity.ender_dragon.growl"]
                "enderman" = ["minecraft:entity.enderman.ambient", "minecraft:entity.enderman.stare", "minecraft:entity.enderman.scream", "minecraft:entity.enderman.teleport"]
                # "endermite" = ["minecraft:"]
                "evoker" = ["minecraft:entity.evoker.ambient", "minecraft:entity.evoker.cast_spell", "minecraft:entity.evoker.celebrate", "minecraft:entity.evoker.prepare_summon", "minecraft:entity.evoker.prepare_attack", "minecraft:entity.evoker_fangs.attack"]
                "frog" = ["minecraft:entity.frog.ambient", "minecraft:entity.frog.eat", "minecraft:entity.frog.tongue", "minecraft:entity.frog.long_jump", "minecraft:entity.frog.step"]
                "ghast" = ["minecraft:entity.ghast.ambient", "minecraft:entity.ghast.warn", "minecraft:entity.ghast.hurt", "minecraft:entity.ghast.scream"]
                # "giant" = ["minecraft:"]
                "goat" = ["minecraft:entity.goat.ambient", "minecraft:entity.goat.prepare_ram", "minecraft:entity.goat.screaming.ambient", "minecraft:entity.goat.screaming.prepare_ram"]
                "guardian" = ["minecraft:entity.elder_guardian.ambient", "minecraft:entity.elder_guardian.curse", "minecraft:entity.elder_guardian.attack", "minecraft:entity.guardian.ambient", "minecraft:entity.guardian.attack"]
                "hoglin" = ["minecraft:entity.hoglin.ambient", "minecraft:entity.hoglin.attack", "minecraft:entity.hoglin.angry"]
                # "husk" = ["minecraft:"]
                "illusioner" = ["minecraft:entity.illusioner.ambient", "minecraft:entity.illusioner.cast_spell", "minecraft:entity.illusioner.prepare_blindness", "minecraft:entity.illusioner.prepare_mirror"]
                "iron_golem" = ["minecraft:entity.iron_golem.step"]
                "rabbit" = ["minecraft:entity.rabbit.ambient", "minecraft:entity.rabbit.attack"]
                "llama" = ["minecraft:entity.llama.ambient", "minecraft:entity.llama.angry", "minecraft:entity.llama.spit"]
                "magma_cube" = ["minecraft:entity.magma_cube.jump", "minecraft:entity.magma_cube.squish", "minecraft:entity.magma_cube.squish_small"]
                "panda" = ["minecraft:entity.panda.ambient", "minecraft:entity.panda.aggressive_ambient", "minecraft:entity.panda.bite"]
                "phantom" = ["minecraft:entity.phantom.ambient", "minecraft:entity.phantom.swoop"]
                "piglin" = ["minecraft:entity.piglin.ambient", "minecraft:entity.piglin.angry"]
                "piglin_brute" = ["minecraft:entity.piglin_brute.ambient", "minecraft:entity.piglin_brute.angry"]
                "pillager" = ["minecraft:entity.pillager.ambient", "minecraft:entity.pillager.celebrate"]
                "polar_bear" = ["minecraft:entity.polar_bear.ambient", "minecraft:entity.polar_bear.warning", ]
                "putterfish" = ["minecraft:entity.puffer_fish.sting", "minecraft:entity.puffer_fish.blow_out", "minecraft:entity.puffer_fish.blow_up"]
                "ravager" = ["minecraft:entity.ravager.ambient", "minecraft:entity.ravager.step", "minecraft:entity.ravager.roar", "minecraft:entity.ravager.attack", "minecraft:entity.ravager.celebrate"]
                "shulker" = ["minecraft:entity.shulker.ambient", "minecraft:entity.shulker.shoot"]
                # "silverfish" = ["minecraft:"]
                "skeleton" = ["minecraft:entity.skeleton.ambient", "minecraft:entity.skeleton.step"]
                "slime" = ["minecraft:entity.slime.jump", "minecraft:entity.slime.squish", "minecraft:entity.slime.attack"]
                # "snow_golem" = ["minecraft:"]
                "stray" = ["minecraft:entity.stray.ambient", "minecraft:entity.stray.step"]
                "spider" = ["minecraft:entity.spider.ambient", "minecraft:entity.spider.step"]
                "vex" = ["minecraft:entity.vex.ambient", "minecraft:entity.vex.charge"]
                "vindicator" = ["minecraft:entity.vindicator.ambient", "minecraft:entity.vindicator.celebrate"]
                "witch" = ["minecraft:entity.witch.ambient", "minecraft:entity.witch.celebrate"]
                "wither_skeleton" = ["minecraft:entity.wither_skeleton.ambient", "minecraft:entity.wither_skeleton.step"]
                "wolf" = ["minecraft:entity.wolf.ambient", "minecraft:entity.wolf.growl", "minecraft:entity.wolf.howl"]
                "zoglin" = ["minecraft:entity.zoglin.ambient", "minecraft:entity.zoglin.attack", "minecraft:entity.zoglin.angry"]
                # "zombie" = ["minecraft:"]
                "zombified_piglin" = ["minecraft:entity.zombified_piglin.ambient", "minecraft:entity.zombified_piglin.angry"]
                                
                "warden" = [
                    "minecraft:entity.warden.agitated",
                    "minecraft:entity.warden.ambient",
                    "minecraft:entity.warden.angry",
                    "minecraft:entity.warden.dig",
                    "minecraft:entity.warden.emerge",
                    "minecraft:entity.warden.listening",
                    "minecraft:entity.warden.listening_angry",
                    "minecraft:entity.warden.roar",
                    "minecraft:entity.warden.sniff",
                    "minecraft:entity.warden.sonic_boom",
                    "minecraft:entity.warden.sonic_charge",
                    "minecraft:entity.warden.tendril_clicks"
                ]
                """
        );
        return writer;
    }

    @NotNull
    private static FileWriter writeSmeltFile(File smeltFile) throws IOException {
        FileWriter writer = new FileWriter(smeltFile);
        writer.write("""
                "minecraft:iron_ingot" = 0.1
                "minecraft:gold_ingot" = 0.2
                "minecraft:netherite_scrap" = 2.0
                "minecraft:cooked_porkchop" = 0.1
                "minecraft:cooked_beef" = 0.1
                "minecraft:cooked_chicken" = 0.075
                "minecraft:cooked_rabbit" = 0.075
                "minecraft:cooked_mutton" = 0.075
                "minecraft:baked_potato" = 0.05
                "minecraft:cooked_cod" = 0.05
                "minecraft:cooked_salmon" = 0.075
                "minecraft:cooked_tropical_fish" = 0.05
                "minecraft:cooked_kelp" = 0.01
                """);
        return writer;
    }

    @NotNull
    private static FileWriter writeNearbyFile(File nearbyFile) throws IOException {
        FileWriter writer = new FileWriter(nearbyFile);
        writer.write("""
                # FLOWERS
                                        
                [flowers."minecraft:dandelion"]
                value = 0.3
                radius = 8
                [flowers."minecraft:poppy"]
                value = 0.3
                radius = 8
                [flowers."minecraft:blue_orchid"]
                value = 0.4
                radius = 8
                [flowers."minecraft:allium"]
                value = 0.4
                radius = 8
                [flowers."minecraft:azure_bluet"]
                value = 0.3
                radius = 8
                [flowers."minecraft:red_tulip"]
                value = 0.4
                radius = 8
                [flowers."minecraft:orange_tulip"]
                value = 0.4
                radius = 8
                [flowers."minecraft:white_tulip"]
                value = 0.4
                radius = 8
                [flowers."minecraft:pink_tulip"]
                value = 0.4
                radius = 8
                [flowers."minecraft:oxeye_daisy"]
                value = 0.3
                radius = 8
                [flowers."minecraft:cornflower"]
                value = 0.3
                radius = 8
                [flowers."minecraft:lily_of_the_valley"]
                value = 0.3
                radius = 8
                [flowers."minecraft:torchflower"]
                value = 1
                radius = 8
                [flowers."minecraft:sunflower"]
                value = 0.4
                radius = 8
                [flowers."minecraft:lilac"]
                value = 0.4
                radius = 8
                [flowers."minecraft:rose_bush"]
                value = 0.4
                radius = 8
                [flowers."minecraft:peony"]
                value = 0.4
                radius = 8
                [flowers."minecraft:pitcher_plant"]
                value = 1
                radius = 8
                                        
                # LEAVES
                                        
                [leaves."minecraft:oak_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:spruce_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:birch_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:jungle_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:acacia_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:dark_oak_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:azalea_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:flowering_azalea_leaves"]
                value = 0.2
                radius = 8
                [leaves."minecraft:mangrove_leaves"]
                value = 0.1
                radius = 8
                [leaves."minecraft:cherry_leaves"]
                value = 0.2
                radius = 8
                                        
                # OTHER_PLANTS
                                        
                [other_plants."minecraft:spore_blossom"]
                value = 0.3
                radius = 12
                [other_plants."minecraft:azalea"]
                value = 0.3
                radius = 12
                [other_plants."minecraft:flowering_azalea"]
                value = 0.4
                radius = 12
                [other_plants."minecraft:pink_petals"]
                value = 0.4
                radius = 6
                [other_plants."minecraft:sea_pickle"]
                value = 0.2
                radius = 6
                [other_plants."minecraft:lily_pad"]
                value = 0.1
                radius = 6
                [other_plants."minecraft:waterlily"]
                value = 0.1
                radius = 6
                [other_plants."minecraft:shroomlight"]
                value = 0.1
                radius = 8
                                        
                # UTILITIES
                                        
                ["minecraft:beacon"]
                value = 1.5
                radius = 12
                ["minecraft:conduit"]
                value = 1.25
                radius = 12
                                        
                # CANDLES
                                        
                [candles."minecraft:candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:white_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:orange_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:magenta_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:light_blue_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:yellow_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:lime_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:pink_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:gray_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:light_gray_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:cyan_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:purple_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:blue_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:brown_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:green_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:red_candle"]
                value = 0.2
                radius = 6
                [candles."minecraft:black_candle"]
                value = 0.2
                radius = 6
                                        
                # FIRELIGHTS
                                        
                [fire_lights."minecraft:campfire"]
                value = 0.5
                radius = 12
                [fire_lights."minecraft:soul_campfire"]
                value = 0.25
                radius = 12
                [fire_lights."minecraft:torch"]
                value = 0.05
                radius = 6
                [fire_lights."minecraft:soul_torch"]
                value = 0.025
                radius = 6
                [fire_lights."minecraft:jack_o_lantern"]
                value = 0.2
                radius = 8
                [fire_lights."minecraft:lit_pumpkin"]
                value = 0.2
                radius = 8
                [fire_lights."minecraft:lantern"]
                value = 0.2
                radius = 8
                [fire_lights."minecraft:soul_lantern"]
                value = 0.1
                radius = 8
                                        
                # COPPER_BULBS
                                        
                [glowing_blocks."minecraft:copper_bulb"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:exposed_copper_bulb"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:weathered_copper_bulb"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:oxidized_copper_bulb"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:waxed_copper_bulb"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:waxed_exposed_copper_bulb"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:waxed_weathered_copper_bulb"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:waxed_oxidized_copper_bulb"]
                value = 0.2
                radius = 8
                                        
                # OTHER_GLOWING_BLOCKS
                                        
                [glowing_blocks."minecraft:end_rod"]
                value = 0.1
                radius = 6
                [glowing_blocks."minecraft:glowstone"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:redstone_lamp"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:sea_lantern"]
                value = 0.2
                radius = 8
                [glowing_blocks."minecraft:pearlescent_froglight"]
                value = 0.3
                radius = 8
                [glowing_blocks."minecraft:verdant_froglight"]
                value = 0.3
                radius = 8
                [glowing_blocks."minecraft:ochre_froglight"]
                value = 0.3
                radius = 8
                                        
                # DECORATIONS
                                        
                [decorations."minecraft:decorated_pot"]
                value = 0.1
                radius = 4
                [decorations."minecraft:painting"]
                value = 0.1
                radius = 6
                """);
        return writer;
    }

    @NotNull
    private static FileWriter writeEntityFile(File entityFile) throws IOException {
        FileWriter writer = new FileWriter(entityFile);
        writer.write("""
                "minecraft:endermite" = 0.125
                "minecraft:silverfish" = 0.25
                "minecraft:slime" = 0.25
                "minecraft:piglin" = 0.375
                "minecraft:vex" = 0.375
                "minecraft:zombie" = 0.375
                "minecraft:zombie_villager" = 0.375
                "minecraft:magma_cube" = 0.375
                "minecraft:phantom" = 0.375
                "minecraft:drowned" = 0.375
                "minecraft:husk" = 0.375
                "minecraft:shulker" = 0.5
                "minecraft:skeleton" = 0.5
                "minecraft:spider" = 0.5
                "minecraft:zombified_piglin" = 0.5
                "minecraft:stray" = 0.625
                "minecraft:pillager" = 0.625
                "minecraft:cave_spider" = 0.625
                "minecraft:blaze" = 0.625
                "minecraft:creeper" = 0.625
                "minecraft:vindicator" = 0.75
                "minecraft:enderman" = 0.75
                "minecraft:wither_skeleton" = 0.75
                "minecraft:guardian" = 0.75
                "minecraft:witch" = 1.0
                "minecraft:ghast" = 1.0
                "minecraft:piglin_brute" = 1.0
                "minecraft:illusioner" = 1.25
                "minecraft:evoker" = 1.5
                "minecraft:hoglin" = 2.5
                "minecraft:zoglin" = 2.5
                "minecraft:ravager" = 5.0
                "minecraft:giant" = 7.5
                "minecraft:elder_guardian" = 20.0
                "minecraft:warden" = 25.0
                "minecraft:wither" = 25.0
                "minecraft:ender_dragon" = 35.0
                """);
        return writer;
    }

    @NotNull
    private static FileWriter writeAdvancementFile(File advancementFile) throws IOException {
        FileWriter writer = new FileWriter(advancementFile);
        writer.write("""
                "minecraft:story/mine_stone" = 0.5
                "minecraft:story/upgrade_tools" = 0.5
                "minecraft:story/smelt_iron" = 1.0
                "minecraft:story/obtain_armor" = 1.0
                "minecraft:story/lava_bucket" = 1.0
                "minecraft:story/iron_tools" = 1.0
                "minecraft:story/deflect_arrow" = 1.0
                "minecraft:story/form_obsidian" = 1.0
                "minecraft:story/mine_diamond" = 2.0
                "minecraft:story/enter_the_nether" = 2.0
                "minecraft:story/shiny_gear" = 5.0
                "minecraft:story/enchant_item" = 2.0
                "minecraft:story/cure_zombie_villager" = 4.0
                "minecraft:story/follow_ender_eye" = 3.0
                "minecraft:story/enter_the_end" = 2.0
                "minecraft:nether/return_to_sender" = 3.0
                "minecraft:nether/find_bastion" = 3.0
                "minecraft:nether/obtain_ancient_debris" = 4.0
                "minecraft:nether/fast_travel" = 5.0
                "minecraft:nether/find_fortress" = 3.0
                "minecraft:nether/obtain_crying_obsidian" = 1.0
                "minecraft:nether/distract_piglin" = 2.0
                "minecraft:nether/ride_strider" = 2.0
                "minecraft:nether/uneasy_alliance" = 5.0
                "minecraft:nether/loot_bastion" = 3.0
                "minecraft:nether/use_lodestone" = 1.0
                "minecraft:nether/netherite_armor" = 10.0
                "minecraft:nether/get_wither_skull" = 5.0
                "minecraft:nether/obtain_blaze_rod" = 2.0
                "minecraft:nether/charge_respawn_anchor" = 2.0
                "minecraft:nether/ride_strider_in_overworld_lava" = 3.0
                "minecraft:nether/explore_nether" = 5.0
                "minecraft:nether/summon_wither" = 3.0
                "minecraft:nether/brew_potion" = 1.0
                "minecraft:nether/create_beacon" = 2.0
                "minecraft:nether/all_potions" = 5.0
                "minecraft:nether/use_soul_speed" = 0.5
                "minecraft:nether/create_full_beacon" = 7.0
                "minecraft:nether/all_effects" = 35.0
                "minecraft:end/kill_dragon" = 5.0
                "minecraft:end/dragon_egg" = 5.0
                "minecraft:end/enter_end_gateway" = 2.0
                "minecraft:end/respawn_dragon" = 5.0
                "minecraft:end/dragon_breath" = 2.0
                "minecraft:end/find_end_city" = 4.0
                "minecraft:end/elytra" = 8.0
                "minecraft:end/levitate" = 4.0
                "minecraft:adventure/voluntary_exile" = 2.0
                "minecraft:adventure/spyglass_at_parrot" = 2.0
                "minecraft:adventure/kill_a_mob" = 1.0
                "minecraft:adventure/read_power_from_chiseled_bookshelf" = 2.0
                "minecraft:adventure/trade" = 2.0
                "minecraft:adventure/trim_with_any_armor_pattern" = 2.0
                "minecraft:adventure/honey_block_slide" = 2.0
                "minecraft:adventure/ol_betsy" = 1.0
                "minecraft:adventure/lightning_rod_with_villager_no_fire" = 3.0
                "minecraft:adventure/fall_from_world_height" = 4.0
                "minecraft:adventure/salvage_sherd" = 3.0
                "minecraft:adventure/avoid_vibration" = 2.0
                "minecraft:adventure/sleep_in_bed" = 1.0
                "minecraft:adventure/hero_of_the_village" = 10.0
                "minecraft:adventure/spyglass_at_ghast" = 2.0
                "minecraft:adventure/throw_trident" = 2.0
                "minecraft:adventure/kill_mob_near_sculk_catalyst" = 2.0
                "minecraft:adventure/shoot_arrow" = 1.0
                "minecraft:adventure/kill_all_mobs" = 15.0
                "minecraft:adventure/totem_of_undying" = 5.0
                "minecraft:adventure/summon_iron_golem" = 3.0
                "minecraft:adventure/trade_at_world_height" = 3.0
                "minecraft:adventure/trim_with_all_exclusive_armor_patterns" = 15.0
                "minecraft:adventure/two_birds_one_arrow" = 3.0
                "minecraft:adventure/whos_the_pillager_now" = 2.0
                "minecraft:adventure/arbalistic" = 6.0
                "minecraft:adventure/craft_decorated_pot_using_only_sherds" = 3.0
                "minecraft:adventure/adventuring_time" = 30.0
                "minecraft:adventure/play_jukebox_in_meadows" = 3.0
                "minecraft:adventure/walk_on_powder_snow_with_leather_boots" = 2.0
                "minecraft:adventure/spyglass_at_dragon" = 2.0
                "minecraft:adventure/very_very_frightening" = 2.0
                "minecraft:adventure/sniper_duel" = 4.0
                "minecraft:adventure/bullseye" = 4.0
                "minecraft:husbandry/safely_harvest_honey" = 2.0
                "minecraft:husbandry/breed_an_animal" = 2.0
                "minecraft:husbandry/allay_deliver_item_to_player" = 3.0
                "minecraft:husbandry/tame_an_animal" = 2.0
                "minecraft:husbandry/make_a_sign_glow" = 1.0
                "minecraft:husbandry/fishy_business" = 1.0
                "minecraft:husbandry/silk_touch_nest" = 3.0
                "minecraft:husbandry/tadpole_in_a_bucket" = 3.0
                "minecraft:husbandry/obtain_sniffer_egg" = 5.0
                "minecraft:husbandry/plant_seed" = 0.5
                "minecraft:husbandry/wax_on" = 1.0
                "minecraft:husbandry/bred_all_animals" = 20.0
                "minecraft:husbandry/allay_deliver_cake_to_note_block" = 2.0
                "minecraft:husbandry/complete_catalogue" = 17.0
                "minecraft:husbandry/tactical_fishing" = 1.0
                "minecraft:husbandry/leash_all_frog_variants" = 5.0
                "minecraft:husbandry/feed_snifflet" = 4.0
                "minecraft:husbandry/balanced_diet" = 12.0
                "minecraft:husbandry/obtain_netherite_hoe" = 6.0
                "minecraft:husbandry/wax_off" = 0.5
                "minecraft:husbandry/axolotl_in_a_bucket" = 3.0
                "minecraft:husbandry/froglights" = 5.0
                "minecraft:husbandry/plant_any_sniffer_seed" = 5.0
                "minecraft:husbandry/kill_axolotl_target" = 3.0
                """);
        return writer;
    }

    @NotNull
    private static FileWriter writeBlockFile(File blockFile) throws IOException {
        FileWriter writer = new FileWriter(blockFile);
        writer.write("""         
                "minecraft:wheat" = 0.5
                "minecraft:carrot" = 0.5
                "minecraft:potato" = 0.5
                "minecraft:beetroots" = 0.4
                "minecraft:melon" = 0.3
                "minecraft:pumpkin" = 0.2
                "minecraft:bamboo" = 0.25
                "minecraft:cocoa" = 0.25
                "minecraft:sweet_berry_bush" = 0.25
                                        
                "minecraft:coal_ore" = 0.3334
                "minecraft:copper_ore" = 0.3334
                "minecraft:iron_ore" = 0.625
                "minecraft:gold_ore" = 1.25
                "minecraft:redstone_ore" = 0.5
                "minecraft:lapis_ore" = 0.75
                "minecraft:diamond_ore" = 3.0
                "minecraft:emerald_ore" = 1.75
                "minecraft:nether_quartz_ore" = 0.5
                "minecraft:nether_gold_ore" = 1.0
                "minecraft:deepslate_coal_ore" = 0.3334
                "minecraft:deepslate_copper_ore" = 0.3334
                "minecraft:deepslate_iron_ore" = 0.625
                "minecraft:deepslate_gold_ore" = 1.25
                "minecraft:deepslate_redstone_ore" = 0.5
                "minecraft:deepslate_lapis_ore" = 0.75
                "minecraft:deepslate_diamond_ore" = 3.0
                "minecraft:deepslate_emerald_ore" = 1.75
                """);
        return writer;
    }
}
