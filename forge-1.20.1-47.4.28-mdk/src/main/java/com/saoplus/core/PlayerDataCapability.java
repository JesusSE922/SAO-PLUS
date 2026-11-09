package com.saoplus.core;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = SaoPlus.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PlayerDataCapability
{
    public static final Capability<PlayerData> PLAYER_DATA =
            CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerDataCapability()
    {
    }

    /** Empty when the player's capabilities are invalid (for example a removed entity). */
    public static Optional<PlayerData> get(Player player)
    {
        return player.getCapability(PLAYER_DATA).resolve();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(PlayerData.class);
    }
}
