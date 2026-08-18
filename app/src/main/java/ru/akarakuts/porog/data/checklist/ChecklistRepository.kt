package ru.akarakuts.porog.data.checklist

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import ru.akarakuts.porog.R
import ru.akarakuts.porog.data.local.SettingsStore
import ru.akarakuts.porog.domain.ChecklistIconCatalog
import ru.akarakuts.porog.domain.ChecklistItem
import java.util.concurrent.TimeUnit

class ChecklistRepository(
    private val context: Context,
    private val settings: SettingsStore,
) {
    suspend fun items(): List<ChecklistItem> {
        ensureSeeded()
        maybeResetDay()
        return parse(settings.checklistJson()).map { it.toDomain() }
    }

    suspend fun toggle(id: Long, checked: Boolean) {
        val rows = parse(settings.checklistJson()).map {
            if (it.id == id) it.copy(checked = checked) else it
        }
        settings.setChecklistJson(serialize(rows))
    }

    suspend fun clearChecks() {
        val rows = parse(settings.checklistJson()).map { it.copy(checked = false) }
        settings.setChecklistJson(serialize(rows))
    }

    suspend fun addCustom(title: String, icon: String? = null) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        ensureSeeded()
        val rows = parse(settings.checklistJson()).toMutableList()
        val nextId = (rows.maxOfOrNull { it.id } ?: 0L) + 1
        val nextOrder = (rows.maxOfOrNull { it.sortOrder } ?: 0) + 1
        val iconId = icon?.takeIf { it.isNotBlank() } ?: ChecklistIconCatalog.match(trimmed)
        rows.add(Row(nextId, null, trimmed, false, nextOrder, iconId))
        settings.setChecklistJson(serialize(rows))
    }

    suspend fun deleteItem(id: Long) {
        val rows = parse(settings.checklistJson()).filterNot { it.id == id }
        settings.setChecklistJson(serialize(rows))
    }

    private suspend fun ensureSeeded() {
        if (settings.checklistJson().isNotBlank()) return
        val seed = DEFAULT_KEYS.mapIndexed { index, key ->
            Row(id = index + 1L, labelKey = key, customLabel = null, checked = false, sortOrder = index, icon = key)
        }
        settings.setChecklistJson(serialize(seed))
    }

    private suspend fun maybeResetDay() {
        val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        val last = settings.current().lastChecklistEpochDay
        if (last != today) {
            val rows = parse(settings.checklistJson()).map { it.copy(checked = false) }
            settings.setChecklistJson(serialize(rows))
            settings.setChecklistEpochDay(today)
        }
    }

    private fun Row.toDomain(): ChecklistItem {
        val builtIn = labelKey != null
        val title = if (labelKey != null) {
            val resId = context.resources.getIdentifier(
                "checklist_item_$labelKey",
                "string",
                context.packageName,
            )
            if (resId != 0) context.getString(resId) else customLabel.orEmpty()
        } else {
            customLabel ?: context.getString(R.string.checklist_item_custom)
        }
        return ChecklistItem(
            id = id,
            title = title,
            checked = checked,
            builtIn = builtIn,
            key = labelKey,
            icon = icon ?: labelKey ?: ChecklistIconCatalog.match(title),
        )
    }

    private data class Row(
        val id: Long,
        val labelKey: String?,
        val customLabel: String?,
        val checked: Boolean,
        val sortOrder: Int,
        val icon: String?,
    )

    private fun parse(json: String): List<Row> {
        if (json.isBlank()) return emptyList()
        val arr = JSONArray(json)
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Row(
                id = o.getLong("id"),
                labelKey = o.optString("key").ifBlank { null },
                customLabel = o.optString("custom").ifBlank { null },
                checked = o.optBoolean("checked"),
                sortOrder = o.optInt("order"),
                icon = o.optString("icon").ifBlank { null },
            )
        }
    }

    private fun serialize(rows: List<Row>): String {
        val arr = JSONArray()
        rows.sortedBy { it.sortOrder }.forEach { row ->
            arr.put(
                JSONObject()
                    .put("id", row.id)
                    .put("key", row.labelKey ?: "")
                    .put("custom", row.customLabel ?: "")
                    .put("checked", row.checked)
                    .put("order", row.sortOrder)
                    .put("icon", row.icon ?: ""),
            )
        }
        return arr.toString()
    }

    companion object {
        val DEFAULT_KEYS = listOf("keys", "wallet", "pass", "headphones", "charger", "pills")
    }
}
