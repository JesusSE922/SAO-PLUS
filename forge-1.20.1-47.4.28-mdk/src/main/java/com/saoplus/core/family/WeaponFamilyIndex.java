package com.saoplus.core.family;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Immutable lookup from item ids and item tags to weapon family ids, built from datapack files
 * {@code data/<namespace>/saoplus/weapon_families/<name>.json}:
 * <pre>{"family": "namespace:id", "items": ["mod:item"], "tags": ["mod:tag"],
 *  "categories": ["epicfight:greatsword"], "stats": {"key": 1.0}, "family_stats": {"key": 1.0}}</pre>
 * {@code categories} are external weapon classes reported by a registered {@link WeaponCategorySource}
 * (the Epic Fight adapter reports {@code epicfight:<category>}). {@code stats} is an opaque numeric map
 * attached to every listed item; SAO+ stores and exposes it but assigns it no meaning yet.
 * SAO+ ships no families: the catalog is defined by data, so no family or weapon id is hard-coded.
 */
public final class WeaponFamilyIndex
{
    public static final WeaponFamilyIndex EMPTY =
            new WeaponFamilyIndex(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());

    private final Map<ResourceLocation, ResourceLocation> byItem;
    private final Map<ResourceLocation, ResourceLocation> byTag;
    private final Map<ResourceLocation, ResourceLocation> byCategory;
    private final Map<ResourceLocation, Map<String, Double>> statsByItem;
    private final Map<ResourceLocation, Map<String, Double>> familyStats;
    private final Map<ResourceLocation, FamilyRule> rules;

    private WeaponFamilyIndex(Map<ResourceLocation, ResourceLocation> byItem,
                              Map<ResourceLocation, ResourceLocation> byTag,
                              Map<ResourceLocation, ResourceLocation> byCategory,
                              Map<ResourceLocation, Map<String, Double>> statsByItem,
                              Map<ResourceLocation, Map<String, Double>> familyStats,
                              Map<ResourceLocation, FamilyRule> rules)
    {
        this.rules = rules;
        this.familyStats = familyStats;
        this.byItem = byItem;
        this.byTag = byTag;
        this.byCategory = byCategory;
        this.statsByItem = statsByItem;
    }

    public Optional<ResourceLocation> familyOfCategory(ResourceLocation category)
    {
        return Optional.ofNullable(byCategory.get(category));
    }

    public FamilyRule ruleOf(ResourceLocation family)
    {
        return rules.getOrDefault(family, FamilyRule.NONE);
    }

    public Map<ResourceLocation, FamilyRule> rules()
    {
        return rules;
    }

    /** Default stats of a family ("family_stats" in data); empty map when none. */
    public Map<String, Double> statsOfFamily(ResourceLocation family)
    {
        return familyStats.getOrDefault(family, Map.of());
    }

    /** Opaque stats attached to an item by data; empty map when none. */
    public Map<String, Double> statsOfItem(ResourceLocation itemId)
    {
        return statsByItem.getOrDefault(itemId, Map.of());
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
        return byItem.size() + byTag.size() + byCategory.size();
    }

    /**
     * Builds an index from parsed files. Files are processed in id order so conflicts resolve
     * deterministically: the first file to claim an item or tag wins and later claims are reported.
     */
    public static WeaponFamilyIndex build(Map<ResourceLocation, JsonElement> files, Consumer<String> warn)
    {
        Map<ResourceLocation, ResourceLocation> items = new LinkedHashMap<>();
        Map<ResourceLocation, ResourceLocation> tags = new LinkedHashMap<>();
        Map<ResourceLocation, ResourceLocation> categories = new LinkedHashMap<>();
        Map<ResourceLocation, Map<String, Double>> stats = new HashMap<>();
        Map<ResourceLocation, Map<String, Double>> familyStats = new HashMap<>();
        Map<ResourceLocation, FamilyRule> rules = new HashMap<>();

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
            List<ResourceLocation> fileItems = readIds(json, "items", fileId, warn);
            claim(fileItems, family, items, fileId, "item", warn);
            claim(readIds(json, "categories", fileId, warn), family, categories, fileId, "category", warn);
            FamilyRule rule = FamilyRule.parse(json, fileId, warn);
            if (rule != FamilyRule.NONE && rules.putIfAbsent(family, rule) != null)
            {
                warn.accept(fileId + ": rules for " + family + " already defined, keeping the first");
            }
            Map<String, Double> defaults = readStats(json, "family_stats", fileId, warn);
            if (!defaults.isEmpty() && familyStats.putIfAbsent(family, defaults) != null)
            {
                warn.accept(fileId + ": family_stats for " + family + " already defined, keeping the first");
            }
            Map<String, Double> fileStats = readStats(json, "stats", fileId, warn);
            if (!fileStats.isEmpty())
            {
                for (ResourceLocation item : fileItems)
                {
                    // Only the file that owns the item (the family that won it) contributes stats.
                    if (family.equals(items.get(item)))
                    {
                        stats.computeIfAbsent(item, id -> new java.util.TreeMap<>()).putAll(fileStats);
                    }
                }
            }
            claim(readIds(json, "tags", fileId, warn), family, tags, fileId, "tag", warn);
        }
        return new WeaponFamilyIndex(Collections.unmodifiableMap(items), Collections.unmodifiableMap(tags),
                Collections.unmodifiableMap(categories), Collections.unmodifiableMap(stats), Collections.unmodifiableMap(familyStats), Collections.unmodifiableMap(rules));
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

    private static Map<String, Double> readStats(JsonObject json, String key, ResourceLocation fileId, Consumer<String> warn)
    {
        JsonElement element = json.get(key);
        if (element == null)
        {
            return Map.of();
        }
        if (!element.isJsonObject())
        {
            warn.accept(fileId + ": \"" + key + "\" must be an object, ignored");
            return Map.of();
        }
        Map<String, Double> result = new java.util.TreeMap<>();
        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
        {
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()
                    && Double.isFinite(value.getAsDouble()))
            {
                result.put(entry.getKey(), value.getAsDouble());
            }
            else
            {
                warn.accept(fileId + ": stat \"" + entry.getKey() + "\" is not a finite number, ignored");
            }
        }
        return result;
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
