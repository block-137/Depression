package net.depression.mixin.emotion;

import com.llamalad7.mixinextras.sugar.Local;
import net.depression.client.ClientActionbarHint;
import net.depression.mental.MentalStatus;
import net.depression.network.ActionbarHintPacket;
import net.depression.util.TempValues;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomizableContainerBlockEntity.class)
public abstract class RandomizableContainerBlockEntityMixin {
    @Inject(method = "createMenu", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/RandomizableContainerBlockEntity;unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V"))
    private void onUnpackLootTable(int i, Inventory inventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> cir) {
        TempValues.lootPlayer = (ServerPlayer) player;
    }
}
