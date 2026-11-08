package com.saoplus.core;

import net.minecraftforge.fml.common.Mod;

@Mod(SaoPlus.MODID)
public final class SaoPlus
{
    public static final String MODID = "saoplus";

    public SaoPlus()
    {
        ModNetwork.register();
    }
}
