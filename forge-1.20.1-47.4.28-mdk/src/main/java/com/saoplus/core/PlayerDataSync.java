package com.saoplus.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Coalesces data changes: services mark a player dirty and one snapshot per player is sent at the
 * end of the server tick, however many changes happened during it. Server thread only.
 */
@Mod.EventBusSubscriber(modid = SaoPlus.MODID)
public final class PlayerDataSync
{
    private static final Set<UUID> DIRTY = new LinkedHashSet<>();

    private PlayerDataSync()
    {
    }

    public static void markDirty(ServerPlayer player)
    {
        DIRTY.add(player.getUUID());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || DIRTY.isEmpty())
        {
            return;
        }
        List<UUID> pending = new ArrayList<>(DIRTY);
        DIRTY.clear();
        for (UUID id : pending)
        {
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(id);
            if (player != null)
            {
                ModNetwork.syncPlayerData(player);
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        DIRTY.remove(event.getEntity().getUUID());
    }
}
