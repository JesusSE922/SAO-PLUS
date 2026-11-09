package com.saoplus.core;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModNetwork
{
    private static final String PROTOCOL_VERSION = "1";
    // FriendlyByteBuf#readNbt rejects tags above 2 MiB; stay below so a snapshot never disconnects a client.
    static final int MAX_SYNC_BYTES = 1_900_000;
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

    /**
     * Sends the full player data snapshot to its owner. Returns false, and logs, when the data
     * cannot be sent: missing capability (for example a removed entity) or an oversized snapshot.
     */
    public static boolean syncPlayerData(ServerPlayer player)
    {
        Optional<PlayerData> data = PlayerDataCapability.get(player);
        if (data.isEmpty())
        {
            SaoPlus.LOGGER.warn("Skipped SAO+ data sync for {}: player data capability is missing",
                    player.getGameProfile().getName());
            return false;
        }

        CompoundTag snapshot = data.get().serializeNBT();
        int size = encodedSize(snapshot);
        if (size > MAX_SYNC_BYTES)
        {
            SaoPlus.LOGGER.error("Skipped SAO+ data sync for {}: snapshot is {} bytes, above the {} byte limit",
                    player.getGameProfile().getName(), size, MAX_SYNC_BYTES);
            return false;
        }

        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayerDataSyncPacket(snapshot));
        return true;
    }

    static int encodedSize(CompoundTag tag)
    {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try
        {
            buffer.writeNbt(tag);
            return buffer.readableBytes();
        }
        finally
        {
            buffer.release();
        }
    }
}
