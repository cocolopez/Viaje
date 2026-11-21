package app.viajejapon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Trazos de tinta (sumi-e) dibujados con Canvas: sin imágenes ni assets.

/** Contorno de un trazo de pincel horizontal: grosor variable, borde irregular. */
private fun brushPath(w: Float, h: Float, seed: Int, startThick: Float, endThick: Float): Path {
    val rnd = Random(seed)
    val n = 36
    val top = ArrayList<Offset>(n + 1)
    val bottom = ArrayList<Offset>(n + 1)
    for (i in 0..n) {
        val t = i / n.toFloat()
        var th = startThick + (endThick - startThick) * t
        th *= 0.9f + 0.2f * rnd.nextFloat()
        if (t < 0.04f) th *= 0.55f + t * 10f // arranque redondeado
        val half = h * th / 2f
        val cy = h / 2f + (rnd.nextFloat() - 0.5f) * h * 0.10f
        top.add(Offset(w * t, cy - half))
        bottom.add(Offset(w * t, cy + half))
    }
    return Path().apply {
        moveTo(top[0].x, top[0].y)
        for (p in top) lineTo(p.x, p.y)
        for (p in bottom.asReversed()) lineTo(p.x, p.y)
        close()
    }
}

/** Trazo de pincel con "pincel seco" (hebras finas) al final. */
fun DrawScope.inkStroke(
    color: Color,
    width: Float,
    height: Float,
    seed: Int = 1,
    startThick: Float = 1f,
    endThick: Float = 0.35f,
    dry: Boolean = true,
    left: Float = 0f,
    top: Float = 0f,
) {
    translate(left, top) {
        drawPath(brushPath(width, height, seed, startThick, endThick), color)
        if (dry) {
            val rnd = Random(seed + 99)
            repeat(3) {
                val y = height * (0.3f + 0.4f * rnd.nextFloat())
                val x0 = width * (0.55f + 0.2f * rnd.nextFloat())
                val x1 = width * (0.95f + 0.1f * rnd.nextFloat())
                drawLine(
                    color.copy(alpha = color.alpha * 0.45f),
                    Offset(x0, y),
                    Offset(x1, y),
                    strokeWidth = (height * 0.06f).coerceAtLeast(1f),
                )
            }
        }
    }
}

@Composable
fun BrushStroke(modifier: Modifier, color: Color, seed: Int = 1) {
    Canvas(modifier) { inkStroke(color, size.width, size.height, seed) }
}

/** Ensō: círculo de pincel abierto, grueso al empezar y fino al terminar. */
fun DrawScope.ensoRing(color: Color, center: Offset, radius: Float, thickness: Float, seed: Int = 5) {
    val rnd = Random(seed)
    val n = 64
    val startDeg = -50f
    val sweep = 320f
    val outer = ArrayList<Offset>(n + 1)
    val inner = ArrayList<Offset>(n + 1)
    for (i in 0..n) {
        val t = i / n.toFloat()
        val a = ((startDeg + sweep * t) * PI / 180.0).toFloat()
        val th = thickness * (1f - 0.8f * t) * (0.9f + 0.2f * rnd.nextFloat())
        val r = radius + (rnd.nextFloat() - 0.5f) * thickness * 0.2f
        outer.add(Offset(center.x + (r + th / 2f) * cos(a), center.y + (r + th / 2f) * sin(a)))
        inner.add(Offset(center.x + (r - th / 2f) * cos(a), center.y + (r - th / 2f) * sin(a)))
    }
    val path = Path().apply {
        moveTo(outer[0].x, outer[0].y)
        for (p in outer) lineTo(p.x, p.y)
        for (p in inner.asReversed()) lineTo(p.x, p.y)
        close()
    }
    drawPath(path, color)
}

/** Paisaje de tinta diluida: montañas lejanas, el Fuji y colinas. */
@Composable
fun InkMountains(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height

        // montañas lejanas
        val far = Path().apply {
            moveTo(0f, h * 0.80f)
            lineTo(w * 0.12f, h * 0.64f)
            lineTo(w * 0.26f, h * 0.74f)
            lineTo(w * 0.40f, h * 0.58f)
            lineTo(w * 0.58f, h * 0.72f)
            lineTo(w * 0.78f, h * 0.62f)
            lineTo(w, h * 0.74f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(far, Sumi.copy(alpha = 0.07f))

        // Fuji
        val fuji = Path().apply {
            moveTo(w * 0.18f, h * 0.88f)
            cubicTo(w * 0.34f, h * 0.80f, w * 0.46f, h * 0.60f, w * 0.52f, h * 0.40f)
            lineTo(w * 0.57f, h * 0.40f)
            cubicTo(w * 0.62f, h * 0.60f, w * 0.78f, h * 0.80f, w * 0.96f, h * 0.88f)
            close()
        }
        drawPath(fuji, Sumi.copy(alpha = 0.16f))
        // nieve
        val snow = Path().apply {
            moveTo(w * 0.495f, h * 0.49f)
            lineTo(w * 0.52f, h * 0.40f)
            lineTo(w * 0.57f, h * 0.40f)
            lineTo(w * 0.595f, h * 0.49f)
            lineTo(w * 0.565f, h * 0.465f)
            lineTo(w * 0.545f, h * 0.505f)
            lineTo(w * 0.525f, h * 0.465f)
            close()
        }
        drawPath(snow, Washi.copy(alpha = 0.92f))

        // colinas en primer plano
        val hills = Path().apply {
            moveTo(0f, h * 0.90f)
            cubicTo(w * 0.20f, h * 0.82f, w * 0.35f, h * 0.95f, w * 0.55f, h * 0.88f)
            cubicTo(w * 0.75f, h * 0.81f, w * 0.88f, h * 0.92f, w, h * 0.86f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hills, Sumi.copy(alpha = 0.10f))

        // pincelada de horizonte
        inkStroke(
            Sumi.copy(alpha = 0.30f),
            width = w * 0.55f,
            height = h * 0.025f,
            seed = 11,
            startThick = 0.4f,
            endThick = 1f,
            left = w * 0.04f,
            top = h * 0.865f,
        )
    }
}
