package com.saoplus.core.family;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Immutable lookup from item ids and item tags to weapon family ids, built from datapack files
 * {@code data/<namespace>/saoplus/weapon_families/<name>.json}:
 * <pre>{"family": "namespace:id", "items": ["mod:item"], "tags": ["mod:tag"]}</pre>
 * SAO+ ships no families: the catalog is defined by data, so no family or weapon id is hard-coded.
 */
public final class WeaponFamilyIndex
{
    public static final WeaponFamilyIndex EMPTY =
            new WeaponFamilyIndex(Map.of(), Map.of());

    private final Map<ResourceLocation, ResourceLocation> byItem;
    private final Map<ResourceLocation, ResourceLocation> byTag;

    private WeaponFamilyIndex(Map<ResourceLocation, ResourceLocation> byItem,
                              Map<ResourceLocation, ResourceLocation> byTag)
    {
        this.byItem = byItem;
        this.byTag = byTag;
    }

    public Optional<ResourceLocation> familyOfItem(ResourceLocation itemId)
    {
        return Optional.ofNullable(byItem.get(itemId));
    }

    /** Tag id to family, for callers that test tag membership against a stack. */
    public Map<ResourceLocation, ResourceLocation> tagEntries()
    {
        return byTag;
    }

    public int size()
    {
        return byItem.size() + byTag.size();
    }

    /**
     * Builds an index from parsed files. Files are processed in id order so conflicts resolve
     * deterministically: the first file to claim an item or tag wins and later claims are reported.
     */
    public static WeaponFamilyIndex build(Map<ResourceLocation, JsonElement> files, Consumer<String> warn)
    {
        Map<ResourceLocation, ResourceLocation> items = new LinkedHashMap<>();
        Map<ResourceLocation, ResourceLocation> tags = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> file : new TreeMap<>(files).entrySet())
        {
            ResourceLocation fileId = file.getKey();
            if (!file.getValue().isJsonObject())
            {
                warn.accept(fileId + ": expected a JSON object, file skipped");
                continue;
            }
            JsonObject json = file.getValue().getAsJsonObject();
            ResourceLocation family = readId(json, "family");
            if (family == null)
            {
                warn.accept(fileId + ": missing or invalid \"family\", file skipped");
                continue;
            }
            claim(readIds(json, "items", fileId, warn), family, items, fileId, "item", warn);
            claim(readIds(json, "tags", fileId, warn), family, tags, fileId, "tag", warn);
        }
        return new WeaponFamilyIndex(Collections.unmodifiableMap(items), Collections.unmodifiableMap(tags));
    }

    private static void claim(List<ResourceLocation> ids, ResourceLocation family,
                              Map<ResourceLocation, ResourceLocation> target, ResourceLocation fileId,
                              String kind, Consumer<String> warn)
    {
        for (ResourceLocation id : ids)
        {
            ResourceLocation existing = target.putIfAbsent(id, family);
            if (existing != null && !existing.equals(family))
            {
                warn.accept(fileId + ": " + kind + " " + id + " is already assigned to family "
                        + existing + "; keeping the first assignment");
            }
        }
    }

    private static ResourceLocation readId(JsonObject json, String key)
    {
        JsonElement element = json.get(key);
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())
        {
            return null;
        }
        return ResourceLocation.tryParse(element.getAsString());
    }

    private static List<ResourceLocation> readIds(JsonObject json, String key, ResourceLocation fileId,
                                                  Consumer<String> warn)
    {
        JsonElement element = json.get(key);
        if (element == null)
        {
            return List.of();
        }
        if (!element.isJsonArray())
        {
            warn.accept(fileId + ": \"" + key + "\" must be an array, ignored");
            return List.of();
        }
        JsonArray array = element.getAsJsonArray();
        List<ResourceLocation> result = new java.util.ArrayList<>();
        for (JsonElement entry : array)
        {
            ResourceLocation id = entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()
                    ? ResourceLocation.tryParse(entry.getAsString())
                    : null;
            if (id == null)
            {
                warn.accept(fileId + ": invalid id in \"" + key + "\": " + entry + ", entry ignored");
            }
            else
            {
                result.add(id);
            }
        }
        return result;
    }
}
