package net.depression.item;

import net.depression.client.ClientMentalStatus;
import net.depression.client.DepressionClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MentalHealthScaleItem extends Item {
    public MentalHealthScaleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (player.isCreative()) {
            return super.use(level, player, interactionHand);
        }
        player.getCooldowns().addCooldown(this, 200);
        if (level.isClientSide()) {
            ClientMentalStatus mentalStatus = DepressionClient.clientMentalStatus;
            player.sendSystemMessage(Component.translatable("message.depression.mental_health_scale_1")
                    .append(String.format("%.2f", mentalStatus.mentalHealthValue)));
            player.sendSystemMessage(Component.translatable("message.depression.mental_health_scale_2")
                    .append(Component.translatable("message.depression." + mentalStatus.mentalIllnessString)));
        }
        return super.use(level, player, interactionHand);
    }
}
