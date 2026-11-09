package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class PlayerData
{
    static final String WEAPON_MASTERY_KEY = "weapon_mastery";
    static final String SPECIALIZATION_KEY = "specialization";

    // Untyped data; excludes typed components, which are stored under their own keys.
    private CompoundTag data = new CompoundTag();
    private WeaponMasteryData weaponMastery = new WeaponMasteryData();
    private SpecializationData specialization = new SpecializationData();

    public WeaponMasteryData getWeaponMastery()
    {
        return weaponMastery;
    }

    public SpecializationData getSpecialization()
    {
        return specialization;
    }

    public void copyFrom(PlayerData other)
    {
        data = other.data.copy();
        weaponMastery = other.weaponMastery.copy();
        specialization = other.specialization.copy();
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
        if (!specialization.isEmpty())
        {
            tag.put(SPECIALIZATION_KEY, specialization.save());
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag tag)
    {
        CompoundTag raw = tag.copy();
        weaponMastery = raw.contains(WEAPON_MASTERY_KEY, Tag.TAG_COMPOUND)
                ? WeaponMasteryData.load(raw.getCompound(WEAPON_MASTERY_KEY))
                : new WeaponMasteryData();
        specialization = raw.contains(SPECIALIZATION_KEY, Tag.TAG_COMPOUND)
                ? SpecializationData.load(raw.getCompound(SPECIALIZATION_KEY))
                : new SpecializationData();
        raw.remove(WEAPON_MASTERY_KEY);
        raw.remove(SPECIALIZATION_KEY);
        data = raw;
    }
}
