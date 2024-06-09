package net.depression.item;

import net.depression.client.ClientActionbarHint;
import net.depression.client.ClientDiaryUpdater;
import net.depression.server.Registry;
import net.depression.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DiaryItem extends WrittenBookItem {

    public DiaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        long todayTime = level.getDayTime() % 24000L;
        if (!compoundTag.contains("last_write_time")) { //如果这本日记没有写过
            if (todayTime >= 12544L) { //如果已经到了晚上，则为日记加上Tag并更新日记
                compoundTag.putString("title", Component.translatable("item.depression.diary").getString());
                compoundTag.putString("author", player.getName().getString());
                compoundTag.putLong("last_write_time", level.getDayTime());
                itemStack.setTag(compoundTag);
                if (!level.isClientSide()) {
                    Registry.diaryUpdate((ServerPlayer) player, itemStack);
                }
                else {
                    player.playSound(ModSounds.WRITE_DIARY.get()); //播放音效
                    ClientDiaryUpdater.setInfo(this, level, player, interactionHand);
                }
            }
            else { //如果还没到晚上，则发送提示
                if (level.isClientSide()) {
                    ClientActionbarHint.displayTranslatable(ClientActionbarHint.diaryUnwrittenHint);
                }
            }
        }
        else { //如果这本日记写过
            long lastWriteTime = compoundTag.getLong("last_write_time");
            if (todayTime >= 12544L && level.getDayTime() - lastWriteTime >= 12544) { //如果已经到了晚上且距离上次写的时间 >= 12544 tick，则为日记加上Tag并更新日记
                compoundTag.putLong("last_write_time", level.getDayTime());
                itemStack.setTag(compoundTag);
                if (!level.isClientSide()) {
                    Registry.diaryUpdate((ServerPlayer) player, itemStack);
                }
                else {
                    player.playSound(ModSounds.WRITE_DIARY.get()); //播放音效
                    ClientDiaryUpdater.setInfo(this, level, player, interactionHand);
                }
            }
            else { //如果还没到晚上，则打开日记
                if (level.isClientSide()) {
                    return super.use(level, player, interactionHand);
                }
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }
    @Override
    public boolean isFoil(ItemStack itemStack) {
        return itemStack.isEnchanted();
    }
}
