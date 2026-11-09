package com.saoplus.core.service;

import com.saoplus.core.SaoPlus;
import com.saoplus.core.SaoPlusConfig;
import com.saoplus.core.family.WeaponFamilyManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Experience by use (GDD 7.2), source side. It listens to vanilla Forge damage events, so it works
 * with any mod that deals damage through them and has no compile-time dependency on Epic Fight or
 * other combat mods. Only direct melee hits by a player with a weapon of a configured family count;
 * projectile weapons are not handled yet.
 */
@Mod.EventBusSubscriber(modid = SaoPlus.MODID)
public final class WeaponUsageEvents
{
    private WeaponUsageEvents()
    {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event)
    {
        if (event.isCanceled() || event.getAmount() <= 0.0F)
        {
            return;
        }
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)
                || source.getDirectEntity() != player
                || event.getEntity() == player)
        {
            return;
        }

        int experience = SaoPlusConfig.MASTERY_EXPERIENCE_PER_HIT.get();
        if (experience <= 0)
        {
            return;
        }
        ItemStack weapon = player.getMainHandItem();
        WeaponFamilyManager.INSTANCE.familyOf(weapon)
                .ifPresent(family -> WeaponMasteryService.addExperience(player, family, experience));
    }
}
