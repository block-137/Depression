package net.depression.mixin.rhythmcraft;

import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PrimaryLevelData.class)
public interface PrimaryLevelDataAccessor {
    @Invoker("setGameTime")
    void invokeSetGameTime(long gameTime);
}
