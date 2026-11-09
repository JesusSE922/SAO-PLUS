package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stored values for a single weapon family. Holds data only: ranks, thresholds and
 * formulas are not stored here and belong to a future progression service.
 */
public final class WeaponMasteryRecord
{
    private static final String EXPERIENCE = "experience";
    private static final String CHALLENGES = "challenges";
    private static final String TECHNIQUES = "unlocked_techniques";

    private long experience;
    // Challenge payloads stay untyped until the GDD defines their shape.
    private final Map<ResourceLocation, CompoundTag> challenges = new LinkedHashMap<>();
    private final Set<ResourceLocation> unlockedTechniques = new LinkedHashSet<>();

    public long getExperience()
    {
        return experience;
    }

    public void setExperience(long experience)
    {
        this.experience = Math.max(0L, experience);
    }

    public Map<ResourceLocation, CompoundTag> getChallenges()
    {
        Map<ResourceLocation, CompoundTag> copy = new LinkedHashMap<>();
        challenges.forEach((id, tag) -> copy.put(id, tag.copy()));
        return Collections.unmodifiableMap(copy);
    }

    public void putChallenge(ResourceLocation id, CompoundTag value)
    {
        challenges.put(id, value.copy());
    }

    public Set<ResourceLocation> getUnlockedTechniques()
    {
        return Collections.unmodifiableSet(new LinkedHashSet<>(unlockedTechniques));
    }

    public boolean unlockTechnique(ResourceLocation id)
    {
        return unlockedTechniques.add(id);
    }

    public boolean isEmpty()
    {
        return experience == 0L && challenges.isEmpty() && unlockedTechniques.isEmpty();
    }

    public WeaponMasteryRecord copy()
    {
        WeaponMasteryRecord copy = new WeaponMasteryRecord();
        copy.experience = experience;
        challenges.forEach((id, tag) -> copy.challenges.put(id, tag.copy()));
        copy.unlockedTechniques.addAll(unlockedTechniques);
        return copy;
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        tag.putLong(EXPERIENCE, experience);

        CompoundTag challengeTag = new CompoundTag();
        challenges.forEach((id, value) -> challengeTag.put(id.toString(), value.copy()));
        tag.put(CHALLENGES, challengeTag);

        ListTag techniqueList = new ListTag();
        unlockedTechniques.forEach(id -> techniqueList.add(StringTag.valueOf(id.toString())));
        tag.put(TECHNIQUES, techniqueList);
        return tag;
    }

    /** Tolerant load: missing or malformed fields are skipped and unknown fields are ignored. */
    public static WeaponMasteryRecord load(CompoundTag tag)
    {
        WeaponMasteryRecord record = new WeaponMasteryRecord();
        if (tag.contains(EXPERIENCE, Tag.TAG_ANY_NUMERIC))
        {
            record.setExperience(tag.getLong(EXPERIENCE));
        }

        if (tag.contains(CHALLENGES, Tag.TAG_COMPOUND))
        {
            CompoundTag challengeTag = tag.getCompound(CHALLENGES);
            for (String key : challengeTag.getAllKeys())
            {
                ResourceLocation id = ResourceLocation.tryParse(key);
                if (id != null && challengeTag.contains(key, Tag.TAG_COMPOUND))
                {
                    record.challenges.put(id, challengeTag.getCompound(key).copy());
                }
            }
        }

        if (tag.contains(TECHNIQUES, Tag.TAG_LIST))
        {
            for (Tag entry : tag.getList(TECHNIQUES, Tag.TAG_STRING))
            {
                ResourceLocation id = ResourceLocation.tryParse(entry.getAsString());
                if (id != null)
                {
                    record.unlockedTechniques.add(id);
                }
            }
        }
        return record;
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof WeaponMasteryRecord record
                && experience == record.experience
                && challenges.equals(record.challenges)
                && unlockedTechniques.equals(record.unlockedTechniques);
    }

    @Override
    public int hashCode()
    {
        return java.util.Objects.hash(experience, challenges, unlockedTechniques);
    }
}
