package com.example.examplemod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public final class SaoRpgCommands
{
    private SaoRpgCommands()
    {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
        var addExperience = Commands.literal("add")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("cantidad", IntegerArgumentType.integer(1))
                        .executes(context -> addExperience(context.getSource(),
                                IntegerArgumentType.getInteger(context, "cantidad"))));
        var setExperience = Commands.literal("set")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("cantidad", IntegerArgumentType.integer(0))
                        .executes(context -> setExperience(context.getSource(),
                                IntegerArgumentType.getInteger(context, "cantidad"))));
        var setLevel = Commands.literal("set")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("nivel", IntegerArgumentType.integer(1, PlayerData.MAX_LEVEL))
                        .executes(context -> setLevel(context.getSource(),
                                IntegerArgumentType.getInteger(context, "nivel"))));

        event.getDispatcher().register(Commands.literal("saorpg")
                .then(Commands.literal("stats")
                        .executes(context -> showStats(context.getSource())))
                .then(Commands.literal("exp")
                        .then(addExperience)
                        .then(setExperience))
                .then(Commands.literal("level")
                        .then(setLevel)));
    }

    private static int showStats(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException
    {
        ServerPlayer player = source.getPlayerOrException();
        PlayerData data = getPlayerData(player);
        String nextLevel = data.getLevel() >= PlayerData.MAX_LEVEL
                ? "MAX"
                : data.getExperience() + " / " + LevelManager.getExperienceRequired(data.getLevel());

        source.sendSuccess(() -> Component.literal(
                "SAO+ RPG | Nivel: " + data.getLevel()
                        + " | EXP: " + nextLevel
                        + " | Fuerza: " + data.getStrength()
                        + " | Agilidad: " + data.getAgility()
                        + " | Vitalidad: " + data.getVitality()
                        + " | Inteligencia: " + data.getIntelligence()
                        + " | Puntos disponibles: " + data.getAvailablePoints()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int addExperience(CommandSourceStack source, int amount)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException
    {
        return ExperienceManager.addExperience(source.getPlayerOrException(), amount)
                ? Command.SINGLE_SUCCESS : 0;
    }

    private static int setExperience(CommandSourceStack source, int amount)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException
    {
        return ExperienceManager.setExperience(source.getPlayerOrException(), amount)
                ? Command.SINGLE_SUCCESS : 0;
    }

    private static int setLevel(CommandSourceStack source, int level)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException
    {
        return LevelManager.setLevel(source.getPlayerOrException(), level)
                ? Command.SINGLE_SUCCESS : 0;
    }

    private static PlayerData getPlayerData(ServerPlayer player)
    {
        return player.getCapability(PlayerDataCapability.PLAYER_DATA)
                .orElseThrow(() -> new IllegalStateException("Player data capability is missing"));
    }
}
