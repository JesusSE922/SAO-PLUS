package com.saoplus.core.service;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Posted before a specialization switch is applied. Cancelable so that future rules (the GDD says
 * switching is limited and has consequences, but defines neither) can veto it.
 */
@Cancelable
public final class SpecializationChangedEvent extends Event
{
    private final ServerPlayer player;
    private final ResourceLocation family;
    private final ResourceLocation previousBranch;
    private final ResourceLocation newBranch;

    SpecializationChangedEvent(ServerPlayer player, ResourceLocation family,
                               ResourceLocation previousBranch, ResourceLocation newBranch)
    {
        this.player = player;
        this.family = family;
        this.previousBranch = previousBranch;
        this.newBranch = newBranch;
    }

    public ServerPlayer getPlayer()
    {
        return player;
    }

    public ResourceLocation getFamily()
    {
        return family;
    }

    /** The branch being deactivated, or null if the family had no active branch. */
    public ResourceLocation getPreviousBranch()
    {
        return previousBranch;
    }

    public ResourceLocation getNewBranch()
    {
        return newBranch;
    }
}
