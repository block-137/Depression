package net.depression;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import net.depression.item.ModCreativeTabs;
import net.depression.item.ModItems;
import net.depression.listener.BlockEventListener;
import net.depression.listener.CommandRegistrationListener;
import net.depression.listener.EntityEventListener;
import net.depression.listener.PlayerEventListener;
import net.depression.network.DiaryUpdatePacket;
import net.depression.server.Registry;
import net.depression.sound.ModSounds;
import org.slf4j.Logger;

public final class Depression {
    public static final String MOD_ID = "depression";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ModItems.register();
        ModCreativeTabs.register();
        ModSounds.register();

        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                DiaryUpdatePacket.DIARY_UPDATE_PACKET, Registry::receiveDiaryUpdatePacket);

        CommandRegistrationEvent.EVENT.register(CommandRegistrationListener::registerCommands);
        PlayerEvent.SMELT_ITEM.register(PlayerEventListener::onSmeltItem);
        PlayerEvent.PLAYER_QUIT.register(PlayerEventListener::onPlayerQuit);
        PlayerEvent.ATTACK_ENTITY.register(PlayerEventListener::onAttackEntity);
        PlayerEvent.PLAYER_ADVANCEMENT.register(PlayerEventListener::onPlayerAdvancement);
        BlockEvent.PLACE.register(BlockEventListener::onBlockPlace);
        BlockEvent.BREAK.register(BlockEventListener::onBlockBreak);
        EntityEvent.LIVING_DEATH.register(EntityEventListener::onEntityDeath);
    }
}
