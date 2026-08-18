/** ChecklistVisuals — библиотека Material-иконок чеклиста по id каталога. */
package ru.akarakuts.porog.ui.checklist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Masks
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.ui.graphics.vector.ImageVector
import ru.akarakuts.porog.domain.ChecklistIconCatalog

fun checklistIcon(id: String?): ImageVector = when (id) {
    "keys" -> Icons.Filled.VpnKey
    "wallet" -> Icons.Filled.AccountBalanceWallet
    "card" -> Icons.Filled.CreditCard
    "pass" -> Icons.Filled.Badge
    "headphones" -> Icons.Filled.Headphones
    "charger" -> Icons.Filled.BatteryChargingFull
    "pills" -> Icons.Filled.Medication
    "phone" -> Icons.Filled.Smartphone
    "umbrella" -> Icons.Filled.Umbrella
    "glasses" -> Icons.Filled.Visibility
    "sunglasses" -> Icons.Filled.WbSunny
    "documents" -> Icons.Filled.Description
    "laptop" -> Icons.Filled.Laptop
    "bag" -> Icons.Filled.Backpack
    "watch" -> Icons.Filled.Watch
    "bottle" -> Icons.Filled.LocalDrink
    "jacket" -> Icons.Filled.Checkroom
    "hat" -> Icons.Filled.AcUnit
    "gloves" -> Icons.Filled.PanTool
    "mask" -> Icons.Filled.Masks
    "cosmetics" -> Icons.Filled.Brush
    "notebook" -> Icons.Filled.EditNote
    "child" -> Icons.Filled.ChildCare
    "pet" -> Icons.Filled.Pets
    "work" -> Icons.Filled.Work
    "food" -> Icons.Filled.LunchDining
    "gym" -> Icons.Filled.FitnessCenter
    else -> Icons.AutoMirrored.Filled.Label
}

fun catalogIconIds(): List<String> =
    ChecklistIconCatalog.entries.map { it.id }.filter { it != ChecklistIconCatalog.DEFAULT } +
        ChecklistIconCatalog.DEFAULT
