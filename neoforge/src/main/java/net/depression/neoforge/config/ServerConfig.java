package net.depression.neoforge.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import dev.architectury.platform.Platform;
import net.depression.mental.MentalStatus;
import org.jetbrains.annotations.NotNull;

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
            config.set("emotion_stabilize_rate", 0.1);
        }
        if (!config.contains("mental_health_change_rate")) {
            config.set("mental_health_change_rate", 0.01);
        }
        if (!config.contains("ptsd_damage_rate")) {
            config.set("ptsd_damage_rate", 0.5);
        }
        if (!config.contains("ptsd_disperse_rate")) {
            config.set("ptsd_disperse_rate", 0.01);
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
                                        
                ["minecraft:dandelion"]
                value = 0.3
                radius = 8
                ["minecraft:poppy"]
                value = 0.3
                radius = 8
                ["minecraft:blue_orchid"]
                value = 0.4
                radius = 8
                ["minecraft:allium"]
                value = 0.4
                radius = 8
                ["minecraft:azure_bluet"]
                value = 0.3
                radius = 8
                ["minecraft:red_tulip"]
                value = 0.4
                radius = 8
                ["minecraft:orange_tulip"]
                value = 0.4
                radius = 8
                ["minecraft:white_tulip"]
                value = 0.4
                radius = 8
                ["minecraft:pink_tulip"]
                value = 0.4
                radius = 8
                ["minecraft:oxeye_daisy"]
                value = 0.3
                radius = 8
                ["minecraft:cornflower"]
                value = 0.3
                radius = 8
                ["minecraft:lily_of_the_valley"]
                value = 0.3
                radius = 8
                ["minecraft:torchflower"]
                value = 1
                radius = 8
                ["minecraft:sunflower"]
                value = 0.4
                radius = 8
                ["minecraft:lilac"]
                value = 0.4
                radius = 8
                ["minecraft:rose_bush"]
                value = 0.4
                radius = 8
                ["minecraft:peony"]
                value = 0.4
                radius = 8
                ["minecraft:pitcher_plant"]
                value = 1
                radius = 8
                                        
                # LEAVES
                                        
                ["minecraft:oak_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:spruce_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:birch_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:jungle_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:acacia_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:dark_oak_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:azalea_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:flowering_azalea_leaves"]
                value = 0.2
                radius = 8
                ["minecraft:mangrove_leaves"]
                value = 0.1
                radius = 8
                ["minecraft:cherry_leaves"]
                value = 0.2
                radius = 8
                                        
                # OTHER_PLANTS
                                        
                ["minecraft:spore_blossom"]
                value = 0.3
                radius = 12
                ["minecraft:azalea"]
                value = 0.3
                radius = 12
                ["minecraft:flowering_azalea"]
                value = 0.4
                radius = 12
                ["minecraft:pink_petals"]
                value = 0.4
                radius = 6
                ["minecraft:sea_pickle"]
                value = 0.2
                radius = 6
                ["minecraft:lily_pad"]
                value = 0.1
                radius = 6
                ["minecraft:waterlily"]
                value = 0.1
                radius = 6
                ["minecraft:shroomlight"]
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
                                        
                ["minecraft:candle"]
                value = 0.2
                radius = 6
                ["minecraft:white_candle"]
                value = 0.2
                radius = 6
                ["minecraft:orange_candle"]
                value = 0.2
                radius = 6
                ["minecraft:magenta_candle"]
                value = 0.2
                radius = 6
                ["minecraft:light_blue_candle"]
                value = 0.2
                radius = 6
                ["minecraft:yellow_candle"]
                value = 0.2
                radius = 6
                ["minecraft:lime_candle"]
                value = 0.2
                radius = 6
                ["minecraft:pink_candle"]
                value = 0.2
                radius = 6
                ["minecraft:gray_candle"]
                value = 0.2
                radius = 6
                ["minecraft:light_gray_candle"]
                value = 0.2
                radius = 6
                ["minecraft:cyan_candle"]
                value = 0.2
                radius = 6
                ["minecraft:purple_candle"]
                value = 0.2
                radius = 6
                ["minecraft:blue_candle"]
                value = 0.2
                radius = 6
                ["minecraft:brown_candle"]
                value = 0.2
                radius = 6
                ["minecraft:green_candle"]
                value = 0.2
                radius = 6
                ["minecraft:red_candle"]
                value = 0.2
                radius = 6
                ["minecraft:black_candle"]
                value = 0.2
                radius = 6
                                        
                # FIRELIGHTS
                                        
                ["minecraft:campfire"]
                value = 0.5
                radius = 12
                ["minecraft:soul_campfire"]
                value = 0.25
                radius = 12
                ["minecraft:torch"]
                value = 0.05
                radius = 6
                ["minecraft:soul_torch"]
                value = 0.025
                radius = 6
                ["minecraft:jack_o_lantern"]
                value = 0.2
                radius = 8
                ["minecraft:lit_pumpkin"]
                value = 0.2
                radius = 8
                ["minecraft:lantern"]
                value = 0.2
                radius = 8
                ["minecraft:soul_lantern"]
                value = 0.1
                radius = 8
                                        
                # COPPER_BULBS
                                        
                ["minecraft:copper_bulb"]
                value = 0.2
                radius = 8
                ["minecraft:exposed_copper_bulb"]
                value = 0.2
                radius = 8
                ["minecraft:weathered_copper_bulb"]
                value = 0.2
                radius = 8
                ["minecraft:oxidized_copper_bulb"]
                value = 0.2
                radius = 8
                ["minecraft:waxed_copper_bulb"]
                value = 0.2
                radius = 8
                ["minecraft:waxed_exposed_copper_bulb"]
                value = 0.2
                radius = 8
                ["minecraft:waxed_weathered_copper_bulb"]
                value = 0.2
                radius = 8
                ["minecraft:waxed_oxidized_copper_bulb"]
                value = 0.2
                radius = 8
                                        
                # OTHER_GLOWING_BLOCKS
                                        
                ["minecraft:end_rod"]
                value = 0.1
                radius = 6
                ["minecraft:glowstone"]
                value = 0.2
                radius = 8
                ["minecraft:redstone_lamp"]
                value = 0.2
                radius = 8
                ["minecraft:sea_lantern"]
                value = 0.2
                radius = 8
                ["minecraft:pearlescent_froglight"]
                value = 0.3
                radius = 8
                ["minecraft:verdant_froglight"]
                value = 0.3
                radius = 8
                ["minecraft:ochre_froglight"]
                value = 0.3
                radius = 8
                                        
                # DECORATIONS
                                        
                ["minecraft:decorated_pot"]
                value = 0.1
                radius = 4
                ["minecraft:painting"]
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
