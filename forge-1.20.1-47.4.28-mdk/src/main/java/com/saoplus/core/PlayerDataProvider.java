package com.saoplus.core;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDataProvider implements ICapabilitySerializable<CompoundTag>
{
    private final PlayerData data = new PlayerData();
    private LazyOptional<PlayerData> optionalData = LazyOptional.of(() -> data);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
    {
        if (capability != PlayerDataCapability.PLAYER_DATA)
        {
            return LazyOptional.empty();
        }

        if (!optionalData.isPresent())
        {
            optionalData = LazyOptional.of(() -> data);
        }
        return optionalData.cast();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return data.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
        data.deserializeNBT(tag);
    }

    public void invalidate()
    {
        optionalData.invalidate();
    }
}
