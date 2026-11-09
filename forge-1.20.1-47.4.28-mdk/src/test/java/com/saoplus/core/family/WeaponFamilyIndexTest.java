package com.saoplus.core.family;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Fictitious ids only (saoplus_test, other_test). */
class WeaponFamilyIndexTest
{
    private static ResourceLocation id(String ns, String path)
    {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }

    private static JsonElement json(String text)
    {
        return JsonParser.parseString(text);
    }

    @Test
    void emptyDataGivesEmptyIndex()
    {
        WeaponFamilyIndex index = WeaponFamilyIndex.build(Map.of(), m -> fail("no warning expected: " + m));
        assertEquals(0, index.size());
        assertTrue(index.familyOfItem(id("other_test", "item_x")).isEmpty());
    }

    @Test
    void itemsAndTagsMapToFamily()
    {
        Map<ResourceLocation, JsonElement> files = new HashMap<>();
        files.put(id("saoplus_test", "one"), json(
                "{\"family\":\"saoplus_test:family_a\",\"items\":[\"other_test:item_x\",\"other_test:item_y\"],"
                        + "\"tags\":[\"other_test:tag_z\"]}"));
        WeaponFamilyIndex index = WeaponFamilyIndex.build(files, m -> fail("no warning expected: " + m));

        assertEquals(id("saoplus_test", "family_a"), index.familyOfItem(id("other_test", "item_x")).orElseThrow());
        assertEquals(id("saoplus_test", "family_a"), index.familyOfItem(id("other_test", "item_y")).orElseThrow());
        assertEquals(id("saoplus_test", "family_a"), index.tagEntries().get(id("other_test", "tag_z")));
        assertEquals(3, index.size());
    }

    @Test
    void conflictKeepsFirstFileByIdOrderAndWarns()
    {
        Map<ResourceLocation, JsonElement> files = new HashMap<>();
        files.put(id("saoplus_test", "b_second"), json(
                "{\"family\":\"saoplus_test:family_b\",\"items\":[\"other_test:item_x\"]}"));
        files.put(id("saoplus_test", "a_first"), json(
                "{\"family\":\"saoplus_test:family_a\",\"items\":[\"other_test:item_x\"]}"));
        List<String> warnings = new ArrayList<>();
        WeaponFamilyIndex index = WeaponFamilyIndex.build(files, warnings::add);

        assertEquals(id("saoplus_test", "family_a"), index.familyOfItem(id("other_test", "item_x")).orElseThrow());
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("already assigned"));
    }

    @Test
    void invalidFilesAndEntriesAreSkippedWithWarnings()
    {
        Map<ResourceLocation, JsonElement> files = new HashMap<>();
        files.put(id("saoplus_test", "not_object"), json("[1,2]"));
        files.put(id("saoplus_test", "no_family"), json("{\"items\":[\"other_test:item_x\"]}"));
        files.put(id("saoplus_test", "bad_family"),
                json("{\"family\":\"Not Valid!\",\"items\":[\"other_test:item_x\"]}"));
        files.put(id("saoplus_test", "mixed"), json(
                "{\"family\":\"saoplus_test:family_a\",\"items\":[\"BAD ID\",5,\"other_test:item_ok\"],"
                        + "\"tags\":\"oops\"}"));
        List<String> warnings = new ArrayList<>();
        WeaponFamilyIndex index = assertDoesNotThrow(() -> WeaponFamilyIndex.build(files, warnings::add));

        assertEquals(1, index.size());
        assertTrue(index.familyOfItem(id("other_test", "item_ok")).isPresent());
        assertTrue(index.familyOfItem(id("other_test", "item_x")).isEmpty());
        // not an object, no family, bad family, two bad item ids, "tags" not an array
        assertEquals(6, warnings.size(), warnings.toString());
    }
}
