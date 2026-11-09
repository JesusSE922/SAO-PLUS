package com.saoplus.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = SaoPlus.MODID)
public final class PlayerDataEvents
{
    private static final ResourceLocation PLAYER_DATA_ID =
            ResourceLocation.fromNamespaceAndPath(SaoPlus.MODID, "player_data");

    private PlayerDataEvents()
    {
    }

    @SubscribeEvent
    public static void attachPlayerData(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            PlayerDataProvider provider = new PlayerDataProvider();
            event.addCapability(PLAYER_DATA_ID, provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    public static void copyPlayerData(PlayerEvent.Clone event)
    {
        Player original = event.getOriginal();
        Player replacement = event.getEntity();
        boolean wasDeath = event.isWasDeath();

        if (wasDeath)
        {
            original.reviveCaps();
        }
        try
        {
            LazyOptional<PlayerData> oldData = original.getCapability(PlayerDataCapability.PLAYER_DATA);
            LazyOptional<PlayerData> newData = replacement.getCapability(PlayerDataCapability.PLAYER_DATA);
            Optional<PlayerData> originalData = oldData.resolve();
            Optional<PlayerData> replacementData = newData.resolve();
            if (originalData.isEmpty() || replacementData.isEmpty())
            {
                SaoPlus.LOGGER.error("Could not copy SAO+ player data during clone for {}: "
                                + "original capability present={}, replacement capability present={}",
                        original.getGameProfile().getName(), originalData.isPresent(), replacementData.isPresent());
                return;
            }

            replacementData.get().copyFrom(originalData.get());
        }
        finally
        {
            if (wasDeath)
            {
                original.invalidateCaps();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        syncPlayerData(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        syncPlayerData(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        syncPlayerData(event.getEntity());
    }

    private static void syncPlayerData(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            ModNetwork.syncPlayerData(serverPlayer);
        }
    }
}
