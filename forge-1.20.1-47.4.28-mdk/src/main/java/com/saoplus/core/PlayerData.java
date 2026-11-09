package com.saoplus.core;

import com.saoplus.core.progression.ProgressionData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public final class  PlayerData
{
    /** Namespaced root key, so it cannot collide with unrelated data. */
    static final String WEAPON_MASTERY_KEY = "saoplus:weapon_mastery";
    /** Key used before the namespaced one; read and migrated, never written. */
    static final String LEGACY_WEAPON_MASTERY_KEY = "weapon_mastery";
    static final String SPECIALIZATION_KEY = "specialization";
    static final String PROGRESSION_KEY = "saoplus:progression";

    // Untyped data; excludes typed components, which are stored under their own keys.
    private CompoundTag data = new CompoundTag();
    private WeaponMasteryData weaponMastery = new WeaponMasteryData();
    private SpecializationData specialization = new SpecializationData();
    private ProgressionData progression = new ProgressionData();

    public WeaponMasteryData getWeaponMastery()
    {
        return weaponMastery;
    }

    public ProgressionData getProgression()
    {
        return progression;
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
        progression = other.progression.copy();
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
        if (!progression.isEmpty())
        {
            tag.put(PROGRESSION_KEY, progression.save());
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
        if (raw.contains(WEAPON_MASTERY_KEY, Tag.TAG_COMPOUND))
        {
            weaponMastery = WeaponMasteryData.load(raw.getCompound(WEAPON_MASTERY_KEY));
        }
        else if (isLegacyMastery(raw))
        {
            weaponMastery = WeaponMasteryData.load(raw.getCompound(LEGACY_WEAPON_MASTERY_KEY));
            raw.remove(LEGACY_WEAPON_MASTERY_KEY);
        }
        else
        {
            weaponMastery = new WeaponMasteryData();
        }
        specialization = raw.contains(SPECIALIZATION_KEY, Tag.TAG_COMPOUND)
                ? SpecializationData.load(raw.getCompound(SPECIALIZATION_KEY))
                : new SpecializationData();
        progression = raw.contains(PROGRESSION_KEY, Tag.TAG_COMPOUND)
                ? ProgressionData.load(raw.getCompound(PROGRESSION_KEY))
                : new ProgressionData();
        raw.remove(WEAPON_MASTERY_KEY);
        raw.remove(SPECIALIZATION_KEY);
        if (raw.contains(PROGRESSION_KEY, Tag.TAG_COMPOUND))
        {
            raw.remove(PROGRESSION_KEY);
        }
        data = raw;
    }

    /**
     * The legacy key is migrated only if it looks like mastery data (every entry a compound under a
     * valid id). Anything else under that name is foreign data and stays in the untyped tag.
     */
    private static boolean isLegacyMastery(CompoundTag raw)
    {
        if (!raw.contains(LEGACY_WEAPON_MASTERY_KEY, Tag.TAG_COMPOUND))
        {
            return false;
        }
        CompoundTag legacy = raw.getCompound(LEGACY_WEAPON_MASTERY_KEY);
        for (String key : legacy.getAllKeys())
        {
            if (ResourceLocation.tryParse(key) == null || !legacy.contains(key, Tag.TAG_COMPOUND))
            {
                return false;
            }
        }
        return true;
    }
}
