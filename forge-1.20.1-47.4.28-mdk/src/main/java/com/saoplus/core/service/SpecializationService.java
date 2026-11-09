package com.saoplus.core.service;

import com.saoplus.core.PlayerData;
import com.saoplus.core.PlayerDataCapability;
import com.saoplus.core.PlayerDataSync;
import com.saoplus.core.SaoPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

/**
 * Server-authoritative specialization switching (GDD 6.3): one active branch per family, previous
 * branch progress is kept, re-selecting a branch restores it. Limits and consequences of switching
 * are not defined by the GDD; they can be added by cancelling {@link SpecializationChangedEvent}.
 */
public final class SpecializationService
{
    private SpecializationService()
    {
    }

    /**
     * Activates {@code branch} for {@code family}. Returns false when it is already active, when
     * a listener cancelled the change, or when the player's data is unavailable.
     */
    public static boolean select(ServerPlayer player, ResourceLocation family, ResourceLocation branch)
    {
        Optional<PlayerData> data = PlayerDataCapability.get(player);
        if (data.isEmpty())
        {
            SaoPlus.LOGGER.warn("Ignored a specialization change for {}: player data capability is missing",
                    player.getGameProfile().getName());
            return false;
        }

        Optional<ResourceLocation> previous = data.get().getSpecialization().getActive(family);
        if (previous.filter(branch::equals).isPresent())
        {
            return false;
        }
        SpecializationChangedEvent event =
                new SpecializationChangedEvent(player, family, previous.orElse(null), branch);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }

        data.get().getSpecialization().select(family, branch);
        PlayerDataSync.markDirty(player);
        return true;
    }
}
