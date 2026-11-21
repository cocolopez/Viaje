package app.viajejapon.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.viajejapon.data.Place
import app.viajejapon.data.Trip
import app.viajejapon.data.fmtDate
import app.viajejapon.data.fmtShort
import app.viajejapon.data.fmtWeekday
import app.viajejapon.data.num
import app.viajejapon.data.numText
import app.viajejapon.data.parseDate
import app.viajejapon.data.parseNum
import java.time.LocalDate

@Composable
fun ItineraryScreen(trip: Trip, pad: PaddingValues, vm: TripViewModel, onSettings: () -> Unit) {
    val start = parseDate(trip.start)
    val end = parseDate(trip.end)
    val days: List<LocalDate> =
        if (start != null && end != null && !end.isBefore(start)) {
            generateSequence(start) { it.plusDays(1) }.takeWhile { !it.isAfter(end) }.take(90).toList()
        } else emptyList()

    var sel by rememberSaveable { mutableStateOf("") }
    var editing by remember { mutableStateOf<Place?>(null) }
    var creating by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    val selected: LocalDate? = days.firstOrNull { it.toString() == sel }
        ?: days.firstOrNull { it == LocalDate.now() }
        ?: days.firstOrNull()

    ScreenFrame(
        kanji = "日",
        title = "Itinerario",
        subtitle = if (days.isEmpty()) "Sin fechas" else "${days.size} días · ${trip.places.size} lugares",
        pad = pad,
        onAdd = if (selected != null) ({ creating = true }) else null,
    ) {
        if (days.isEmpty() || selected == null) {
            item {
                WashiCard {
                    Text("Para armar el itinerario por día, primero definí la fecha de inicio y de fin del viaje.", color = Nezu)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onSettings) { Text("Ir a Ajustes") }
                }
            }
        } else {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(days.size) { i ->
                        val d = days[i]
                        DayChip(
                            n = i + 1,
                            d = d,
                            selected = d == selected,
                            count = trip.places.count { it.date == d.toString() },
                        ) { sel = d.toString() }
                    }
                }
            }
            val dayPlaces = trip.places
                .filter { it.date == selected.toString() }
                .sortedWith(compareBy({ it.time.ifBlank { "99:99" } }, { it.name }))
            val est = dayPlaces.sumOf { it.estYen }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(fmtDate(selected.toString()), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (est > 0) Text("Estimado ¥${num(est)}", color = Nezu)
                }
            }
            if (dayPlaces.isEmpty()) {
                item { EmptyState("Nada planeado para este día.\nTocá ＋ para agregar un lugar.") }
            }
            items(dayPlaces, key = { it.id }) { p ->
                WashiCard(onClick = { editing = p }, pad = 12.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = p.done,
                            onCheckedChange = { vm.togglePlace(p) },
                            colors = CheckboxDefaults.colors(checkedColor = Matcha),
                        )
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (p.time.isNotBlank()) {
                                    Text(p.time, color = Shu, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text(
                                    p.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (p.done) TextDecoration.LineThrough else null,
                                    color = if (p.done) Nezu else Sumi,
                                )
                            }
                            if (p.notes.isNotBlank()) {
                                Text(p.notes, style = MaterialTheme.typography.bodySmall, color = Nezu)
                            }
                            if (p.estYen > 0) {
                                Text("¥${num(p.estYen)}", style = MaterialTheme.typography.bodySmall, color = Ai)
                            }
                        }
                        TextButton(onClick = { openMaps(ctx, p.name + " Japan") }) { Text("Mapa", color = Ai) }
                    }
                }
            }
        }
    }

    if (creating && selected != null) {
        PlaceDialog(
            initial = null,
            defaultDate = selected.toString(),
            onDismiss = { creating = false },
            onSave = { vm.savePlace(it); sel = it.date; creating = false },
        )
    }
    editing?.let { p ->
        PlaceDialog(
            initial = p,
            defaultDate = p.date,
            onDismiss = { editing = null },
            onSave = { vm.savePlace(it); editing = null },
            onDelete = { vm.deletePlace(p.id); editing = null },
        )
    }
}

@Composable
private fun DayChip(n: Int, d: LocalDate, selected: Boolean, count: Int, onClick: () -> Unit) {
    val bg = if (selected) Shu else Paper
    val fg = if (selected) Paper else Sumi
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg,
        border = BorderStroke(1.dp, if (selected) Shu else Line),
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Día $n", fontSize = 11.sp, color = fg)
            Text(fmtShort(d), fontWeight = FontWeight.Bold, color = fg)
            Text(fmtWeekday(d), fontSize = 11.sp, color = fg)
            Text(if (count > 0) "● $count" else " ", fontSize = 10.sp, color = if (selected) Paper else Matcha)
        }
    }
}

@Composable
private fun PlaceDialog(
    initial: Place?,
    defaultDate: String,
    onDismiss: () -> Unit,
    onSave: (Place) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var p by remember { mutableStateOf(initial ?: Place(date = defaultDate)) }
    var est by remember { mutableStateOf(numText(initial?.estYen ?: 0.0)) }

    FormDialog(
        title = if (initial == null) "Nuevo lugar" else "Editar lugar",
        onDismiss = onDismiss,
        onSave = { onSave(p.copy(estYen = parseNum(est))) },
        saveEnabled = p.name.isNotBlank() && p.date.isNotBlank(),
        onDelete = onDelete,
    ) {
        JpField("Lugar (Fushimi Inari, Shibuya…)", p.name, { p = p.copy(name = it) })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateButton("Día", p.date, { p = p.copy(date = it) }, Modifier.weight(1.4f))
            TimeButton("Hora", p.time, { p = p.copy(time = it) }, Modifier.weight(1f))
        }
        JpField("Costo estimado (¥)", est, { est = it }, keyboard = KeyboardType.Decimal)
        JpField("Notas", p.notes, { p = p.copy(notes = it) }, singleLine = false)
    }
}
