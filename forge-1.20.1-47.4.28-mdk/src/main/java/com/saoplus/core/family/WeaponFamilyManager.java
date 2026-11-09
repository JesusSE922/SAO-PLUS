package com.saoplus.core.family;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.saoplus.core.SaoPlus;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;

/** Server-side datapack listener that resolves a held item to its weapon family. */
@Mod.EventBusSubscriber(modid = SaoPlus.MODID)
public final class WeaponFamilyManager extends SimpleJsonResourceReloadListener
{
    private static final Gson GSON = new GsonBuilder().create();
    public static final WeaponFamilyManager INSTANCE = new WeaponFamilyManager();

    private volatile WeaponFamilyIndex index = WeaponFamilyIndex.EMPTY;
    private volatile int generation;

    private WeaponFamilyManager()
    {
        super(GSON, "saoplus/weapon_families");
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(INSTANCE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager,
                         ProfilerFiller profiler)
    {
        generation++;
        index = WeaponFamilyIndex.build(files, message -> SaoPlus.LOGGER.warn("Weapon family data: {}", message));
        SaoPlus.LOGGER.info("Loaded {} weapon family assignment(s) from {} file(s)", index.size(), files.size());
    }

    public FamilyRule ruleOf(ResourceLocation family)
    {
        return index.ruleOf(family);
    }

    public Map<ResourceLocation, FamilyRule> rules()
    {
        return index.rules();
    }

    /** Increases on every datapack reload; caches keyed by item must be dropped when it changes. */
    public int generation()
    {
        return generation;
    }

    public Optional<ResourceLocation> familyOf(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return Optional.empty();
        }
        WeaponFamilyIndex current = index;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Optional<ResourceLocation> direct = current.familyOfItem(itemId);
        if (direct.isPresent())
        {
            return direct;
        }
        for (Map.Entry<ResourceLocation, ResourceLocation> tag : current.tagEntries().entrySet())
        {
            if (stack.is(TagKey.<Item>create(Registries.ITEM, tag.getKey())))
            {
                return Optional.of(tag.getValue());
            }
        }
        for (WeaponCategorySource source : WeaponCategorySource.sources())
        {
            Optional<ResourceLocation> family = source.categoryOf(stack).flatMap(current::familyOfCategory);
            if (family.isPresent())
            {
                return family;
            }
        }
        return Optional.empty();
    }

    /** Weapon stats: the item own stats, else its family default stats (see WeaponProfile); empty if none. */
    public Map<String, Double> statsOf(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return Map.of();
        }
        Map<String, Double> own = index.statsOfItem(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        if (!own.isEmpty())
        {
            return own;
        }
        return familyOf(stack).map(index::statsOfFamily).orElse(Map.of());
    }
}
