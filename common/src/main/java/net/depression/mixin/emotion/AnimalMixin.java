package net.depression.mixin.emotion;

import net.depression.mental.MentalStatus;
import net.depression.network.ActionbarHintPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalMixin {
    @Inject(method = "mobInteract", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Animal;setInLove(Lnet/minecraft/world/entity/player/Player;)V"))
    private void onBreedAnimal(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        onFeedAnimal(player, (Animal) (Object) this);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Animal;ageUp(IZ)V"))
    private void onGrowUpAnimal(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        onFeedAnimal(player, (Animal) (Object) this);
    }

    @Unique
    private static void onFeedAnimal(Player player, Animal animal) {
        if (!player.level().isClientSide()) {
            MentalStatus mentalStatus = MentalStatus.getMentalStatusByServerPlayer(player);
            double healValue = mentalStatus.mentalHeal(animal.getEncodeId(), 0.5);
            if (healValue > 0.25) {
                ActionbarHintPacket.sendFeedAnimalHealPacket((ServerPlayer) player, animal.getName());
            }
        }
    }
}
