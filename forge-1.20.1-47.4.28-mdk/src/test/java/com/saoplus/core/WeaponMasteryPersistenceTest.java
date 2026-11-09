package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Uses only fictitious test ids; none of these are production weapon families. */
class WeaponMasteryPersistenceTest
{
    private static final ResourceLocation FAMILY_A = id("saoplus_test", "family_a");
    private static final ResourceLocation FAMILY_B = id("saoplus_test", "family_b");
    private static final ResourceLocation CHALLENGE = id("saoplus_test", "challenge_x");
    private static final ResourceLocation TECHNIQUE = id("saoplus_test", "technique_x");

    private static ResourceLocation id(String ns, String path)
    {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }

    private static WeaponMasteryData sample()
    {
        WeaponMasteryData data = new WeaponMasteryData();
        WeaponMasteryRecord a = data.getOrCreate(FAMILY_A);
        a.setExperience(1234L);
        CompoundTag progress = new CompoundTag();
        progress.putInt("count", 7);
        a.putChallenge(CHALLENGE, progress);
        a.unlockTechnique(TECHNIQUE);
        data.getOrCreate(FAMILY_B).setExperience(99L);
        return data;
    }

    @Test
    void t1_roundTrip()
    {
        WeaponMasteryData empty = new WeaponMasteryData();
        assertEquals(empty, WeaponMasteryData.load(empty.save()));

        WeaponMasteryData one = new WeaponMasteryData();
        one.getOrCreate(FAMILY_A).setExperience(5L);
        assertEquals(one, WeaponMasteryData.load(one.save()));

        WeaponMasteryData many = sample();
        assertEquals(many, WeaponMasteryData.load(many.save()));
    }

    @Test
    void t2_familiesAreIndependent()
    {
        WeaponMasteryData data = sample();
        data.getOrCreate(FAMILY_A).setExperience(1L);
        assertEquals(99L, data.get(FAMILY_B).orElseThrow().getExperience());
        assertTrue(data.get(FAMILY_B).orElseThrow().getUnlockedTechniques().isEmpty());
    }

    @Test
    void t3_emptyOrMissingTagYieldsEmptyState()
    {
        assertTrue(WeaponMasteryData.load(new CompoundTag()).isEmpty());
        PlayerData player = new PlayerData();
        player.deserializeNBT(new CompoundTag());
        assertTrue(player.getWeaponMastery().isEmpty());
    }

    @Test
    void t4_corruptDataDoesNotLoseOtherFamilies()
    {
        CompoundTag tag = sample().save();
        tag.putString("saoplus_test:wrong_type", "not a compound");
        tag.put("Invalid Key!", new CompoundTag());
        CompoundTag badFields = new CompoundTag();
        badFields.putString("experience", "text");
        badFields.putInt("challenges", 3);
        ListTag badList = new ListTag();
        badList.add(StringTag.valueOf("NOT valid"));
        badFields.put("unlocked_techniques", badList);
        tag.put("saoplus_test:bad_fields", badFields);

        WeaponMasteryData loaded = assertDoesNotThrow(() -> WeaponMasteryData.load(tag));
        assertEquals(sample().get(FAMILY_A), loaded.get(FAMILY_A));
        assertEquals(99L, loaded.get(FAMILY_B).orElseThrow().getExperience());
        assertTrue(loaded.get(id("saoplus_test", "bad_fields")).orElseThrow().isEmpty());
    }

    @Test
    void t5_unrecognizedFamilySurvivesCycle()
    {
        ResourceLocation unknown = id("some_other_namespace", "never_registered");
        WeaponMasteryData data = new WeaponMasteryData();
        data.getOrCreate(unknown).setExperience(42L);
        WeaponMasteryData cycled = WeaponMasteryData.load(WeaponMasteryData.load(data.save()).save());
        assertEquals(42L, cycled.get(unknown).orElseThrow().getExperience());
    }

    @Test
    void t6_copyIsDeep()
    {
        PlayerData source = new PlayerData();
        source.getWeaponMastery().getOrCreate(FAMILY_A).setExperience(10L);
        PlayerData target = new PlayerData();
        target.copyFrom(source);

        source.getWeaponMastery().getOrCreate(FAMILY_A).setExperience(500L);
        source.getWeaponMastery().getOrCreate(FAMILY_A).unlockTechnique(TECHNIQUE);
        WeaponMasteryRecord copied = target.getWeaponMastery().get(FAMILY_A).orElseThrow();
        assertEquals(10L, copied.getExperience());
        assertTrue(copied.getUnlockedTechniques().isEmpty());
    }

    @Test
    void t7_phase1DataLoadsAndRawContentIsKept()
    {
        CompoundTag legacy = new CompoundTag();
        legacy.putString("legacy_key", "legacy_value");

        PlayerData player = new PlayerData();
        player.deserializeNBT(legacy);
        assertTrue(player.getWeaponMastery().isEmpty());
        assertEquals(legacy, player.serializeNBT(), "empty mastery must not alter Phase 1 NBT");

        player.getWeaponMastery().getOrCreate(FAMILY_A).setExperience(3L);
        PlayerData reloaded = new PlayerData();
        reloaded.deserializeNBT(player.serializeNBT());
        assertEquals("legacy_value", reloaded.serializeNBT().getString("legacy_key"));
        assertEquals(3L, reloaded.getWeaponMastery().get(FAMILY_A).orElseThrow().getExperience());
        assertEquals(player.serializeNBT(), reloaded.serializeNBT());
    }

    @Test
    void t8_exposedViewsCannotMutateState()
    {
        WeaponMasteryData data = sample();
        assertThrows(UnsupportedOperationException.class, () -> data.snapshot().clear());
        WeaponMasteryRecord record = data.get(FAMILY_A).orElseThrow();
        assertThrows(UnsupportedOperationException.class, () -> record.getUnlockedTechniques().clear());
        assertThrows(UnsupportedOperationException.class, () -> record.getChallenges().clear());

        // Mutating returned copies must not touch the stored data.
        record.setExperience(0L);
        data.snapshot().get(FAMILY_A).getChallenges().get(CHALLENGE).putInt("count", 999);
        assertEquals(1234L, data.get(FAMILY_A).orElseThrow().getExperience());
        assertEquals(7, data.get(FAMILY_A).orElseThrow().getChallenges().get(CHALLENGE).getInt("count"));
    }

    @Test
    void t9_storageHasNoEquippedWeaponState()
    {
        // Mastery holds no "current weapon": switching weapons is not a data operation,
        // so every family's record must be unchanged across a save/load of the whole state.
        PlayerData player = new PlayerData();
        player.getWeaponMastery().getOrCreate(FAMILY_A).setExperience(100L);
        player.getWeaponMastery().getOrCreate(FAMILY_B).setExperience(200L);
        CompoundTag before = player.serializeNBT();

        PlayerData after = new PlayerData();
        after.deserializeNBT(before);
        assertEquals(100L, after.getWeaponMastery().get(FAMILY_A).orElseThrow().getExperience());
        assertEquals(200L, after.getWeaponMastery().get(FAMILY_B).orElseThrow().getExperience());
        assertEquals(before, after.serializeNBT());
    }
}
