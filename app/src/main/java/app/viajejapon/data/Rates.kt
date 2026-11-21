package app.viajejapon.data

import org.json.JSONObject
import java.math.BigDecimal
import java.math.MathContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cotización del yen desde ExchangeRate-API (endpoint abierto, sin clave, una actualización por día).
 * Atribución requerida: https://www.exchangerate-api.com
 * Llamar fuera del hilo principal.
 */
object Rates {

    /** Cuántas unidades de [base] vale 1 yen (ej. ARS por ¥). */
    fun yenTo(base: String): Double {
        require(base.length == 3) { "Código de moneda inválido" }
        val conn = URL("https://open.er-api.com/v6/latest/JPY").openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val o = JSONObject(body)
            require(o.optString("result") == "success") { "Respuesta inválida" }
            val v = o.getJSONObject("rates").getDouble(base)
            require(v > 0.0) { "Cotización inválida" }
            return v
        } finally {
            conn.disconnect()
        }
    }

    fun fmt(v: Double): String = BigDecimal(v).round(MathContext(5)).stripTrailingZeros().toPlainString()
}
