package com.saoplus.core;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(SaoPlus.MODID)
public final class SaoPlus
{
    public static final String MODID = "saoplus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SaoPlus(FMLJavaModLoadingContext context)
    {
        ModNetwork.register();
        context.registerConfig(ModConfig.Type.SERVER, SaoPlusConfig.SPEC);
    }
}
