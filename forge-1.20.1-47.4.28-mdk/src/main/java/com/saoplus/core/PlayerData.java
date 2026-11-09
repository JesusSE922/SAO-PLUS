package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class PlayerData
{
    static final String WEAPON_MASTERY_KEY = "weapon_mastery";

    // Untyped data; excludes typed components, which are stored under their own keys.
    private CompoundTag data = new CompoundTag();
    private WeaponMasteryData weaponMastery = new WeaponMasteryData();

    public WeaponMasteryData getWeaponMastery()
    {
        return weaponMastery;
    }

    public void copyFrom(PlayerData other)
    {
        data = other.data.copy();
        weaponMastery = other.weaponMastery.copy();
    }

    public CompoundTag getData()
    {
        return serializeNBT();
    }

    public void setData(CompoundTag data)
    {
        deserializeNBT(data);
    }

    public CompoundTag serializeNBT()
    {
        CompoundTag tag = data.copy();
        if (!weaponMastery.isEmpty())
        {
            tag.put(WEAPON_MASTERY_KEY, weaponMastery.save());
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag tag)
    {
        CompoundTag raw = tag.copy();
        weaponMastery = raw.contains(WEAPON_MASTERY_KEY, Tag.TAG_COMPOUND)
                ? WeaponMasteryData.load(raw.getCompound(WEAPON_MASTERY_KEY))
                : new WeaponMasteryData();
        raw.remove(WEAPON_MASTERY_KEY);
        data = raw;
    }
}
