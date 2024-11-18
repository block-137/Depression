package net.depression.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.depression.Depression;
import net.depression.client.ClientMentalStatus;
import net.depression.client.ClientPTSDManager;
import net.depression.client.DepressionClient;
import net.depression.mental.PTSDManager;
import net.depression.network.MentalTraitPacket;
import net.depression.network.PlaySoundPacket;
import net.depression.screen.MentalTraitInfoScreen;
import net.depression.screen.MentalTraitSelectionScreen;
import net.depression.sound.ModSounds;
import net.depression.util.TempValues;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
    @Unique
    private MentalTraitSelectionScreen selectionScreen;
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        if (!ClientMentalStatus.isMentalTraitSelected) {
            Minecraft minecraft = Minecraft.getInstance();
            if (selectionScreen == null) {
                selectionScreen = new MentalTraitSelectionScreen();
            }
            if (minecraft.screen != selectionScreen && !(minecraft.screen instanceof MentalTraitInfoScreen)) {
                minecraft.setScreen(selectionScreen);
            }
        }

        ClientPTSDManager ptsdManager = DepressionClient.clientMentalStatus.ptsdManager;
        Player player = Minecraft.getInstance().player;
        long curTick = ((ClientLevel) (Object) this).getGameTime();
        String dimensionID = Minecraft.getInstance().level.dimensionTypeId().location().toString();
        ArrayDeque<Pair<Entity, Long>> falseEntities = ClientPTSDManager.falseEntities.get(dimensionID);
        if (falseEntities != null) {
            falseEntities.removeIf(pair -> curTick - pair.getSecond() > 400);
        }
        switch (ptsdManager.onsetLevel) {
            case 4:
            case 3:
            case 2:
                if ((curTick - ptsdManager.startTick) % 20 == 0) {
                    player.playSound(ModSounds.PANT.get());
                }
            case 1:
                if (ptsdManager.onsetLevel <= 1 && (curTick - ptsdManager.startTick) % 20 == 0) {
                    player.playSound(ModSounds.PANT.get(), 0.5f, 1f);
                }
                if ((curTick - ptsdManager.startTick) % ptsdManager.heartBeatTick == 0) {
                    player.playSound(ModSounds.HEARTBEATS.get(), (float) ptsdManager.heartBeatVolume, 1f);
                }
                break;
            case 0:
                break;
        }
    }
    @Inject(method = "playLocalSound", at = @At("HEAD"))
    private void playLocalSound(double d, double e, double f, SoundEvent soundEvent, SoundSource soundSource, float g, float h, boolean bl, CallbackInfo ci) {
        String id = soundEvent.getLocation().toString();
        if (PTSDManager.soundEventMap.containsKey(id)) {
            PlaySoundPacket.sendToServer(id);
        }
    }
}
