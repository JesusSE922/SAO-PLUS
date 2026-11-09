package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Specialization state per weapon family (GDD 6.3): at most one active branch per family, and the
 * progress of every branch ever selected is kept when switching. Family and branch ids are open
 * {@link ResourceLocation}s; branch progress is opaque NBT until the GDD defines it.
 */
public final class SpecializationData
{
    private static final String ACTIVE = "active";
    private static final String BRANCHES = "branches";

    private static final class State
    {
        ResourceLocation active;
        final Map<ResourceLocation, CompoundTag> branches = new LinkedHashMap<>();

        State copy()
        {
            State copy = new State();
            copy.active = active;
            branches.forEach((id, tag) -> copy.branches.put(id, tag.copy()));
            return copy;
        }

        @Override
        public boolean equals(Object other)
        {
            return other instanceof State state
                    && java.util.Objects.equals(active, state.active)
                    && branches.equals(state.branches);
        }

        @Override
        public int hashCode()
        {
            return java.util.Objects.hash(active, branches);
        }
    }

    private final Map<ResourceLocation, State> families = new LinkedHashMap<>();

    public Optional<ResourceLocation> getActive(ResourceLocation family)
    {
        State state = families.get(family);
        return state == null ? Optional.empty() : Optional.ofNullable(state.active);
    }

    /** Inactive branches must not apply their effects; callers use this to decide. */
    public boolean isActive(ResourceLocation family, ResourceLocation branch)
    {
        return getActive(family).filter(branch::equals).isPresent();
    }

    /**
     * Makes {@code branch} the active branch of {@code family}. The previous branch is
     * deactivated and its progress is kept. Returns the previously active branch, if any.
     */
    public Optional<ResourceLocation> select(ResourceLocation family, ResourceLocation branch)
    {
        State state = families.computeIfAbsent(family, id -> new State());
        ResourceLocation previous = state.active;
        state.active = branch;
        state.branches.computeIfAbsent(branch, id -> new CompoundTag());
        return Optional.ofNullable(previous);
    }

    public Set<ResourceLocation> getKnownBranches(ResourceLocation family)
    {
        State state = families.get(family);
        return state == null
                ? Set.of()
                : Collections.unmodifiableSet(new LinkedHashSet<>(state.branches.keySet()));
    }

    /** Copy of a branch's stored progress; empty if the branch was never selected. */
    public CompoundTag getBranchProgress(ResourceLocation family, ResourceLocation branch)
    {
        State state = families.get(family);
        CompoundTag tag = state == null ? null : state.branches.get(branch);
        return tag == null ? new CompoundTag() : tag.copy();
    }

    public void setBranchProgress(ResourceLocation family, ResourceLocation branch, CompoundTag progress)
    {
        families.computeIfAbsent(family, id -> new State()).branches.put(branch, progress.copy());
    }

    public boolean isEmpty()
    {
        return families.isEmpty();
    }

    public SpecializationData copy()
    {
        SpecializationData copy = new SpecializationData();
        families.forEach((id, state) -> copy.families.put(id, state.copy()));
        return copy;
    }

    public CompoundTag save()
    {
        CompoundTag tag = new CompoundTag();
        families.forEach((family, state) ->
        {
            CompoundTag familyTag = new CompoundTag();
            if (state.active != null)
            {
                familyTag.putString(ACTIVE, state.active.toString());
            }
            CompoundTag branchTag = new CompoundTag();
            state.branches.forEach((branch, progress) -> branchTag.put(branch.toString(), progress.copy()));
            familyTag.put(BRANCHES, branchTag);
            tag.put(family.toString(), familyTag);
        });
        return tag;
    }

    /** Tolerant load: invalid ids and malformed entries are skipped, the rest is kept. */
    public static SpecializationData load(CompoundTag tag)
    {
        SpecializationData data = new SpecializationData();
        for (String familyKey : tag.getAllKeys())
        {
            ResourceLocation family = ResourceLocation.tryParse(familyKey);
            if (family == null || !tag.contains(familyKey, Tag.TAG_COMPOUND))
            {
                continue;
            }
            CompoundTag familyTag = tag.getCompound(familyKey);
            State state = new State();
            if (familyTag.contains(BRANCHES, Tag.TAG_COMPOUND))
            {
                CompoundTag branchTag = familyTag.getCompound(BRANCHES);
                for (String branchKey : branchTag.getAllKeys())
                {
                    ResourceLocation branch = ResourceLocation.tryParse(branchKey);
                    if (branch != null && branchTag.contains(branchKey, Tag.TAG_COMPOUND))
                    {
                        state.branches.put(branch, branchTag.getCompound(branchKey).copy());
                    }
                }
            }
            if (familyTag.contains(ACTIVE, Tag.TAG_STRING))
            {
                ResourceLocation active = ResourceLocation.tryParse(familyTag.getString(ACTIVE));
                if (active != null)
                {
                    state.active = active;
                    state.branches.computeIfAbsent(active, id -> new CompoundTag());
                }
            }
            data.families.put(family, state);
        }
        return data;
    }

    @Override
    public boolean equals(Object other)
    {
        return other instanceof SpecializationData data && families.equals(data.families);
    }

    @Override
    public int hashCode()
    {
        return families.hashCode();
    }
}
