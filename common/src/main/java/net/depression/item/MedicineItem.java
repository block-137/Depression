package net.depression.item;

import net.depression.client.ClientActionbarHint;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MedicineItem extends Item {
    public MobEffect effect;
    public int duration;
    public String loreTranslationKey;

    public MedicineItem(MobEffect effect, int duration, int amplifier, String loreTranslationKey) {
        super(new Properties().food(new FoodProperties.Builder().nutrition(0).saturationMod(0f).alwaysEat()
                .effect(new MobEffectInstance(effect, duration, amplifier, false, false, true), 1.0F).build()).arch$tab(ModCreativeTabs.ITEMS_TAB));
        this.effect = effect;
        this.duration = duration;
        this.loreTranslationKey = loreTranslationKey;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable(loreTranslationKey).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            MobEffectInstance effectInstance = player.getEffect(effect);
            if (effectInstance != null && !effectInstance.endsWithin(duration/2)) { //如果私自加量服药（效果剩余时间大于持续时间的一半）
                if (itemStack.isEdible()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), player.getEatingSound(itemStack), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    player.gameEvent(GameEvent.EAT);
                }
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 19));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                if (level.isClientSide()) {
                    ClientActionbarHint.displayTranslatable("message.depression.medicine_overdose");
                }
                return itemStack;
            }
        }
        return super.finishUsingItem(itemStack, level, livingEntity);
    }
}
