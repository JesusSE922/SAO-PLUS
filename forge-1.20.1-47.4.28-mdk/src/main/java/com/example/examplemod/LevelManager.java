package com.example.examplemod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class LevelManager
{
    private LevelManager()
    {
    }

    public static int getExperienceRequired(int currentLevel)
    {
        int boundedLevel = Math.max(1, Math.min(PlayerData.MAX_LEVEL, currentLevel));
        return 100 + 50 * (boundedLevel - 1);
    }

    public static boolean setLevel(ServerPlayer player, int level)
    {
        if (level < 1 || level > PlayerData.MAX_LEVEL)
        {
            return false;
        }

        PlayerData data = getPlayerData(player);
        data.setLevel(level);
        data.setExperience(0);
        ModNetwork.syncPlayerData(player);
        player.sendSystemMessage(Component.literal("Tu nivel se ha establecido en " + level + "."));
        return true;
    }

    static int processLevelUps(ServerPlayer player, PlayerData data)
    {
        int startingLevel = data.getLevel();
        long levelsGained = 0;

        while (data.getLevel() < PlayerData.MAX_LEVEL)
        {
            int requiredExperience = getExperienceRequired(data.getLevel());
            if (data.getExperience() < requiredExperience)
            {
                break;
            }

            data.setExperience(data.getExperience() - requiredExperience);
            data.setLevel(data.getLevel() + 1);
            levelsGained++;
        }

        if (data.getLevel() == PlayerData.MAX_LEVEL)
        {
            data.setExperience(0);
        }

        if (levelsGained > 0)
        {
            long pointsToGrant = levelsGained * PlayerData.ATTRIBUTE_POINTS_PER_LEVEL;
            int grantedPoints = (int) Math.min(pointsToGrant,
                    (long) Integer.MAX_VALUE - data.getAvailablePoints());
            data.setAvailablePoints(data.getAvailablePoints() + grantedPoints);

            player.sendSystemMessage(Component.literal("¡Has subido del nivel " + startingLevel
                    + " al nivel " + data.getLevel() + "!"));
            player.sendSystemMessage(Component.literal("Has recibido " + grantedPoints
                    + " puntos de atributos."));
        }

        return (int) levelsGained;
    }

    static long getExperienceUntilMaxLevel(PlayerData data)
    {
        long experienceNeeded = 0;
        for (int level = data.getLevel(); level < PlayerData.MAX_LEVEL; level++)
        {
            experienceNeeded += getExperienceRequired(level);
        }
        return Math.max(0, experienceNeeded - data.getExperience());
    }

    private static PlayerData getPlayerData(ServerPlayer player)
    {
        return player.getCapability(PlayerDataCapability.PLAYER_DATA)
                .orElseThrow(() -> new IllegalStateException("Player data capability is missing"));
    }
}
