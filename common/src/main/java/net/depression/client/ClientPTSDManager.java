package net.depression.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import net.depression.Depression;
import net.depression.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ClientPTSDManager {
    public long pausedTime;
    public long startTick;
    public Long startTime;
    public static int onsetLevel;
    public int heartBeatTick;
    public double heartBeatVolume;
    public TinnitusSoundInstance tinnitusSound;
    private final Random random = new Random();
    public static ConcurrentHashMap<String, ConcurrentLinkedDeque<Pair<Entity, Long>>> falseEntities = new ConcurrentHashMap<>();

    public static final ResourceLocation PTSD_ONSET_LEFT = new ResourceLocation(Depression.MOD_ID, "textures/symptom/ptsd_onset_left.png");
    public static final ResourceLocation PTSD_ONSET_RIGHT = new ResourceLocation(Depression.MOD_ID, "textures/symptom/ptsd_onset_right.png");
    public static final ResourceLocation PTSD_ONSET_UP = new ResourceLocation(Depression.MOD_ID, "textures/symptom/ptsd_onset_up.png");
    public void receivePTSDOnsetPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        int level = buf.readInt();
        if (level > 0) {
            double distance = buf.readDouble();
            heartBeatTick = (int) (10 + (distance / 24d * 10));
            heartBeatVolume = 0.5d + (24d - distance) / 24d * 0.5d;
        }

        if (level > 0 && onsetLevel == 0) { //PTSD发作
            Level gameLevel = Minecraft.getInstance().level;
            if (gameLevel != null) {
                startTick = Minecraft.getInstance().level.getGameTime();
            }
            String translatable = "message.depression.ptsd_onset_" + level + "_" + random.nextInt(3);
            Minecraft.getInstance().gui.setOverlayMessage(Component.translatable(translatable), false);
        }
        if (level > 1 && onsetLevel <= 1) { //PTSD加重到2级
            startTinnitus();
            startTime = new Date().getTime();
        }
        if (level == 0 && onsetLevel > 0) { //PTSD消散
            startTick = -1;
        }
        if (level < 2 && onsetLevel >= 2) { //PTSD减轻到1级及以下
            stopTinnitus();
            startTime = null;
        }
        onsetLevel = level;
    }

    public void clear() {
        onsetLevel = 0;
        startTime = null;
        startTick = -1;
        pausedTime = 0;
        heartBeatTick = 20;
        heartBeatVolume = 0.5;
        falseEntities.clear();
    }

    public void render(PoseStack poseStack, int x, int y) {
        int a = Math.min((int) (x * 0.2), 170);
        int b = Math.min((int) (y * 0.2), 170);
        if (startTime != null) {
            long curTime = new Date().getTime();
            if (onsetLevel >= 4) {
                int xOffset = curTime - startTime > 2000 ? 0 : -a + (int) (a * (curTime - startTime) / 2000);
                int yOffset = curTime - startTime > 2000 ? 0 : -b + (int) (b * (curTime - startTime) / 2000);
                RenderSystem.setShaderTexture(0, PTSD_ONSET_LEFT);
                GuiComponent.blit(poseStack, xOffset, 0, 90, 170 - a, 0, a, y, 480, 360);
                RenderSystem.setShaderTexture(0, PTSD_ONSET_RIGHT);
                GuiComponent.blit(poseStack, x - a - xOffset, 0, 90, 310, 0, a, y, 480, 360);
                RenderSystem.setShaderTexture(0, PTSD_ONSET_UP);
                GuiComponent.blit(poseStack, 0, yOffset, 90, 0, 170 - b, x, b, 480, 360);
            }
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (minecraft.isPaused()) {
                pausedTime = curTime;
            }
            else {
                if (pausedTime != 0) {
                    startTime = curTime - pausedTime + startTime;
                    pausedTime = 0;
                }

                float xRot = player.getXRot();
                xRot += (float) (Math.sin(2 * Math.PI / 1000f * (curTime - startTime)) * 0.025f); //add即积分，对本函数积分得 f(x) = -cos2 pi t.
                player.setXRot(xRot);
            }
        }
    }


    public void startTinnitus() {
        if (tinnitusSound == null) {
            tinnitusSound = new TinnitusSoundInstance();
            Minecraft.getInstance().getSoundManager().play(tinnitusSound);
        }
    }

    public void stopTinnitus() {
        if (tinnitusSound != null) {
            tinnitusSound.stop();
            tinnitusSound = null;
        }
    }

    public static void receivePhotismPacket(FriendlyByteBuf buf, NetworkManager.PacketContext packetContext) {
        String id = buf.readUtf();
        EntityType.byString(id).ifPresent(entityType -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null) {
                return;
            }
            Player player = minecraft.player;
            Vec3 eyePos = player.getEyePosition();
            Vec3 viewVec = player.getViewVector(1.0F);
            viewVec.normalize();
            Vec3 rayTraceEnd = eyePos.add(viewVec.scale(5.0d));
            BlockHitResult hitResult = minecraft.level.clip(new ClipContext(eyePos, rayTraceEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            Vec3 spawnPoint;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = hitResult.getBlockPos();
                BlockState blockState = level.getBlockState(blockPos);
                if (!blockState.getCollisionShape(level, blockPos).isEmpty()) {
                    blockPos = blockPos.relative(hitResult.getDirection());
                }
                spawnPoint = new Vec3(blockPos.getX() + 0.5d, blockPos.getY(), blockPos.getZ() + 0.5d);
            }
            else {
                spawnPoint = rayTraceEnd;
            }
            Entity entity = entityType.create(level);
            entity.setPos(spawnPoint);
            entity.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition());
            if (entity instanceof Mob) {
                Mob mob = (Mob) entity;
                mob.setTarget(player);
                mob.getLookControl().setLookAt(player, 0f, 0f);
                mob.getMoveControl().setWantedPosition(player.getX(), player.getY(), player.getZ(), 0d);
            }
            falseEntities.computeIfAbsent(level.dimensionTypeId().location().toString(), key -> new ConcurrentLinkedDeque<>())
                    .add(new Pair<>(entity, level.getGameTime()));
        });
    }

    public static class TinnitusSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
        public boolean stopped;
        public TinnitusSoundInstance() {
            super(ModSounds.TINNITUS.get(), SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
            this.looping = true;
            this.delay = 0;
            this.volume = 0.25F;
            this.relative = false;
        }

        public void stop() {
            stopped = true;
            looping = false;
        }

        @Override
        public boolean isStopped() {
            return stopped;
        }

        @Override
        public void tick() {
        }
    }
}
