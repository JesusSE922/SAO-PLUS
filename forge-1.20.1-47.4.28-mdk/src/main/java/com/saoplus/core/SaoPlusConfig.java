package com.saoplus.core;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Per-world server configuration. Values here are tunable placeholders: the GDD has not defined
 * mastery numbers yet, so nothing in this file is a confirmed design value.
 */
public final class SaoPlusConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MASTERY_EXPERIENCE_PER_HIT;

    static
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MASTERY_EXPERIENCE_PER_HIT = builder
                .comment("Placeholder (not a GDD value): weapon mastery experience granted to the weapon's",
                        "family for each melee hit that deals damage. 0 disables experience by use.")
                .defineInRange("masteryExperiencePerHit", 1, 0, 1000);
        SPEC = builder.build();
    }

    private SaoPlusConfig()
    {
    }
}
