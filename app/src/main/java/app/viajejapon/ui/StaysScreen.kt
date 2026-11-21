package app.viajejapon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.viajejapon.data.Stay
import app.viajejapon.data.Trip
import app.viajejapon.data.fmtDate
import app.viajejapon.data.money
import app.viajejapon.data.numText
import app.viajejapon.data.parseDate
import app.viajejapon.data.parseNum
import java.time.temporal.ChronoUnit

private fun nightsOf(s: Stay): Long {
    val a = parseDate(s.checkIn)
    val b = parseDate(s.checkOut)
    return if (a != null && b != null) maxOf(0L, ChronoUnit.DAYS.between(a, b)) else 0L
}

@Composable
fun StaysScreen(trip: Trip, pad: PaddingValues, vm: TripViewModel) {
    var editing by remember { mutableStateOf<Stay?>(null) }
    var creating by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    ScreenFrame(
        kanji = "宿",
        title = "Estadía",
        subtitle = "${trip.stays.sumOf { nightsOf(it) }} noches en ${trip.stays.size} alojamientos",
        pad = pad,
        onAdd = { creating = true },
    ) {
        if (trip.stays.isEmpty()) {
            item { EmptyState("Todavía no cargaste alojamientos.\nTocá ＋ para agregar un hotel, ryokan o departamento.") }
        }
        items(trip.stays.sortedBy { it.checkIn }, key = { it.id }) { s ->
            WashiCard(onClick = { editing = s }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(s.name.ifBlank { "Alojamiento" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (s.city.isNotBlank()) Text(s.city, color = Ai, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (s.cost > 0) Text(money(s.cost, s.cur, trip.baseCurrency), fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                DashedDivider()
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Check-in", style = MaterialTheme.typography.labelSmall, color = Nezu)
                        Text(if (s.checkIn.isBlank()) "—" else fmtDate(s.checkIn))
                    }
                    Text("${nightsOf(s)} 泊", color = Shu, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterVertically))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Check-out", style = MaterialTheme.typography.labelSmall, color = Nezu)
                        Text(if (s.checkOut.isBlank()) "—" else fmtDate(s.checkOut))
                    }
                }
                if (s.address.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(s.address, style = MaterialTheme.typography.bodySmall, color = Nezu)
                }
                if (s.confirmation.isNotBlank()) {
                    Text("Reserva: ${s.confirmation}", style = MaterialTheme.typography.bodySmall, color = Ai, fontWeight = FontWeight.Bold)
                }
                if (s.notes.isNotBlank()) {
                    Text(s.notes, style = MaterialTheme.typography.bodySmall, color = Nezu)
                }
                TextButton(onClick = {
                    openMaps(ctx, listOf(s.name, s.address.ifBlank { s.city }).filter { it.isNotBlank() }.joinToString(" "))
                }) { Text("Abrir en Maps", color = Ai) }
            }
        }
    }

    if (creating) {
        StayDialog(null, trip, { creating = false }, { vm.saveStay(it); creating = false })
    }
    editing?.let { s ->
        StayDialog(
            initial = s,
            trip = trip,
            onDismiss = { editing = null },
            onSave = { vm.saveStay(it); editing = null },
            onDelete = { vm.deleteStay(s.id); editing = null },
        )
    }
}

@Composable
private fun StayDialog(
    initial: Stay?,
    trip: Trip,
    onDismiss: () -> Unit,
    onSave: (Stay) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var s by remember { mutableStateOf(initial ?: Stay()) }
    var cost by remember { mutableStateOf(numText(initial?.cost ?: 0.0)) }

    FormDialog(
        title = if (initial == null) "Nuevo alojamiento" else "Editar alojamiento",
        onDismiss = onDismiss,
        onSave = { onSave(s.copy(cost = parseNum(cost))) },
        saveEnabled = s.name.isNotBlank(),
        onDelete = onDelete,
    ) {
        JpField("Nombre", s.name, { s = s.copy(name = it) })
        JpField("Ciudad (Tokio, Kioto…)", s.city, { s = s.copy(city = it) })
        JpField("Dirección", s.address, { s = s.copy(address = it) })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateButton("Check-in", s.checkIn, { s = s.copy(checkIn = it) }, Modifier.weight(1f))
            DateButton("Check-out", s.checkOut, { s = s.copy(checkOut = it) }, Modifier.weight(1f))
        }
        JpField("Código de reserva", s.confirmation, { s = s.copy(confirmation = it) })
        JpField("Costo total", cost, { cost = it }, keyboard = KeyboardType.Decimal)
        CurrencyChips(s.cur, trip.baseCurrency) { s = s.copy(cur = it) }
        JpField("Notas", s.notes, { s = s.copy(notes = it) }, singleLine = false)
    }
}
