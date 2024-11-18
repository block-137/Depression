package net.depression;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.block.ModBlocks;
import net.depression.block.entity.ModBannerPatterns;
import net.depression.config.ServerConfig;
import net.depression.effect.ModEffects;
import net.depression.item.ModCreativeTabs;
import net.depression.item.ModItems;
import net.depression.listener.BlockEventListener;
import net.depression.listener.CommandRegistrationListener;
import net.depression.listener.EntityEventListener;
import net.depression.listener.PlayerEventListener;
import net.depression.mental.PTSDManager;
import net.depression.network.DiaryUpdatePacket;
import net.depression.network.MentalTraitPacket;
import net.depression.network.PlaySoundPacket;
import net.depression.server.Registry;
import net.depression.sound.ModSounds;
import net.depression.world.VillageAdditions;
import org.slf4j.Logger;

public final class Depression {
    public static final String MOD_ID = "depression";
    public static final String MOD_VERSION = "0.1.4";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ModBannerPatterns.register();
        ModEffects.register(); //必须先注册效果（因为物品使用了效果），否则物品注册时会报错
        ModBlocks.register();
        ModItems.register();
        ModCreativeTabs.register();
        ModSounds.register(); //必须先注册音效（因为心理医生使用了音效）
        VillageAdditions.register();

        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                MentalTraitPacket.MENTAL_TRAIT_PACKET, Registry::receiveMentalTraitPacket);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                DiaryUpdatePacket.DIARY_UPDATE_PACKET, Registry::receiveDiaryUpdatePacket);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                PlaySoundPacket.PLAY_SOUND_PACKET, PTSDManager::receivePlaySoundPacket);

        CommandRegistrationEvent.EVENT.register(CommandRegistrationListener::registerCommands);
        PlayerEvent.SMELT_ITEM.register(PlayerEventListener::onSmeltItem);
        PlayerEvent.PLAYER_QUIT.register(PlayerEventListener::onPlayerQuit);
        PlayerEvent.ATTACK_ENTITY.register(PlayerEventListener::onAttackEntity);
        PlayerEvent.PLAYER_ADVANCEMENT.register(PlayerEventListener::onPlayerAdvancement);
        BlockEvent.PLACE.register(BlockEventListener::onBlockPlace);
        BlockEvent.BREAK.register(BlockEventListener::onBlockBreak);
        EntityEvent.LIVING_DEATH.register(EntityEventListener::onEntityDeath);
        ServerConfig.load();
    }
}
