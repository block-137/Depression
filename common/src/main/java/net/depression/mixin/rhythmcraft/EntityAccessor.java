package net.depression.mixin.rhythmcraft;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("setSharedFlag")
    public void invokeSetSharedFlag(int flag, boolean value);
}
