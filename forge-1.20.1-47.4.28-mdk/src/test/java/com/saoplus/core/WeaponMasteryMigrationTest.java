package com.saoplus.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Fictitious ids only (saoplus_test). */
class WeaponMasteryMigrationTest
{
    private static final ResourceLocation FAMILY = ResourceLocation.tryParse("saoplus_test:family_a");

    private static CompoundTag legacyRoot()
    {
        CompoundTag record = new CompoundTag();
        record.putLong("experience", 7L);
        CompoundTag mastery = new CompoundTag();
        mastery.put(FAMILY.toString(), record);
        CompoundTag root = new CompoundTag();
        root.put("weapon_mastery", mastery);
        root.putString("foreign", "kept");
        return root;
    }

    @Test
    void legacyKeyIsMigratedToNamespacedKey()
    {
        PlayerData data = new PlayerData();
        data.deserializeNBT(legacyRoot());
        assertEquals(7L, data.getWeaponMastery().get(FAMILY).orElseThrow().getExperience());

        CompoundTag saved = data.serializeNBT();
        assertTrue(saved.contains("saoplus:weapon_mastery"));
        assertFalse(saved.contains("weapon_mastery"));
        assertEquals("kept", saved.getString("foreign"));
    }

    @Test
    void foreignDataUnderLegacyNameIsLeftUntouched()
    {
        CompoundTag foreign = new CompoundTag();
        foreign.putInt("Not A Valid Id", 3);
        CompoundTag root = new CompoundTag();
        root.put("weapon_mastery", foreign);

        PlayerData data = new PlayerData();
        data.deserializeNBT(root);
        assertTrue(data.getWeaponMastery().isEmpty());
        assertEquals(foreign, data.serializeNBT().getCompound("weapon_mastery"));
    }

    @Test
    void unknownRecordFieldsSurviveRoundTripAndCopy()
    {
        CompoundTag record = new CompoundTag();
        record.putLong("experience", 1L);
        record.put("future_field", StringTag.valueOf("x"));
        CompoundTag mastery = new CompoundTag();
        mastery.put(FAMILY.toString(), record);
        CompoundTag root = new CompoundTag();
        root.put("saoplus:weapon_mastery", mastery);

        PlayerData data = new PlayerData();
        data.deserializeNBT(root);
        PlayerData clone = new PlayerData();
        clone.copyFrom(data);
        assertEquals("x", clone.serializeNBT().getCompound("saoplus:weapon_mastery")
                .getCompound(FAMILY.toString()).getString("future_field"));
    }

    @Test
    void bothKeysPresentNamespacedWinsAndLegacyIsNotLostOrOverwritten()
    {
        CompoundTag root = legacyRoot();
        CompoundTag newRecord = new CompoundTag();
        newRecord.putLong("experience", 99L);
        CompoundTag newMastery = new CompoundTag();
        newMastery.put(FAMILY.toString(), newRecord);
        root.put("saoplus:weapon_mastery", newMastery);

        PlayerData data = new PlayerData();
        data.deserializeNBT(root);
        assertEquals(99L, data.getWeaponMastery().get(FAMILY).orElseThrow().getExperience());
        CompoundTag saved = data.serializeNBT();
        assertEquals(7L, saved.getCompound("weapon_mastery").getCompound(FAMILY.toString()).getLong("experience"));
    }

    @Test
    void migrationIsIdempotent()
    {
        PlayerData first = new PlayerData();
        first.deserializeNBT(legacyRoot());
        CompoundTag once = first.serializeNBT();

        PlayerData second = new PlayerData();
        second.deserializeNBT(once);
        CompoundTag twice = second.serializeNBT();
        assertEquals(once, twice);
    }

    @Test
    void editingKnownFieldsKeepsUnknownFields()
    {
        CompoundTag record = new CompoundTag();
        record.put("future_field", StringTag.valueOf("x"));
        CompoundTag mastery = new CompoundTag();
        mastery.put(FAMILY.toString(), record);
        CompoundTag root = new CompoundTag();
        root.put("saoplus:weapon_mastery", mastery);

        PlayerData data = new PlayerData();
        data.deserializeNBT(root);
        data.getWeaponMastery().getOrCreate(FAMILY).setExperience(5L);
        data.getWeaponMastery().getOrCreate(FAMILY).unlockTechnique(ResourceLocation.tryParse("saoplus_test:t"));

        CompoundTag saved = data.serializeNBT().getCompound("saoplus:weapon_mastery").getCompound(FAMILY.toString());
        assertEquals("x", saved.getString("future_field"));
        assertEquals(5L, saved.getLong("experience"));
    }

    @Test
    void emptyMasteryIsOmitted()
    {
        assertFalse(new PlayerData().serializeNBT().contains("saoplus:weapon_mastery"));
    }
}
