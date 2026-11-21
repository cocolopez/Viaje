package app.viajejapon.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// ---------- Dinero ----------

fun Trip.toBase(amount: Double, cur: String): Double =
    if (cur == CUR_JPY) amount * rate else amount

fun Trip.expensesBase(): Double = expenses.sumOf { toBase(it.amount, it.cur) }

fun Trip.bookingsBase(): Double =
    flights.sumOf { toBase(it.cost, it.cur) } + stays.sumOf { toBase(it.cost, it.cur) }

fun Trip.spentBase(): Double = expensesBase() + if (countBookings) bookingsBase() else 0.0

/** true si hay montos en yenes pero todavía no se cargó la cotización. */
fun Trip.yenWithoutRate(): Boolean =
    rate <= 0.0 && (
        expenses.any { it.cur == CUR_JPY && it.amount > 0 } ||
            flights.any { it.cur == CUR_JPY && it.cost > 0 } ||
            stays.any { it.cur == CUR_JPY && it.cost > 0 }
        )

// ---------- División de gastos ----------

data class Transfer(val from: String, val to: String, val amount: Double)

/** Saldo por viajero en moneda base: positivo = le deben, negativo = debe. */
fun Trip.balances(): Map<String, Double> {
    val ids = travelers.map { it.id }
    val net = ids.associateWith { 0.0 }.toMutableMap()
    expenses.forEach { e ->
        val amt = toBase(e.amount, e.cur)
        val part = e.split.filter { it in net }.ifEmpty { ids }
        if (e.paidBy in net) net[e.paidBy] = net.getValue(e.paidBy) + amt
        val share = amt / part.size
        part.forEach { net[it] = net.getValue(it) - share }
    }
    return net
}

fun settle(net: Map<String, Double>): List<Transfer> {
    val debtors = net.filter { it.value < -0.5 }.map { it.key to -it.value }
        .sortedByDescending { it.second }.toMutableList()
    val creditors = net.filter { it.value > 0.5 }.map { it.key to it.value }
        .sortedByDescending { it.second }.toMutableList()
    val out = mutableListOf<Transfer>()
    var i = 0
    var j = 0
    while (i < debtors.size && j < creditors.size) {
        val pay = minOf(debtors[i].second, creditors[j].second)
        out += Transfer(debtors[i].first, creditors[j].first, pay)
        debtors[i] = debtors[i].first to debtors[i].second - pay
        creditors[j] = creditors[j].first to creditors[j].second - pay
        if (debtors[i].second < 0.5) i++
        if (creditors[j].second < 0.5) j++
    }
    return out
}

// ---------- Formato ----------

val esAR: Locale = Locale.forLanguageTag("es-AR")

fun num(v: Double): String = String.format(esAR, "%,.0f", v)

fun baseMoney(v: Double, base: String): String = "$base ${num(v)}"

fun money(v: Double, cur: String, base: String): String =
    if (cur == CUR_JPY) "¥" + num(v) else baseMoney(v, base)

fun parseNum(s: String): Double = s.trim().replace(',', '.').toDoubleOrNull() ?: 0.0

fun numText(v: Double): String = when {
    v == 0.0 -> ""
    v % 1.0 == 0.0 -> v.toLong().toString()
    else -> v.toString()
}

fun parseDate(s: String): LocalDate? = runCatching { LocalDate.parse(s) }.getOrNull()

fun fmtDate(s: String): String =
    parseDate(s)?.format(DateTimeFormatter.ofPattern("EEE d MMM yyyy", esAR)) ?: "—"

fun fmtShort(d: LocalDate): String = d.format(DateTimeFormatter.ofPattern("d MMM", esAR))

fun fmtWeekday(d: LocalDate): String = d.format(DateTimeFormatter.ofPattern("EEE", esAR))
