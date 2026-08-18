/** ChecklistIconCatalog — id значков и подбор по смыслу названия (первый матч). */
package ru.akarakuts.porog.domain

import java.util.Locale

object ChecklistIconCatalog {
    const val DEFAULT = "item"

    data class Entry(val id: String, val keywords: List<String>)

    val entries: List<Entry> = listOf(
        Entry("sunglasses", listOf("солнцезащит", "sunglasses", "темные очки", "тёмные очки")),
        Entry("glasses", listOf("очк", "очки", "glasses", "линз")),
        Entry("keys", listOf("ключ", "keys", "key", "связк")),
        Entry("wallet", listOf("кошел", "портмоне", "wallet", "деньг")),
        Entry("card", listOf("карт", "card", "картхолдер")),
        Entry("pass", listOf("пропуск", "бейдж", "badge", "pass", "удостоверен")),
        Entry("headphones", listOf("наушник", "headphones", "airpods", "гарнитур")),
        Entry("charger", listOf("зарядк", "charger", "повербанк", "powerbank", "кабель", "cable", "power bank")),
        Entry("pills", listOf("лекар", "таблет", "pills", "medicine", "витамин", "medication")),
        Entry("phone", listOf("телефон", "смартфон", "phone", "mobile")),
        Entry("umbrella", listOf("зонт", "umbrella")),
        Entry("documents", listOf("паспорт", "документ", "права", "снилс", "полис", "passport", "document")),
        Entry("laptop", listOf("ноутбук", "laptop", "макбук", "macbook", "нетбук")),
        Entry("bag", listOf("рюкзак", "сумк", "портфель", "backpack", "bag")),
        Entry("watch", listOf("часы", "watch", "apple watch")),
        Entry("bottle", listOf("бутыл", "термос", "вода", "water", "bottle")),
        Entry("jacket", listOf("куртк", "пальто", "пуховик", "jacket", "coat")),
        Entry("hat", listOf("шапк", "кепк", "шляп", "hat", "cap")),
        Entry("gloves", listOf("перчат", "gloves", "варежк")),
        Entry("mask", listOf("маск", "mask", "респиратор")),
        Entry("cosmetics", listOf("помад", "косметик", "крем", "lipstick", "makeup")),
        Entry("notebook", listOf("блокнот", "тетрад", "ежедневник", "notebook")),
        Entry("child", listOf("детск", "ребён", "ребен", "соска", "child", "baby")),
        Entry("pet", listOf("поводок", "корм", "питомец", "leash", "pet")),
        Entry("work", listOf("работ", "офис", "work", "office")),
        Entry("food", listOf("обед", "ланч", "еда", "ланчбокс", "lunch", "food")),
        Entry("gym", listOf("спорт", "зал", "фитнес", "gym", "fitness")),
        Entry("item", listOf()),
    )

    fun match(title: String): String {
        val n = normalize(title)
        if (n.isBlank()) return DEFAULT
        return entries.firstOrNull { e ->
            e.keywords.any { kw -> n.contains(kw) }
        }?.id ?: DEFAULT
    }

    fun normalize(text: String): String =
        text.lowercase(Locale.getDefault()).replace('ё', 'е').trim()
}
