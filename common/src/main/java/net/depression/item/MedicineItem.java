package net.depression.item;

import net.depression.mental.MentalIllness;
import net.depression.mental.MentalStatus;
import net.depression.network.ActionbarHintPacket;
import net.depression.server.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MedicineItem extends Item {
    public static final HashMap<String, MobEffectInstance> effectMap = new HashMap<>();

    public String id;

    public MobEffect effect;
    public int duration;
    public int amplifier;
    public int minDelay = 0; //单位: tick
    public int maxDelay = 0;
    public String loreTranslationKey;
    private final Random random = new Random();

    public MedicineItem(MobEffect effect, int duration, int amplifier, String loreTranslationKey) {
        super(new Properties().arch$tab(ModCreativeTabs.ITEMS_TAB));
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.loreTranslationKey = loreTranslationKey;
    }

    public MedicineItem(String id, MobEffect effect, int duration, int amplifier, int minDelay, int maxDelay, String loreTranslationKey) {
        super(new Properties().arch$tab(ModCreativeTabs.ITEMS_TAB));
        this.id = id;
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.loreTranslationKey = loreTranslationKey;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        effectMap.put(id, new MobEffectInstance(effect, duration, amplifier, false, false, true));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        String string = Component.translatable(loreTranslationKey).getString();
        StringBuilder currentString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '\n') {
                list.add(Component.literal(currentString.toString()).withStyle(ChatFormatting.GRAY));
                currentString = new StringBuilder();
            }
            else {
                currentString.append(c);
            }
        }
        if (!currentString.isEmpty()) {
            list.add(Component.literal(currentString.toString()).withStyle(ChatFormatting.GRAY));
        }
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }
    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 32;
    }
    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.EAT;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            MobEffectInstance effectInstance = player.getEffect(effect);
            MentalIllness mentalIllness = MentalStatus.getMentalStatusByServerPlayer(player).mentalIllness;
            if ((effectInstance != null && !effectInstance.endsWithin(duration/2))
                    || mentalIllness.medicineDelay.containsKey(id)) { //如果私自加量服药（效果剩余时间大于持续时间的一半）
                if (itemStack.isEdible()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), player.getEatingSound(itemStack), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    player.gameEvent(GameEvent.EAT);
                }
                if (mentalIllness.odCount < 3) {
                    mentalIllness.odCount++;
                }
                switch (mentalIllness.odCount) {
                    case 1:
                        break;
                    case 3:
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                    case 2:
                        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 19));
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
                        break;
                    default:
                        break;
                }
            }
            else {
                mentalIllness.odCount = 0;
                if (minDelay > 0) {
                    mentalIllness.medicineDelay.put(id, random.nextInt(maxDelay - minDelay) + minDelay);
                } else {
                    player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
                }
            }
            ActionbarHintPacket.sendOverdosePacket(player, mentalIllness.odCount);
        }
        itemStack.shrink(1);
        return itemStack;
    }
}
