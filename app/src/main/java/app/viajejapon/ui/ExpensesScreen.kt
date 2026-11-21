@file:OptIn(ExperimentalLayoutApi::class)

package app.viajejapon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.viajejapon.data.CUR_JPY
import app.viajejapon.data.Expense
import app.viajejapon.data.Trip
import app.viajejapon.data.balances
import app.viajejapon.data.baseMoney
import app.viajejapon.data.bookingsBase
import app.viajejapon.data.expensesBase
import app.viajejapon.data.fmtDate
import app.viajejapon.data.money
import app.viajejapon.data.numText
import app.viajejapon.data.parseNum
import app.viajejapon.data.settle
import app.viajejapon.data.spentBase
import app.viajejapon.data.toBase
import app.viajejapon.data.yenWithoutRate
import java.time.LocalDate

@Composable
fun ExpensesScreen(trip: Trip, pad: PaddingValues, vm: TripViewModel, onSettings: () -> Unit) {
    var editing by remember { mutableStateOf<Expense?>(null) }
    var creating by remember { mutableStateOf(false) }
    val base = trip.baseCurrency
    val nameOf = { id: String -> trip.travelers.firstOrNull { it.id == id }?.name?.ifBlank { "Viajero" } ?: "—" }

    ScreenFrame(
        kanji = "円",
        title = "Gastos",
        subtitle = "${trip.expenses.size} movimientos",
        pad = pad,
        art = ArtKind.Waves,
        onAdd = { creating = true },
    ) {
        // Medidor
        item {
            WashiCard {
                if (trip.budget > 0) {
                    BudgetMeter(trip.spentBase(), trip.budget, base)
                } else {
                    Text("Definí un presupuesto total para ver cómo se llena la barra.", color = Nezu)
                    Spacer(Modifier.height(8.dp))
                    androidx.compose.material3.Button(onClick = onSettings) { Text("Definir presupuesto") }
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gastos: ${baseMoney(trip.expensesBase(), base)}", style = MaterialTheme.typography.bodySmall, color = Nezu)
                    Text(
                        "Vuelos y estadía: ${baseMoney(trip.bookingsBase(), base)}" + if (trip.countBookings) "" else " (no suma)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Nezu,
                    )
                }
                if (trip.yenWithoutRate()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Falta la cotización del yen (1 ¥ = ? $base) para sumar los gastos en ¥. Configurala en Ajustes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Shu,
                    )
                }
            }
        }

        // Saldos entre viajeros
        if (trip.travelers.size > 1 && trip.expenses.isNotEmpty()) {
            item {
                WashiCard {
                    SectionTitle("割", "Saldos")
                    Spacer(Modifier.height(8.dp))
                    val net = trip.balances()
                    trip.travelers.forEach { t ->
                        val v = net[t.id] ?: 0.0
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Text(t.name.ifBlank { "Viajero" }, Modifier.weight(1f))
                            Text(
                                when {
                                    v > 0.5 -> "le deben ${baseMoney(v, base)}"
                                    v < -0.5 -> "debe ${baseMoney(-v, base)}"
                                    else -> "al día"
                                },
                                color = if (v > 0.5) Matcha else if (v < -0.5) Shu else Nezu,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    val transfers = settle(net)
                    if (transfers.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        DashedDivider()
                        Spacer(Modifier.height(8.dp))
                        Text("Para saldar", style = MaterialTheme.typography.labelMedium, color = Nezu)
                        transfers.forEach {
                            Text("${nameOf(it.from)} → ${nameOf(it.to)}: ${baseMoney(it.amount, base)}")
                        }
                    }
                }
            }
        }

        if (trip.expenses.isEmpty()) {
            item { EmptyState("Todavía no registraste gastos.\nTocá ＋ para anotar el primero.") }
        }

        // Lista agrupada por fecha
        val grouped = trip.expenses.sortedByDescending { it.date }.groupBy { it.date }
        grouped.forEach { (date, list) ->
            item(key = "h$date") {
                Text(
                    if (date.isBlank()) "Sin fecha" else fmtDate(date),
                    style = MaterialTheme.typography.labelLarge,
                    color = Nezu,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            items(list, key = { it.id }) { e ->
                val c = categoryOf(e.category)
                WashiCard(onClick = { editing = e }, pad = 12.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Hanko(c.kanji, size = 36.dp, color = Ai)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(e.title.ifBlank { c.label }, fontWeight = FontWeight.Bold)
                            val who = if (trip.travelers.size > 1) {
                                "pagó ${nameOf(e.paidBy)}" +
                                    (if (e.split.size in 1 until trip.travelers.size) " · ${e.split.size} personas" else "")
                            } else c.label
                            Text(who, style = MaterialTheme.typography.bodySmall, color = Nezu)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(money(e.amount, e.cur, base), fontWeight = FontWeight.Bold)
                            if (e.cur == CUR_JPY && trip.rate > 0) {
                                Text(
                                    "≈ ${baseMoney(trip.toBase(e.amount, e.cur), base)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Nezu,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (creating) {
        ExpenseDialog(null, trip, { creating = false }, { vm.saveExpense(it); creating = false })
    }
    editing?.let { e ->
        ExpenseDialog(
            initial = e,
            trip = trip,
            onDismiss = { editing = null },
            onSave = { vm.saveExpense(it); editing = null },
            onDelete = { vm.deleteExpense(e.id); editing = null },
        )
    }
}

@Composable
private fun ExpenseDialog(
    initial: Expense?,
    trip: Trip,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    val allIds = trip.travelers.map { it.id }
    var e by remember {
        mutableStateOf(
            initial?.copy(split = initial.split.filter { it in allIds }.ifEmpty { allIds })
                ?: Expense(
                    date = LocalDate.now().toString(),
                    paidBy = allIds.firstOrNull() ?: "",
                    split = allIds,
                ),
        )
    }
    var amount by remember { mutableStateOf(numText(initial?.amount ?: 0.0)) }
    val multi = trip.travelers.size > 1

    FormDialog(
        title = if (initial == null) "Nuevo gasto" else "Editar gasto",
        onDismiss = onDismiss,
        onSave = { onSave(e.copy(amount = parseNum(amount))) },
        saveEnabled = parseNum(amount) > 0,
        onDelete = onDelete,
    ) {
        JpField("Monto", amount, { amount = it }, keyboard = KeyboardType.Decimal)
        CurrencyChips(e.cur, trip.baseCurrency) { e = e.copy(cur = it) }
        JpField("Descripción (ramen, Suica…)", e.title, { e = e.copy(title = it) })
        Text("Categoría", style = MaterialTheme.typography.labelMedium, color = Nezu)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Categories.forEach { c ->
                FilterChip(
                    selected = e.category == c.key,
                    onClick = { e = e.copy(category = c.key) },
                    label = { Text("${c.kanji} ${c.label}") },
                )
            }
        }
        DateButton("Fecha", e.date, { e = e.copy(date = it) }, Modifier.fillMaxWidth())
        if (multi) {
            Text("Pagó", style = MaterialTheme.typography.labelMedium, color = Nezu)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                trip.travelers.forEach { t ->
                    FilterChip(
                        selected = e.paidBy == t.id,
                        onClick = { e = e.copy(paidBy = t.id) },
                        label = { Text(t.name.ifBlank { "Viajero" }) },
                    )
                }
            }
            Text("Dividir entre", style = MaterialTheme.typography.labelMedium, color = Nezu)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                trip.travelers.forEach { t ->
                    FilterChip(
                        selected = t.id in e.split,
                        onClick = {
                            val now = if (t.id in e.split) e.split - t.id else e.split + t.id
                            if (now.isNotEmpty()) e = e.copy(split = now)
                        },
                        label = { Text(t.name.ifBlank { "Viajero" }) },
                    )
                }
            }
        }
        JpField("Nota", e.note, { e = e.copy(note = it) }, singleLine = false)
    }
}
