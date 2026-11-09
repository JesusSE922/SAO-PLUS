package com.saoplus.core.service;

import com.saoplus.core.PlayerData;
import com.saoplus.core.PlayerDataCapability;
import com.saoplus.core.PlayerDataSync;
import com.saoplus.core.SaoPlus;
import com.saoplus.core.WeaponMasteryRecord;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Optional;

/**
 * Server-authoritative way to change Weapon Mastery data. It applies values only: it contains no
 * ranks, thresholds, formulas or requirements, which the GDD has not defined. Future rules call
 * into this service. Every change posts a {@link WeaponMasteryChangedEvent} and schedules a sync.
 */
public final class WeaponMasteryService
{
    private WeaponMasteryService()
    {
    }

    /** Adds experience to a family. Returns false if {@code amount} is not positive or data is unavailable. */
    public static boolean addExperience(ServerPlayer player, ResourceLocation family, long amount)
    {
        if (amount <= 0)
        {
            return false;
        }
        Optional<PlayerData> data = dataOf(player);
        if (data.isEmpty())
        {
            return false;
        }
        WeaponMasteryRecord record = data.get().getWeaponMastery().getOrCreate(family);
        long total = saturatedAdd(record.getExperience(), amount);
        record.setExperience(total);
        changed(player, family, WeaponMasteryChangedEvent.Kind.EXPERIENCE, null, total);
        return true;
    }

    /** Stores the progress of a challenge (opaque NBT until the GDD defines challenges). */
    public static boolean setChallengeProgress(ServerPlayer player, ResourceLocation family,
                                               ResourceLocation challenge, CompoundTag progress)
    {
        Optional<PlayerData> data = dataOf(player);
        if (data.isEmpty())
        {
            return false;
        }
        WeaponMasteryRecord record = data.get().getWeaponMastery().getOrCreate(family);
        record.putChallenge(challenge, progress);
        changed(player, family, WeaponMasteryChangedEvent.Kind.CHALLENGE, challenge, record.getExperience());
        return true;
    }

    /** Unlocks a technique. Returns false if it was already unlocked or data is unavailable. */
    public static boolean unlockTechnique(ServerPlayer player, ResourceLocation family, ResourceLocation technique)
    {
        Optional<PlayerData> data = dataOf(player);
        if (data.isEmpty())
        {
            return false;
        }
        WeaponMasteryRecord record = data.get().getWeaponMastery().getOrCreate(family);
        if (!record.unlockTechnique(technique))
        {
            return false;
        }
        changed(player, family, WeaponMasteryChangedEvent.Kind.TECHNIQUE_UNLOCKED, technique, record.getExperience());
        return true;
    }

    static long saturatedAdd(long current, long amount)
    {
        long sum = current + amount;
        return sum < current ? Long.MAX_VALUE : sum;
    }

    private static Optional<PlayerData> dataOf(ServerPlayer player)
    {
        Optional<PlayerData> data = PlayerDataCapability.get(player);
        if (data.isEmpty())
        {
            SaoPlus.LOGGER.warn("Ignored a weapon mastery change for {}: player data capability is missing",
                    player.getGameProfile().getName());
        }
        return data;
    }

    private static void changed(ServerPlayer player, ResourceLocation family,
                                WeaponMasteryChangedEvent.Kind kind, ResourceLocation subject, long experience)
    {
        PlayerDataSync.markDirty(player);
        MinecraftForge.EVENT_BUS.post(new WeaponMasteryChangedEvent(player, family, kind, subject, experience));
    }
}
