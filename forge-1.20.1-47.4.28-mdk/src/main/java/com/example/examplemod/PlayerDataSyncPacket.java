package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public record PlayerDataSyncPacket(CompoundTag data)
{
    public static void encode(PlayerDataSyncPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeNbt(packet.data);
    }

    public static PlayerDataSyncPacket decode(FriendlyByteBuf buffer)
    {
        return new PlayerDataSyncPacket(Objects.requireNonNull(buffer.readNbt()));
    }

    public static void handle(PlayerDataSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(packet)));
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PlayerDataSyncPacket packet)
    {
        Player player = Minecraft.getInstance().player;
        if (player != null)
        {
            player.getCapability(PlayerDataCapability.PLAYER_DATA)
                    .ifPresent(data -> data.deserializeNBT(packet.data));
        }
    }
}
