package net.depression.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.sun.jdi.connect.Connector;
import net.depression.mental.MentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.network.MentalStatusPacket;
import net.depression.network.RhythmCraftPacket;
import net.depression.rhythmcraft.PlayingChart;
import net.depression.server.Registry;
import net.depression.world.ParticleFormulaInstance;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.Random;

public class CommandRegistrationListener {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        LiteralCommandNode<CommandSourceStack> emotion = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("emotion")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes((arg) -> {
                                                    CommandSourceStack source = arg.getSource();
                                                    double value = DoubleArgumentType.getDouble(arg.copyFor(source), "value");
                                                    if (value < -20d || value > 20d) {
                                                        source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                    }
                                                    else {
                                                        for (ServerPlayer player : EntityArgument.getPlayers(arg.copyFor(source), "players")) {
                                                            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                                                            if (mentalStatus == null) {
                                                                source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                                return 0;
                                                            }
                                                            mentalStatus.emotionValue = value;
                                                            MentalStatusPacket.sendToPlayer(player, mentalStatus);
                                                        }
                                                    }
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes((arg) -> {
                                                    CommandSourceStack source = arg.getSource();
                                                    double value = DoubleArgumentType.getDouble(arg.copyFor(source), "value");
                                                    if (value < -20d || value > 20d) {
                                                        source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                    }
                                                    else {
                                                        for (ServerPlayer player : EntityArgument.getPlayers(arg.copyFor(source), "players")) {
                                                            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                                                            if (mentalStatus == null) {
                                                                source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                                return 0;
                                                            }
                                                            mentalStatus.emotionValue += value;
                                                            MentalStatusPacket.sendToPlayer(player, mentalStatus);
                                                        }
                                                    }
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("query")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes((arg) -> {
                                            CommandSourceStack source = arg.getSource();
                                            for (ServerPlayer player : EntityArgument.getPlayers(arg.copyFor(source), "players")) {
                                                MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                                                if (mentalStatus == null) {
                                                    source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                    return 0;
                                                }
                                                source.sendSuccess(() -> player.getName().copy().append(Component.translatable("commands.depression.emotion.query")).append(""+mentalStatus.emotionValue), false);
                                            }
                                            return 0;
                                        })
                                )
                        )

        );

        LiteralCommandNode<CommandSourceStack> mentalHealth = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("mentalhealth")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes((arg) -> {
                                                    CommandSourceStack source = arg.getSource();
                                                    double value = DoubleArgumentType.getDouble(arg.copyFor(source), "value");
                                                    if (value < 0d || value > 100d) {
                                                        source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                    }
                                                    else {
                                                        for (ServerPlayer player : EntityArgument.getPlayers(arg.copyFor(source), "players")) {
                                                            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                                                            if (mentalStatus == null) {
                                                                source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                                return 0;
                                                            }
                                                            mentalStatus.mentalHealthValue = value;
                                                            MentalStatusPacket.sendToPlayer(player, mentalStatus);
                                                        }
                                                    }
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes((arg) -> {
                                                    CommandSourceStack source = arg.getSource();
                                                    double value = DoubleArgumentType.getDouble(arg.copyFor(source), "value");
                                                    if (value < 0d || value > 100d) {
                                                        source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                    }
                                                    else {
                                                        for (ServerPlayer player : EntityArgument.getPlayers(arg.copyFor(source), "players")) {
                                                            MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                                                            if (mentalStatus == null) {
                                                                source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                                return 0;
                                                            }
                                                            mentalStatus.mentalHealthValue += value;
                                                            MentalStatusPacket.sendToPlayer(player, mentalStatus);
                                                        }
                                                    }
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("query")
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes((arg) -> {
                                            CommandSourceStack source = arg.getSource();
                                            for (ServerPlayer player : EntityArgument.getPlayers(arg.copyFor(source), "players")) {
                                                MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                                                if (mentalStatus == null) {
                                                    source.sendFailure(Component.translatable("commands.data.get.invalid"));
                                                    return 0;
                                                }
                                                source.sendSuccess(() -> player.getName().copy().append(Component.translatable("commands.depression.mentalhealth.query")).append("" + mentalStatus.mentalHealthValue), false);
                                            }
                                            return 0;
                                        })
                                )
                        )
        );

        LiteralCommandNode<CommandSourceStack> switchTrait = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("switchtrait")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("trait", StringArgumentType.string())
                                        .executes((arg) -> {
                                            CommandSourceStack source = arg.getSource();
                                            MentalTrait mentalTrait = MentalTrait.byId(StringArgumentType.getString(arg.copyFor(source), "trait"));
                                            if (mentalTrait == null) {
                                                source.sendFailure(Component.translatable("argument.id.unknown"));
                                                return 0;
                                            }
                                            for (ServerPlayer player : EntityArgument.getPlayers(arg.copyFor(source), "players")) {
                                                MentalStatus mentalStatus = Registry.mentalStatus.get(player.getUUID());
                                                if (mentalStatus == null) {
                                                    source.sendFailure(Component.translatable("argument.player.unknown"));
                                                    return 0;
                                                }
                                                mentalStatus.loadMentalTrait(mentalTrait);
                                            }
                                            return 0;
                                        })
                                )
                        )
        );

        LiteralCommandNode<CommandSourceStack> setSpace = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("setspace")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("time", IntegerArgumentType.integer())
                                .executes((arg) -> {
                                    RhythmCraftPacket.sendSpaceChange(arg.getSource().getPlayer(), arg.getArgument("time", Integer.class));
                                    return 0;
                                })
                        )
        );

        LiteralCommandNode<CommandSourceStack> setSpeed = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("setspeed")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("speed", DoubleArgumentType.doubleArg())
                                .executes((arg) -> {
                                    CommandSourceStack source = arg.getSource();
                                    ServerPlayer player = source.getPlayer();
                                    PlayingChart playingChart = PlayingChart.playingCharts.get(player.getUUID());
                                    if (playingChart == null) {
                                        source.sendFailure(Component.translatable("commands.depression.rhythmcraft.not_in_game"));
                                        return 0;
                                    }
                                    if (!playingChart.isEditMode) {
                                        source.sendFailure(Component.translatable("commands.depression.rhythmcraft.not_edit_mode"));
                                        return 0;
                                    }
                                    playingChart.chart.speedMap.put(playingChart.tickCount, DoubleArgumentType.getDouble(arg.copyFor(source), "speed"));
                                    playingChart.chart.isEdited = true;
                                    playingChart.integrate(playingChart.tickCount);
                                    source.sendSuccess(() -> Component.translatable("commands.depression.rhythmcraft.set_successful"), true);
                                    return 0;
                                })
                        )
        );

        LiteralCommandNode<CommandSourceStack> setTime = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("settime")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("time", IntegerArgumentType.integer())
                                .executes((arg) -> {
                                    CommandSourceStack source = arg.getSource();
                                    ServerPlayer player = source.getPlayer();
                                    PlayingChart playingChart = PlayingChart.playingCharts.get(player.getUUID());
                                    if (playingChart == null) {
                                        source.sendFailure(Component.translatable("commands.depression.rhythmcraft.not_in_game"));
                                        return 0;
                                    }
                                    if (!playingChart.isEditMode) {
                                        source.sendFailure(Component.translatable("commands.depression.rhythmcraft.not_edit_mode"));
                                        return 0;
                                    }
                                    playingChart.chart.timeMap.put(playingChart.tickCount, (long) IntegerArgumentType.getInteger(arg.copyFor(source), "time"));
                                    playingChart.chart.isEdited = true;
                                    source.sendSuccess(() -> Component.translatable("commands.depression.rhythmcraft.set_successful"), true);
                                    return 0;
                                })
                        )
        );

        LiteralCommandNode<CommandSourceStack> exParticle = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("exparticle")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("name", ParticleArgument.particle(context))
                                .then(Commands.argument("x", StringArgumentType.string())
                                        .then(Commands.argument("y", StringArgumentType.string())
                                                .then(Commands.argument("z", StringArgumentType.string())
                                                        .then(Commands.argument("deltaX", StringArgumentType.string())
                                                                .then(Commands.argument("deltaY", StringArgumentType.string())
                                                                        .then(Commands.argument("deltaZ", StringArgumentType.string())
                                                                                .then(Commands.argument("l", DoubleArgumentType.doubleArg())
                                                                                        .then(Commands.argument("r", DoubleArgumentType.doubleArg())
                                                                                                .then(Commands.argument("speed", DoubleArgumentType.doubleArg())
                                                                                                        .then(Commands.argument("density", DoubleArgumentType.doubleArg())
                                                                                                                .executes((arg) -> executeParticle(arg, false))
                                                                                                                .then(Commands.argument("instant", StringArgumentType.string())
                                                                                                                        .executes((arg) -> executeParticle(arg, true))
                                                                                                                )
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );


        LiteralCommandNode<CommandSourceStack> executeCb = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("executecb")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("delay", IntegerArgumentType.integer())
                                        .executes((arg) -> {
                                            CommandSourceStack source = arg.getSource();
                                            ServerLevel level = source.getLevel();
                                            BlockPos blockPos = BlockPosArgument.getLoadedBlockPos(arg, "pos");
                                            BlockEntity blockEntity = level.getBlockEntity(blockPos);
                                            if (blockEntity instanceof CommandBlockEntity) {
                                                int delay = IntegerArgumentType.getInteger(arg, "delay");
                                                level.scheduleTick(blockPos, blockEntity.getBlockState().getBlock(), delay);
                                                return 0;
                                            }
                                            else {
                                                return 1;
                                            }
                                        })
                                )
                        )
        );

        LiteralCommandNode<CommandSourceStack> randomlyReplaceBlock = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("randomlyreplaceblock")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("pos1", BlockPosArgument.blockPos())
                                .then(Commands.argument("pos2", BlockPosArgument.blockPos())
                                        .then(Commands.argument("block1", BlockStateArgument.block(context))
                                                .then(Commands.argument("block2", BlockStateArgument.block(context))
                                                        .then(Commands.argument("chance", DoubleArgumentType.doubleArg())
                                                                .executes((arg) -> {
                                                                    Random random = new Random();
                                                                    CommandSourceStack source = arg.getSource();
                                                                    ServerLevel level = source.getLevel();
                                                                    BlockPos pos1 = BlockPosArgument.getLoadedBlockPos(arg, "pos1");
                                                                    BlockPos pos2 = BlockPosArgument.getLoadedBlockPos(arg, "pos2");
                                                                    BlockState block1 = BlockStateArgument.getBlock(arg, "block1").getState();
                                                                    BlockState block2 = BlockStateArgument.getBlock(arg, "block2").getState();
                                                                    double chance = DoubleArgumentType.getDouble(arg, "chance");
                                                                    BlockState block3 = null;
                                                                    if (block2.getBlock() instanceof DoublePlantBlock) {
                                                                        block3 = block2.getBlock().withPropertiesOf(block2).setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                                                                    }
                                                                    for (int x = Math.min(pos1.getX(), pos2.getX()); x <= Math.max(pos1.getX(), pos2.getX()); x++) {
                                                                        for (int y = Math.min(pos1.getY(), pos2.getY()); y <= Math.max(pos1.getY(), pos2.getY()); y++) {
                                                                            for (int z = Math.min(pos1.getZ(), pos2.getZ()); z <= Math.max(pos1.getZ(), pos2.getZ()); z++) {
                                                                                BlockPos pos = new BlockPos(x, y, z);
                                                                                BlockState state = level.getBlockState(pos);
                                                                                if (state.is(block1.getBlock())) {
                                                                                    if (block3 != null && !level.getBlockState(pos.above()).is(block1.getBlock())) {
                                                                                        continue;
                                                                                    }
                                                                                    if (random.nextDouble() < chance) {
                                                                                        level.setBlock(pos, block2, 3);
                                                                                        if (block3 != null) {
                                                                                            level.setBlock(pos.above(), block3, 3);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    return 0;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
        );

        LiteralCommandNode<CommandSourceStack> placeTrapezoid = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("placetrapezoid")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("up_edge", IntegerArgumentType.integer())
                                .then(Commands.argument("height", IntegerArgumentType.integer())
                                        .then(Commands.argument("block1", BlockStateArgument.block(context))
                                                .then(Commands.argument("block2", BlockStateArgument.block(context))
                                                        .then(Commands.argument("block3", BlockStateArgument.block(context))
                                                                .executes((arg) -> {
                                                                    Random random = new Random();
                                                                    CommandSourceStack source = arg.getSource();
                                                                    ServerLevel level = source.getLevel();
                                                                    int upEdge = IntegerArgumentType.getInteger(arg, "up_edge");
                                                                    int height = IntegerArgumentType.getInteger(arg, "height");
                                                                    BlockState block1 = BlockStateArgument.getBlock(arg, "block1").getState();
                                                                    BlockState block2 = BlockStateArgument.getBlock(arg, "block2").getState();
                                                                    BlockState block3 = BlockStateArgument.getBlock(arg, "block3").getState();
                                                                    ServerPlayer player = source.getPlayer();
                                                                    if (player == null) {
                                                                        return 1;
                                                                    }
                                                                    BlockPos pos = player.getOnPos();
                                                                    for (int x = 0; x < height; x++) {
                                                                        int halfEdge = (upEdge + x) / 2;
                                                                        double ratio = (double) x / height;
                                                                        for (int z = pos.getZ() - halfEdge; z <= pos.getZ() + halfEdge; z++) {
                                                                            level.setBlock(new BlockPos(x + pos.getX(), pos.getY(), z), block3, 3);
                                                                            level.setBlock(new BlockPos(x + pos.getX(), pos.getY() + 1, z), random.nextDouble() < ratio ? block2 : block1, 3);
                                                                        }
                                                                    }
                                                                    return 0;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
        );

        LiteralCommandNode<CommandSourceStack> drawCube = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("drawcube")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("particle", ParticleArgument.particle(context))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("density", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("delta", Vec3Argument.vec3(false))
                                                        .executes((arg) -> {
                                                            CommandSourceStack source = arg.getSource();
                                                            ServerLevel level = source.getLevel();
                                                            ParticleOptions particleOptions = ParticleArgument.getParticle(arg, "particle");
                                                            BlockPos pos = BlockPosArgument.getLoadedBlockPos(arg, "pos");
                                                            double density = DoubleArgumentType.getDouble(arg, "density");
                                                            Vec3 delta = Vec3Argument.getVec3(arg, "delta");
                                                            for (double x = pos.getX(); x < pos.getX() + 1d; x += density) {
                                                                level.sendParticles(particleOptions, x, pos.getY(), pos.getZ(), 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, x, pos.getY() + 1d, pos.getZ(), 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, x, pos.getY(), pos.getZ() + 1d, 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, x, pos.getY() + 1d, pos.getZ() + 1d, 0, delta.x, delta.y, delta.z, delta.length());
                                                            }
                                                            for (double y = pos.getY(); y < pos.getY() + 1d; y += density) {
                                                                level.sendParticles(particleOptions, pos.getX(), y, pos.getZ(), 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, pos.getX() + 1d, y, pos.getZ(), 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, pos.getX(), y, pos.getZ() + 1d, 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, pos.getX() + 1d, y, pos.getZ() + 1d, 0, delta.x, delta.y, delta.z, delta.length());
                                                            }
                                                            for (double z = pos.getZ(); z < pos.getZ() + 1d; z += density) {
                                                                level.sendParticles(particleOptions, pos.getX(), pos.getY(), z, 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, pos.getX() + 1d, pos.getY(), z, 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, pos.getX(), pos.getY() + 1d, z, 0, delta.x, delta.y, delta.z, delta.length());
                                                                level.sendParticles(particleOptions, pos.getX() + 1d, pos.getY() + 1d, z, 0, delta.x, delta.y, delta.z, delta.length());
                                                            }
                                                            return 0;
                                                        })
                                                )
                                        )

                                )
                        )
        );

        LiteralCommandNode<CommandSourceStack> drawHeart = dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("drawheart")
                        .requires((req) -> req.hasPermission(2))
                        .then(Commands.argument("particle", ParticleArgument.particle(context))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("direction", StringArgumentType.string())
                                                .then(Commands.argument("density", DoubleArgumentType.doubleArg())
                                                        .then(Commands.argument("delta", Vec3Argument.vec3(false))
                                                                .executes((arg) -> {
                                                                    CommandSourceStack source = arg.getSource();
                                                                    ServerLevel level = source.getLevel();
                                                                    ParticleOptions particleOptions = ParticleArgument.getParticle(arg, "particle");
                                                                    Vec3 pos = BlockPosArgument.getLoadedBlockPos(arg, "pos").getCenter();
                                                                    Direction direction = Direction.byName(StringArgumentType.getString(arg, "direction"));
                                                                    if (direction == null) {
                                                                        return 1;
                                                                    }
                                                                    double density = DoubleArgumentType.getDouble(arg, "density");
                                                                    Vec3 delta = Vec3Argument.getVec3(arg, "delta");
                                                                    for (double t = 0; t < 2 * Math.PI; t += density) {
                                                                        double x = 16 * Math.pow(Math.sin(t), 3) / 32d;
                                                                        double y = (13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t)) / 32d;
                                                                        switch (direction) {
                                                                            case NORTH: case SOUTH:
                                                                                level.sendParticles(particleOptions, pos.x + x, pos.y + y, pos.z, 0, delta.x, delta.y, delta.z, delta.length());
                                                                                break;
                                                                            case WEST: case EAST:
                                                                                level.sendParticles(particleOptions, pos.x, pos.y + y, pos.z + x, 0, delta.x, delta.y, delta.z, delta.length());
                                                                                break;
                                                                        }
                                                                    }
                                                                    return 0;
                                                                })
                                                        )
                                                        .then(Commands.argument("blast_radius", DoubleArgumentType.doubleArg())
                                                                .executes((arg) -> {
                                                                    CommandSourceStack source = arg.getSource();
                                                                    ServerLevel level = source.getLevel();
                                                                    ParticleOptions particleOptions = ParticleArgument.getParticle(arg, "particle");
                                                                    Vec3 pos = BlockPosArgument.getLoadedBlockPos(arg, "pos").getCenter();
                                                                    Direction direction = Direction.byName(StringArgumentType.getString(arg, "direction"));
                                                                    if (direction == null) {
                                                                        return 1;
                                                                    }
                                                                    double density = DoubleArgumentType.getDouble(arg, "density");
                                                                    double radius = DoubleArgumentType.getDouble(arg, "blast_radius");
                                                                    double sqrt2 = Math.sqrt(2);
                                                                    for (double t = 0; t < 2 * Math.PI; t += density) {
                                                                        double x = 16 * Math.pow(Math.sin(t), 3) / 32d;
                                                                        double y = (13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t)) / 32d;
                                                                        switch (direction) {
                                                                            case NORTH: case SOUTH:
                                                                                level.sendParticles(particleOptions, pos.x, pos.y, pos.z, 0, radius * x, radius * y, 0, sqrt2 * radius);
                                                                                break;
                                                                            case WEST: case EAST:
                                                                                level.sendParticles(particleOptions, pos.x, pos.y, pos.z, 0, 0, radius * y, radius * x, sqrt2 * radius);
                                                                                break;
                                                                        }
                                                                    }
                                                                    return 0;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    public static int executeParticle(CommandContext<CommandSourceStack> arg, boolean isInstant) {
        CommandSourceStack source = arg.getSource();
        ServerLevel level = source.getLevel();
        ParticleOptions particleOptions = ParticleArgument.getParticle(arg, "name");
        String x = StringArgumentType.getString(arg, "x");
        String y = StringArgumentType.getString(arg, "y");
        String z = StringArgumentType.getString(arg, "z");
        String deltaX = StringArgumentType.getString(arg, "deltaX");
        String deltaY = StringArgumentType.getString(arg, "deltaY");
        String deltaZ = StringArgumentType.getString(arg, "deltaZ");
        double l = DoubleArgumentType.getDouble(arg, "l");
        double r = DoubleArgumentType.getDouble(arg, "r");
        double speed = DoubleArgumentType.getDouble(arg, "speed");
        double density = DoubleArgumentType.getDouble(arg, "density");
        ParticleFormulaInstance instance = new ParticleFormulaInstance(level, particleOptions, x, y, z, deltaX, deltaY, deltaZ, l, r, speed, density, isInstant);
        if (level instanceof PlayingChart playingChart) {
            Registry.particles.computeIfAbsent(playingChart.player.getStringUUID(), k -> new LinkedList<>()).add(instance);
        }
        else {
            Registry.particles.computeIfAbsent(level.dimensionTypeId().location().toString(), k -> new LinkedList<>()).add(instance);
        }
        return 0;
    }
}
