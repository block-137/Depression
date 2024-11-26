package net.depression.mixin.emotion;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.depression.mental.MentalStatus;
import net.depression.network.ActionbarHintPacket;
import net.depression.util.TempValues;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootTable.class)
public class LootTableMixin {
    @Inject(method = "fill", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootContext;getRandom()Lnet/minecraft/util/RandomSource;"))
    private void onFill(CallbackInfo ci, @Local(ordinal = 0) ObjectArrayList<ItemStack> itemList) {
        if (TempValues.lootPlayer == null) {
            return;
        }
        double healValue = 0d;
        for (ItemStack itemStack : itemList) {
            if (itemStack.isEmpty()) {
                return;
            }
            healValue += MentalStatus.onLoot(TempValues.lootPlayer, itemStack);
        }
        if (healValue > 0.5) {
            ActionbarHintPacket.sendLootHealPacket(TempValues.lootPlayer);
        }
        TempValues.lootPlayer = null;
    }
}
