package net.depression.mixin.emotion;

import net.depression.client.ClientActionbarHint;
import net.depression.client.DepressionClient;
import net.depression.mental.MentalStatus;
import net.depression.network.ActionbarHintPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public class AnimalMixin {
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;getAge()I", ordinal = 0))
    private void onFeedAnimal(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        Animal animal = (Animal) (Object) this;
        if (!player.getLevel().isClientSide()) {
            MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
            double healValue = mentalStatus.mentalHeal(animal.getEncodeId(), 0.5);
            if (healValue > 0.25) {
                ActionbarHintPacket.sendFeedAnimalHealPacket((ServerPlayer) player, animal.getName());
            }
        }
    }
}
