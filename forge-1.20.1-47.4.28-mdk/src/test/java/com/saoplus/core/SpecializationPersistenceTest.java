package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Uses only fictitious test ids; none of these are production families or branches. */
class SpecializationPersistenceTest
{
    private static final ResourceLocation FAMILY_A = id("family_a");
    private static final ResourceLocation FAMILY_B = id("family_b");
    private static final ResourceLocation BRANCH_1 = id("branch_1");
    private static final ResourceLocation BRANCH_2 = id("branch_2");

    private static ResourceLocation id(String path)
    {
        return ResourceLocation.fromNamespaceAndPath("saoplus_test", path);
    }

    private static CompoundTag progress(int value)
    {
        CompoundTag tag = new CompoundTag();
        tag.putInt("value", value);
        return tag;
    }

    @Test
    void onlyOneBranchIsActivePerFamily()
    {
        SpecializationData data = new SpecializationData();
        assertTrue(data.getActive(FAMILY_A).isEmpty());
        assertTrue(data.select(FAMILY_A, BRANCH_1).isEmpty());
        assertEquals(BRANCH_1, data.select(FAMILY_A, BRANCH_2).orElseThrow());
        assertTrue(data.isActive(FAMILY_A, BRANCH_2));
        assertFalse(data.isActive(FAMILY_A, BRANCH_1), "inactive branches must not report as active");
    }

    @Test
    void switchingPreservesAndRestoresProgress()
    {
        SpecializationData data = new SpecializationData();
        data.select(FAMILY_A, BRANCH_1);
        data.setBranchProgress(FAMILY_A, BRANCH_1, progress(42));

        data.select(FAMILY_A, BRANCH_2);
        data.setBranchProgress(FAMILY_A, BRANCH_2, progress(7));
        assertEquals(42, data.getBranchProgress(FAMILY_A, BRANCH_1).getInt("value"));

        data.select(FAMILY_A, BRANCH_1);
        assertEquals(42, data.getBranchProgress(FAMILY_A, BRANCH_1).getInt("value"));
        assertEquals(7, data.getBranchProgress(FAMILY_A, BRANCH_2).getInt("value"));
        assertEquals(2, data.getKnownBranches(FAMILY_A).size());
    }

    @Test
    void familiesAreIndependent()
    {
        SpecializationData data = new SpecializationData();
        data.select(FAMILY_A, BRANCH_1);
        data.select(FAMILY_B, BRANCH_2);
        data.select(FAMILY_A, BRANCH_2);
        assertEquals(BRANCH_2, data.getActive(FAMILY_B).orElseThrow());
        assertTrue(data.getKnownBranches(FAMILY_B).contains(BRANCH_2));
        assertFalse(data.getKnownBranches(FAMILY_B).contains(BRANCH_1));
    }

    @Test
    void roundTripKeepsActiveBranchAndAllProgress()
    {
        SpecializationData data = new SpecializationData();
        data.select(FAMILY_A, BRANCH_1);
        data.setBranchProgress(FAMILY_A, BRANCH_1, progress(1));
        data.select(FAMILY_A, BRANCH_2);
        data.setBranchProgress(FAMILY_A, BRANCH_2, progress(2));
        data.select(FAMILY_B, BRANCH_1);

        SpecializationData loaded = SpecializationData.load(data.save());
        assertEquals(data, loaded);
        assertEquals(BRANCH_2, loaded.getActive(FAMILY_A).orElseThrow());
        assertEquals(1, loaded.getBranchProgress(FAMILY_A, BRANCH_1).getInt("value"));
    }

    @Test
    void loadToleratesMalformedData()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("saoplus_test:wrong_type", "text");
        tag.put("Bad Key!", new CompoundTag());
        CompoundTag badFamily = new CompoundTag();
        badFamily.putInt("active", 5);
        badFamily.putString("branches", "not a compound");
        tag.put("saoplus_test:bad_family", badFamily);
        SpecializationData source = new SpecializationData();
        source.select(FAMILY_A, BRANCH_1);
        tag.put(FAMILY_A.toString(), source.save().getCompound(FAMILY_A.toString()));

        SpecializationData loaded = assertDoesNotThrow(() -> SpecializationData.load(tag));
        assertEquals(BRANCH_1, loaded.getActive(FAMILY_A).orElseThrow());
        assertTrue(loaded.getActive(id("bad_family")).isEmpty());
    }

    @Test
    void emptyStateIsOmittedAndDataSurvivesCloneAndSync()
    {
        PlayerData player = new PlayerData();
        assertFalse(player.serializeNBT().contains("specialization"), "empty state must not alter Phase 1 NBT");

        player.getSpecialization().select(FAMILY_A, BRANCH_1);
        player.getWeaponMastery().getOrCreate(FAMILY_A).setExperience(9L);

        PlayerData copy = new PlayerData();
        copy.copyFrom(player);
        player.getSpecialization().select(FAMILY_A, BRANCH_2);
        assertEquals(BRANCH_1, copy.getSpecialization().getActive(FAMILY_A).orElseThrow(), "copy must be deep");

        PlayerData synced = new PlayerData();
        synced.setData(copy.getData());
        assertEquals(copy.serializeNBT(), synced.serializeNBT());
        assertEquals(9L, synced.getWeaponMastery().get(FAMILY_A).orElseThrow().getExperience());
    }
}
