package net.depression;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.*;
import net.depression.listener.*;
import org.slf4j.Logger;

public final class Depression {
    public static final String MOD_ID = "depression";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        CommandRegistrationEvent.EVENT.register(CommandRegistrationListener::registerCommands);
        PlayerEvent.SMELT_ITEM.register(PlayerEventListener::onSmeltItem);
        PlayerEvent.PLAYER_QUIT.register(PlayerEventListener::onPlayerQuit);
        PlayerEvent.ATTACK_ENTITY.register(PlayerEventListener::onAttackEntity);
        BlockEvent.PLACE.register(BlockEventListener::onBlockPlace);
        BlockEvent.BREAK.register(BlockEventListener::onBlockBreak);
        EntityEvent.LIVING_DEATH.register(EntityEventListener::onEntityDeath);
    }
}
