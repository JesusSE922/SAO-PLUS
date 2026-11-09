package com.saoplus.core;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SaoPlus.MODID)
public final class SaoPlus
{
    public static final String MODID = "saoplus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SaoPlus(FMLJavaModLoadingContext context)
    {
        ModNetwork.register();
        context.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, SaoPlusConfig.SPEC);
        ModContent.register(context.getModEventBus());
        com.saoplus.core.economy.EconomyService.init();
        com.saoplus.core.compat.ftb.FtbCompat.initIfPresent();
        com.saoplus.core.compat.epicfight.EpicFightCompat.initIfPresent();
    }
}
