package com.saoplus.core.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeaponMasteryServiceTest
{
    @Test
    void experienceAdditionSaturatesInsteadOfOverflowing()
    {
        assertEquals(15L, WeaponMasteryService.saturatedAdd(10L, 5L));
        assertEquals(Long.MAX_VALUE, WeaponMasteryService.saturatedAdd(Long.MAX_VALUE - 1, 100L));
        assertEquals(Long.MAX_VALUE, WeaponMasteryService.saturatedAdd(Long.MAX_VALUE, 1L));
    }
}
