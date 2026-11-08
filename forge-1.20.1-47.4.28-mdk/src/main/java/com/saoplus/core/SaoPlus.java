package com.saoplus.core;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SaoPlus.MODID)
public final class SaoPlus
{
    public static final String MODID = "saoplus";
    static final Logger LOGGER = LogUtils.getLogger();

    public SaoPlus()
    {
        ModNetwork.register();
    }
}
