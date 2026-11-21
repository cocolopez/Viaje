package app.viajejapon.data

import java.util.UUID

const val CUR_BASE = "BASE"
const val CUR_JPY = "JPY"

fun newId(): String = UUID.randomUUID().toString()

data class Traveler(
    val id: String = newId(),
    val name: String = "",
)

data class Flight(
    val id: String = newId(),
    val airline: String = "",
    val number: String = "",
    val from: String = "",
    val to: String = "",
    val depDate: String = "",
    val depTime: String = "",
    val arrDate: String = "",
    val arrTime: String = "",
    val confirmation: String = "",
    val cost: Double = 0.0,
    val cur: String = CUR_BASE,
    val notes: String = "",
)

data class Stay(
    val id: String = newId(),
    val name: String = "",
    val city: String = "",
    val address: String = "",
    val checkIn: String = "",
    val checkOut: String = "",
    val confirmation: String = "",
    val cost: Double = 0.0,
    val cur: String = CUR_BASE,
    val notes: String = "",
)

data class Place(
    val id: String = newId(),
    val date: String = "",
    val time: String = "",
    val name: String = "",
    val notes: String = "",
    val estYen: Double = 0.0,
    val done: Boolean = false,
)

data class Expense(
    val id: String = newId(),
    val title: String = "",
    val category: String = "otros",
    val amount: Double = 0.0,
    val cur: String = CUR_JPY,
    val date: String = "",
    val paidBy: String = "",
    val split: List<String> = emptyList(),
    val note: String = "",
)

data class Trip(
    val name: String = "Viaje a Japón",
    val start: String = "",
    val end: String = "",
    val baseCurrency: String = "USD",
    val budget: Double = 0.0,
    /** Cuántas unidades de la moneda base vale 1 yen. */
    val rate: Double = 0.0,
    /** Fecha (ISO) en que la cotización se bajó de internet; vacío si es manual. */
    val rateDate: String = "",
    val countBookings: Boolean = true,
    val travelers: List<Traveler> = listOf(Traveler(name = "Yo")),
    val flights: List<Flight> = emptyList(),
    val stays: List<Stay> = emptyList(),
    val places: List<Place> = emptyList(),
    val expenses: List<Expense> = emptyList(),
)

fun <T> List<T>.upsert(item: T, id: (T) -> String): List<T> {
    val i = indexOfFirst { id(it) == id(item) }
    return if (i >= 0) toMutableList().also { it[i] = item } else this + item
}
