package com.saoplus.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModNetwork
{
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(SaoPlus.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private ModNetwork()
    {
    }

    public static void register()
    {
        CHANNEL.registerMessage(0, PlayerDataSyncPacket.class,
                PlayerDataSyncPacket::encode,
                PlayerDataSyncPacket::decode,
                PlayerDataSyncPacket::handle,
                Optional.of(net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void syncPlayerData(ServerPlayer player)
    {
        PlayerData data = player.getCapability(PlayerDataCapability.PLAYER_DATA)
                .orElseThrow(() -> new IllegalStateException("Player data capability is missing"));
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new PlayerDataSyncPacket(data.serializeNBT()));
    }
}
