package app.viajejapon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.viajejapon.data.Flight
import app.viajejapon.data.Trip
import app.viajejapon.data.fmtDate
import app.viajejapon.data.money
import app.viajejapon.data.numText
import app.viajejapon.data.parseNum

@Composable
fun FlightsScreen(trip: Trip, pad: PaddingValues, vm: TripViewModel) {
    var editing by remember { mutableStateOf<Flight?>(null) }
    var creating by remember { mutableStateOf(false) }

    ScreenFrame(
        kanji = "便",
        title = "Vuelos",
        subtitle = "${trip.flights.size} registrados",
        pad = pad,
        onAdd = { creating = true },
    ) {
        if (trip.flights.isEmpty()) {
            item { EmptyState("Todavía no cargaste vuelos.\nTocá ＋ para agregar la ida y la vuelta.") }
        }
        items(trip.flights.sortedBy { it.depDate + it.depTime }, key = { it.id }) { f ->
            FlightCard(f, trip) { editing = f }
        }
    }

    if (creating) {
        FlightDialog(
            initial = null,
            trip = trip,
            onDismiss = { creating = false },
            onSave = { vm.saveFlight(it); creating = false },
        )
    }
    editing?.let { f ->
        FlightDialog(
            initial = f,
            trip = trip,
            onDismiss = { editing = null },
            onSave = { vm.saveFlight(it); editing = null },
            onDelete = { vm.deleteFlight(f.id); editing = null },
        )
    }
}

@Composable
private fun FlightCard(f: Flight, trip: Trip, onClick: () -> Unit) {
    WashiCard(onClick = onClick) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                listOf(f.airline, f.number).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "Vuelo" },
                style = MaterialTheme.typography.labelLarge,
                color = Nezu,
            )
            if (f.cost > 0) {
                Text(money(f.cost, f.cur, trip.baseCurrency), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                f.from.ifBlank { "?" },
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text("→", fontSize = 24.sp, color = Shu)
            Text(
                f.to.ifBlank { "?" },
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )
        }
        Spacer(Modifier.height(8.dp))
        DashedDivider()
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column2("Salida", f.depDate, f.depTime)
            Column2("Llegada", f.arrDate, f.arrTime)
        }
        if (f.confirmation.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text("Reserva: ${f.confirmation}", style = MaterialTheme.typography.bodySmall, color = Ai, fontWeight = FontWeight.Bold)
        }
        if (f.notes.isNotBlank()) {
            Text(f.notes, style = MaterialTheme.typography.bodySmall, color = Nezu, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun Column2(label: String, date: String, time: String) {
    androidx.compose.foundation.layout.Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Nezu)
        Text(if (date.isBlank()) "—" else fmtDate(date), style = MaterialTheme.typography.bodyMedium)
        if (time.isNotBlank()) Text(time, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FlightDialog(
    initial: Flight?,
    trip: Trip,
    onDismiss: () -> Unit,
    onSave: (Flight) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var f by remember { mutableStateOf(initial ?: Flight()) }
    var cost by remember { mutableStateOf(numText(initial?.cost ?: 0.0)) }

    FormDialog(
        title = if (initial == null) "Nuevo vuelo" else "Editar vuelo",
        onDismiss = onDismiss,
        onSave = { onSave(f.copy(cost = parseNum(cost))) },
        saveEnabled = f.from.isNotBlank() && f.to.isNotBlank(),
        onDelete = onDelete,
    ) {
        JpField("Aerolínea", f.airline, { f = f.copy(airline = it) })
        JpField("N° de vuelo", f.number, { f = f.copy(number = it) })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JpField("Desde (EZE)", f.from, { f = f.copy(from = it.uppercase()) }, Modifier.weight(1f))
            JpField("Hacia (NRT)", f.to, { f = f.copy(to = it.uppercase()) }, Modifier.weight(1f))
        }
        Text("Salida", style = MaterialTheme.typography.labelMedium, color = Nezu)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateButton("Fecha", f.depDate, { f = f.copy(depDate = it) }, Modifier.weight(1.4f))
            TimeButton("Hora", f.depTime, { f = f.copy(depTime = it) }, Modifier.weight(1f))
        }
        Text("Llegada", style = MaterialTheme.typography.labelMedium, color = Nezu)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateButton("Fecha", f.arrDate, { f = f.copy(arrDate = it) }, Modifier.weight(1.4f))
            TimeButton("Hora", f.arrTime, { f = f.copy(arrTime = it) }, Modifier.weight(1f))
        }
        JpField("Código de reserva", f.confirmation, { f = f.copy(confirmation = it) })
        JpField("Costo", cost, { cost = it }, keyboard = KeyboardType.Decimal)
        CurrencyChips(f.cur, trip.baseCurrency) { f = f.copy(cur = it) }
        JpField("Notas", f.notes, { f = f.copy(notes = it) }, singleLine = false)
    }
}
