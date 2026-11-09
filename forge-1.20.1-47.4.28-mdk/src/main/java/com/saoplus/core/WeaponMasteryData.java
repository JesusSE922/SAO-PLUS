package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Independent mastery records keyed by an open weapon-family id. The family set is not
 * defined here, so adding families never changes this class or the NBT layout.
 */
public final class WeaponMasteryData
{
    private final Map<ResourceLocation, WeaponMasteryRecord> records = new LinkedHashMap<>();

    public Optional<WeaponMasteryRecord> get(ResourceLocation family)
    {
        WeaponMasteryRecord record = records.get(family);
        return record == null ? Optional.empty() : Optional.of(record.copy());
    }

    /** Returns the live record for a family, creating it if absent. */
    public WeaponMasteryRecord getOrCreate(ResourceLocation family)
    {
        return records.computeIfAbsent(family, id -> new WeaponMasteryRecord());
    }

    public Map<ResourceLocation, WeaponMasteryRecord> snapshot()
    {
        Map<ResourceLocation, WeaponMasteryRecord> copy = new LinkedHashMap<>();
        records.forEach((id, record) -> copy.put(id, record.copy()));
        return Collections.unmodifiableMap(copy);
    }

    public boolean isEmpty()
    {
        return records.isEmpty();
    }

    public WeaponMasteryData copy()
    {
        WeaponMasteryData copy = new WeaponMasteryData();
        records.forEach((id, record) -> copy.records.put(id, record.copy()));
        return copy;
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        records.forEach((id, record) -> tag.put(id.toString(), record.save()));
        return tag;
    }

    /** Tolerant load: invalid keys and non-compound entries are skipped, the rest is kept. */
    public static WeaponMasteryData load(CompoundTag tag)
    {
        WeaponMasteryData data = new WeaponMasteryData();
        for (String key : tag.getAllKeys())
        {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id != null && tag.contains(key, Tag.TAG_COMPOUND))
            {
                data.records.put(id, WeaponMasteryRecord.load(tag.getCompound(key)));
            }
        }
        return data;
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof WeaponMasteryData data && records.equals(data.records);
    }

    @Override
    public int hashCode()
    {
        return records.hashCode();
    }
}
