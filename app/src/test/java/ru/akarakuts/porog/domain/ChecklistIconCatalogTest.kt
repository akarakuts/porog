package ru.akarakuts.porog.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ChecklistIconCatalogTest {
    @Test
    fun matchesKeysInRussian() {
        assertEquals("keys", ChecklistIconCatalog.match("Ключи"))
        assertEquals("keys", ChecklistIconCatalog.match("ключи от дома"))
    }

    @Test
    fun firstMatchWinsForSunglasses() {
        assertEquals("sunglasses", ChecklistIconCatalog.match("солнцезащитные очки"))
    }

    @Test
    fun matchesUmbrellaAndHeadphones() {
        assertEquals("umbrella", ChecklistIconCatalog.match("Зонт"))
        assertEquals("headphones", ChecklistIconCatalog.match("наушники"))
    }

    @Test
    fun unknownFallsBackToDefault() {
        assertEquals(ChecklistIconCatalog.DEFAULT, ChecklistIconCatalog.match("xyz"))
        assertEquals(ChecklistIconCatalog.DEFAULT, ChecklistIconCatalog.match("   "))
    }
}
