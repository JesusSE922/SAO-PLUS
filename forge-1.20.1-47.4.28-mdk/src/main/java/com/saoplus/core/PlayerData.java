package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;

public final class PlayerData
{
    private CompoundTag data = new CompoundTag();

    public void copyFrom(PlayerData other)
    {
        data = other.data.copy();
    }

    public CompoundTag getData()
    {
        return data.copy();
    }

    public void setData(CompoundTag data)
    {
        this.data = data.copy();
    }

    public CompoundTag serializeNBT()
    {
        return data.copy();
    }

    public void deserializeNBT(CompoundTag tag)
    {
        setData(tag);
    }
}
