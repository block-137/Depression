package net.depression.mixin.rhythmcraft;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.BaseCommandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BaseCommandBlock.class)
public abstract class BaseCommandBlockMixin {
    @ModifyArg(method = "performCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performPrefixedCommand(Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)I"), index = 0)
    private CommandSourceStack makeCommandSilent(CommandSourceStack commandSourceStack) {
        return commandSourceStack.withSuppressedOutput();
    }
}
