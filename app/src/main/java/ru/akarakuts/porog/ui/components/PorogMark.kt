/** PorogMark — значок двери с янтарной ручкой и порогом; шапка и «О программе». */
package ru.akarakuts.porog.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.akarakuts.porog.ui.theme.Amber
import ru.akarakuts.porog.ui.theme.Cream
import ru.akarakuts.porog.ui.theme.TealDeep
import ru.akarakuts.porog.ui.theme.TealMid

@Composable
fun PorogMark(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    frame: Color = TealDeep,
    door: Color = TealMid,
    cream: Color = Cream,
    knob: Color = Amber,
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val r = w * 0.08f
        drawRoundRect(
            color = cream,
            topLeft = Offset(w * 0.22f, h * 0.12f),
            size = Size(w * 0.56f, h * 0.68f),
            cornerRadius = CornerRadius(r, r),
        )
        drawRoundRect(
            color = frame,
            topLeft = Offset(w * 0.26f, h * 0.16f),
            size = Size(w * 0.48f, h * 0.60f),
            cornerRadius = CornerRadius(r * 0.7f, r * 0.7f),
        )
        drawRoundRect(
            color = door,
            topLeft = Offset(w * 0.30f, h * 0.20f),
            size = Size(w * 0.40f, h * 0.52f),
            cornerRadius = CornerRadius(r * 0.5f, r * 0.5f),
        )
        drawCircle(
            color = knob,
            radius = w * 0.045f,
            center = Offset(w * 0.62f, h * 0.48f),
        )
        drawRoundRect(
            color = knob,
            topLeft = Offset(w * 0.20f, h * 0.78f),
            size = Size(w * 0.60f, h * 0.07f),
            cornerRadius = CornerRadius(r * 0.4f, r * 0.4f),
        )
    }
}
