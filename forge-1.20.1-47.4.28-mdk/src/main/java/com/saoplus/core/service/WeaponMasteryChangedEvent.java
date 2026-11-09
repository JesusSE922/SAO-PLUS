package com.saoplus.core.service;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

/** Posted on the Forge event bus, server side, after a mastery value changed. Not cancelable. */
public final class WeaponMasteryChangedEvent extends Event
{
    public enum Kind
    {
        EXPERIENCE,
        CHALLENGE,
        TECHNIQUE_UNLOCKED
    }

    private final ServerPlayer player;
    private final ResourceLocation family;
    private final Kind kind;
    private final ResourceLocation subject;
    private final long newExperience;

    WeaponMasteryChangedEvent(ServerPlayer player, ResourceLocation family, Kind kind,
                              ResourceLocation subject, long newExperience)
    {
        this.player = player;
        this.family = family;
        this.kind = kind;
        this.subject = subject;
        this.newExperience = newExperience;
    }

    public ServerPlayer getPlayer()
    {
        return player;
    }

    public ResourceLocation getFamily()
    {
        return family;
    }

    public Kind getKind()
    {
        return kind;
    }

    /** Challenge or technique id for CHALLENGE / TECHNIQUE_UNLOCKED; null for EXPERIENCE. */
    public ResourceLocation getSubject()
    {
        return subject;
    }

    /** The family's total experience after the change. */
    public long getNewExperience()
    {
        return newExperience;
    }
}
