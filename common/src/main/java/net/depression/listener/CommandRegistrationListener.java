package net.depression.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.depression.mental.MentalStatus;
import net.depression.mental.MentalTrait;
import net.depression.network.MentalStatusPacket;
import net.depression.server.Registry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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
                                                source.sendSuccess(player.getName().copy().append(Component.translatable("commands.depression.emotion.query")).append(""+mentalStatus.emotionValue), false);
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
                                                source.sendSuccess(player.getName().copy().append(Component.translatable("commands.depression.mentalhealth.query")).append("" + mentalStatus.mentalHealthValue), false);
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
    }
}
