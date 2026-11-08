package com.example.examplemod;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;

public final class ExperienceManager
{
    private ExperienceManager()
    {
    }

    public static int getExperience(Player player)
    {
        return getPlayerData(player).getExperience();
    }

    public static boolean addExperience(ServerPlayer player, int amount)
    {
        if (amount <= 0)
        {
            return false;
        }

        PlayerData data = getPlayerData(player);
        LevelManager.processLevelUps(player, data);
        if (data.getLevel() >= PlayerData.MAX_LEVEL)
        {
            player.sendSystemMessage(Component.literal("Ya has alcanzado el nivel máximo."));
            return false;
        }

        int acceptedAmount = (int) Math.min((long) amount, LevelManager.getExperienceUntilMaxLevel(data));
        if (acceptedAmount <= 0)
        {
            return false;
        }

        data.setExperience((int) Math.min(Integer.MAX_VALUE, (long) data.getExperience() + acceptedAmount));
        LevelManager.processLevelUps(player, data);
        ModNetwork.syncPlayerData(player);

        player.sendSystemMessage(Component.literal("Has obtenido " + acceptedAmount + " EXP."));
        return true;
    }

    public static boolean setExperience(ServerPlayer player, int amount)
    {
        if (amount < 0)
        {
            return false;
        }

        PlayerData data = getPlayerData(player);
        data.setExperience(amount);
        LevelManager.processLevelUps(player, data);
        ModNetwork.syncPlayerData(player);

        player.sendSystemMessage(Component.literal("Tu progreso de EXP se ha establecido en "
                + data.getExperience() + "."));
        return true;
    }

    private static PlayerData getPlayerData(Player player)
    {
        return player.getCapability(PlayerDataCapability.PLAYER_DATA)
                .orElseThrow(() -> new IllegalStateException("Player data capability is missing"));
    }
}
