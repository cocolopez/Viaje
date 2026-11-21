@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package app.viajejapon.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.viajejapon.data.CUR_BASE
import app.viajejapon.data.CUR_JPY
import app.viajejapon.data.baseMoney
import app.viajejapon.data.fmtDate
import app.viajejapon.data.parseDate
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale

// ---------- Categorías de gasto ----------

data class Cat(val key: String, val kanji: String, val label: String)

val Categories = listOf(
    Cat("comida", "食", "Comida"),
    Cat("transporte", "電", "Transporte"),
    Cat("alojamiento", "宿", "Alojamiento"),
    Cat("actividades", "遊", "Actividades"),
    Cat("compras", "買", "Compras"),
    Cat("otros", "他", "Otros"),
)

fun categoryOf(key: String): Cat = Categories.firstOrNull { it.key == key } ?: Categories.last()

// ---------- Decoración japonesa ----------

/** Sello rojo con un kanji (hanko). */
@Composable
fun Hanko(text: String, modifier: Modifier = Modifier, size: Dp = 36.dp, color: Color = Shu) {
    Box(
        modifier
            .size(size)
            .rotate(-3f)
            .background(color, RoundedCornerShape(size / 8)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = Paper,
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
        )
    }
}

/** Patrón de olas (seigaiha). */
@Composable
fun Seigaiha(modifier: Modifier, bg: Color, ring: Color) {
    Canvas(modifier) {
        val w = 44.dp.toPx()
        val r = w / 2f
        val rows = (size.height / (r / 2f)).toInt() + 2
        val cols = (size.width / w).toInt() + 2
        for (row in 0..rows) {
            val cy = row * r / 2f
            val offset = if (row % 2 == 0) 0f else r
            for (col in -1..cols) {
                val c = Offset(col * w + offset, cy)
                drawCircle(bg, r, c)
                for (k in listOf(1f, 0.72f, 0.44f, 0.16f)) {
                    drawCircle(ring, r * k, c, style = Stroke(1.2.dp.toPx()))
                }
            }
        }
    }
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            Line,
            Offset(0f, 0f),
            Offset(size.width, 0f),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
        )
    }
}

// ---------- Contenedores ----------

@Composable
fun WashiCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    pad: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        color = Paper,
        border = BorderStroke(1.dp, Line),
    ) {
        Column(
            Modifier
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(pad),
            content = content,
        )
    }
}

@Composable
fun SectionTitle(kanji: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Hanko(kanji, size = 26.dp)
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EmptyState(text: String) {
    WashiCard {
        Text(
            text,
            Modifier.fillMaxWidth(),
            color = Nezu,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/** Marco común de las pestañas: título con hanko, lista y botón flotante. */
@Composable
fun ScreenFrame(
    kanji: String,
    title: String,
    subtitle: String?,
    pad: PaddingValues,
    onAdd: (() -> Unit)?,
    content: LazyListScope.() -> Unit,
) {
    Box(Modifier.fillMaxSize().padding(pad)) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    Modifier.statusBarsPadding().padding(top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Hanko(kanji, size = 42.dp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        if (subtitle != null) {
                            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Nezu)
                        }
                    }
                }
            }
            content()
        }
        if (onAdd != null) {
            FloatingActionButton(
                onClick = onAdd,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = Shu,
                contentColor = Paper,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("＋", fontSize = 24.sp)
            }
        }
    }
}

// ---------- Medidor de presupuesto ----------

@Composable
fun BudgetMeter(spent: Double, total: Double, base: String, modifier: Modifier = Modifier) {
    val frac = if (total > 0) (spent / total).toFloat() else 0f
    val anim by animateFloatAsState(frac.coerceIn(0f, 1f), tween(700), label = "meter")
    val color = when {
        frac >= 1f -> Shu
        frac >= 0.75f -> Kin
        else -> Matcha
    }
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column {
                Text("Gastado", style = MaterialTheme.typography.labelMedium, color = Nezu)
                Text(baseMoney(spent, base), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Presupuesto", style = MaterialTheme.typography.labelMedium, color = Nezu)
                Text(baseMoney(total, base), style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.height(10.dp))
        Canvas(Modifier.fillMaxWidth().height(18.dp)) {
            val h = size.height
            val cr = CornerRadius(h / 2f)
            drawRoundRect(Line, size = size, cornerRadius = cr)
            if (anim > 0f) {
                drawRoundRect(color, size = Size(maxOf(h, size.width * anim), h), cornerRadius = cr)
            }
            for (t in listOf(0.25f, 0.5f, 0.75f)) {
                val x = size.width * t
                drawLine(
                    Paper.copy(alpha = 0.8f),
                    Offset(x, 3.dp.toPx()),
                    Offset(x, h - 3.dp.toPx()),
                    strokeWidth = 1.5.dp.toPx(),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${(frac * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
            val msg = when {
                total <= 0 -> "Sin presupuesto definido"
                spent <= total -> "Quedan ${baseMoney(total - spent, base)}"
                else -> "Excedido por ${baseMoney(spent - total, base)}"
            }
            Text(msg, style = MaterialTheme.typography.labelMedium, color = if (spent > total && total > 0) Shu else Nezu)
        }
    }
}

// ---------- Formularios ----------

@Composable
fun JpField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboard: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
    )
}

@Composable
fun DateButton(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { open = true }, modifier = modifier, shape = RoundedCornerShape(6.dp)) {
        Text(if (value.isBlank()) label else fmtDate(value), maxLines = 1)
    }
    if (open) {
        val initial = parseDate(value)?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val st = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    st.selectedDateMillis?.let {
                        onChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString())
                    }
                    open = false
                }) { Text("OK") }
            },
            dismissButton = {
                Row {
                    if (value.isNotBlank()) {
                        TextButton(onClick = { onChange(""); open = false }) { Text("Borrar") }
                    }
                    TextButton(onClick = { open = false }) { Text("Cancelar") }
                }
            },
        ) { DatePicker(state = st) }
    }
}

@Composable
fun TimeButton(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { open = true }, modifier = modifier, shape = RoundedCornerShape(6.dp)) {
        Text(if (value.isBlank()) label else value, maxLines = 1)
    }
    if (open) {
        val parts = value.split(":")
        val st = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 12,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    onChange(String.format(Locale.US, "%02d:%02d", st.hour, st.minute))
                    open = false
                }) { Text("OK") }
            },
            dismissButton = {
                Row {
                    if (value.isNotBlank()) {
                        TextButton(onClick = { onChange(""); open = false }) { Text("Borrar") }
                    }
                    TextButton(onClick = { open = false }) { Text("Cancelar") }
                }
            },
            text = { TimePicker(state = st) },
        )
    }
}

@Composable
fun CurrencyChips(cur: String, base: String, onChange: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = cur == CUR_JPY, onClick = { onChange(CUR_JPY) }, label = { Text("¥ JPY") })
        FilterChip(selected = cur == CUR_BASE, onClick = { onChange(CUR_BASE) }, label = { Text(base) })
    }
}

@Composable
fun FormDialog(
    title: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    saveEnabled: Boolean = true,
    onDelete: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content,
            )
        },
        confirmButton = { Button(onClick = onSave, enabled = saveEnabled) { Text("Guardar") } },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = { confirmDelete = true }) { Text("Eliminar", color = Shu) }
                }
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        },
    )
    if (confirmDelete && onDelete != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("¿Eliminar?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = onDelete) { Text("Eliminar", color = Shu) } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } },
        )
    }
}

// ---------- Mapas ----------

fun openMaps(ctx: Context, query: String) {
    val uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
    runCatching {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
