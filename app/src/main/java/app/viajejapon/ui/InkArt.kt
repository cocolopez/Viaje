package app.viajejapon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

/** Ilustración de tinta que acompaña a cada pestaña. */
enum class ArtKind { Pagoda, Birds, House, Torii, Waves }

@Composable
fun InkArt(kind: ArtKind, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        when (kind) {
            ArtKind.Pagoda -> drawPagoda()
            ArtKind.Birds -> drawBirds()
            ArtKind.House -> drawHouse()
            ArtKind.Torii -> drawTorii()
            ArtKind.Waves -> drawWaves()
        }
    }
}

// ---------- Inicio: pagoda de cinco pisos (gojūnotō) con pétalos de sakura ----------

private fun DrawScope.drawPagoda() {
    val w = size.width
    val h = size.height
    val d = 1.dp.toPx()
    val s = h * 0.80f / 2.85f // alto total ≈ 2,8 unidades (ancho del primer techo = 1)
    val cx = w * 0.74f
    val base = h * 0.94f
    fun X(v: Float) = cx + v * s
    fun Y(v: Float) = base - v * s
    fun box(x: Float, yb: Float, bw: Float, bh: Float, c: Color) =
        drawRect(c, Offset(X(x), Y(yb + bh)), Size(bw * s, bh * s))
    val wall = Sumi.copy(alpha = 0.16f)
    val roofC = Sumi.copy(alpha = 0.30f)
    val inkC = Sumi.copy(alpha = 0.35f)

    // sol pálido detrás
    drawCircle(Shu.copy(alpha = 0.13f), h * 0.17f, Offset(w * 0.30f, h * 0.38f))

    box(-0.5600f, 0.0000f, 1.1200f, 0.0500f, Sumi.copy(alpha = 0.22f))
    box(-0.2800f, 0.0500f, 0.5600f, 0.4000f, wall)
    box(-0.2352f, 0.6400f, 0.4704f, 0.2500f, wall)
    box(-0.1960f, 1.0700f, 0.3920f, 0.2200f, wall)
    box(-0.1624f, 1.4600f, 0.3248f, 0.1900f, wall)
    box(-0.1344f, 1.8050f, 0.2688f, 0.1700f, wall)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.2800f), Y(0.0500f)), Offset(X(-0.2800f), Y(0.4500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.0933f), Y(0.0500f)), Offset(X(-0.0933f), Y(0.4500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.0933f), Y(0.0500f)), Offset(X(0.0933f), Y(0.4500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.2800f), Y(0.0500f)), Offset(X(0.2800f), Y(0.4500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.2352f), Y(0.6400f)), Offset(X(-0.2352f), Y(0.8900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.0784f), Y(0.6400f)), Offset(X(-0.0784f), Y(0.8900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.0784f), Y(0.6400f)), Offset(X(0.0784f), Y(0.8900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.2352f), Y(0.6400f)), Offset(X(0.2352f), Y(0.8900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.1960f), Y(1.0700f)), Offset(X(-0.1960f), Y(1.2900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.0653f), Y(1.0700f)), Offset(X(-0.0653f), Y(1.2900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.0653f), Y(1.0700f)), Offset(X(0.0653f), Y(1.2900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.1960f), Y(1.0700f)), Offset(X(0.1960f), Y(1.2900f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.1624f), Y(1.4600f)), Offset(X(-0.1624f), Y(1.6500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.0541f), Y(1.4600f)), Offset(X(-0.0541f), Y(1.6500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.0541f), Y(1.4600f)), Offset(X(0.0541f), Y(1.6500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.1624f), Y(1.4600f)), Offset(X(0.1624f), Y(1.6500f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.1344f), Y(1.8050f)), Offset(X(-0.1344f), Y(1.9750f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(-0.0448f), Y(1.8050f)), Offset(X(-0.0448f), Y(1.9750f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.0448f), Y(1.8050f)), Offset(X(0.0448f), Y(1.9750f)), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.25f), Offset(X(0.1344f), Y(1.8050f)), Offset(X(0.1344f), Y(1.9750f)), strokeWidth = 1.5f * d)
    box(-0.2900f, 0.4150f, 0.5800f, 0.0300f, Shu.copy(alpha = 0.45f))
    box(-0.2452f, 0.8550f, 0.4904f, 0.0300f, Shu.copy(alpha = 0.45f))
    box(-0.2060f, 1.2550f, 0.4120f, 0.0300f, Shu.copy(alpha = 0.45f))
    box(-0.1724f, 1.6150f, 0.3448f, 0.0300f, Shu.copy(alpha = 0.45f))
    box(-0.1444f, 1.9400f, 0.2888f, 0.0300f, Shu.copy(alpha = 0.45f))
    val moko = Path().apply {
        moveTo(X(-0.5700f), Y(0.2540f))
        cubicTo(X(-0.4560f), Y(0.1664f), X(-0.4332f), Y(0.1700f), X(-0.2964f), Y(0.1700f))
        lineTo(X(0.2964f), Y(0.1700f))
        cubicTo(X(0.4332f), Y(0.1700f), X(0.4560f), Y(0.1664f), X(0.5700f), Y(0.2540f))
        cubicTo(X(0.4332f), Y(0.2660f), X(0.5124f), Y(0.2636f), X(0.3300f), Y(0.2900f))
        lineTo(X(-0.3300f), Y(0.2900f))
        cubicTo(X(-0.5124f), Y(0.2636f), X(-0.4332f), Y(0.2660f), X(-0.5700f), Y(0.2540f))
        close()
    }
    drawPath(moko, roofC)
    val roof0 = Path().apply {
        moveTo(X(-0.5000f), Y(0.5925f))
        cubicTo(X(-0.4000f), Y(0.4443f), X(-0.3800f), Y(0.4500f), X(-0.2600f), Y(0.4500f))
        lineTo(X(0.2600f), Y(0.4500f))
        cubicTo(X(0.3800f), Y(0.4500f), X(0.4000f), Y(0.4443f), X(0.5000f), Y(0.5925f))
        cubicTo(X(0.3800f), Y(0.6115f), X(0.4070f), Y(0.5982f), X(0.2470f), Y(0.6400f))
        lineTo(X(-0.2470f), Y(0.6400f))
        cubicTo(X(-0.4070f), Y(0.5982f), X(-0.3800f), Y(0.6115f), X(-0.5000f), Y(0.5925f))
        close()
    }
    drawPath(roof0, roofC)
    val roof1 = Path().apply {
        moveTo(X(-0.4200f), Y(1.0250f))
        cubicTo(X(-0.3360f), Y(0.8846f), X(-0.3192f), Y(0.8900f), X(-0.2184f), Y(0.8900f))
        lineTo(X(0.2184f), Y(0.8900f))
        cubicTo(X(0.3192f), Y(0.8900f), X(0.3360f), Y(0.8846f), X(0.4200f), Y(1.0250f))
        cubicTo(X(0.3192f), Y(1.0430f), X(0.3402f), Y(1.0304f), X(0.2058f), Y(1.0700f))
        lineTo(X(-0.2058f), Y(1.0700f))
        cubicTo(X(-0.3402f), Y(1.0304f), X(-0.3192f), Y(1.0430f), X(-0.4200f), Y(1.0250f))
        close()
    }
    drawPath(roof1, roofC)
    val roof2 = Path().apply {
        moveTo(X(-0.3500f), Y(1.4175f))
        cubicTo(X(-0.2800f), Y(1.2849f), X(-0.2660f), Y(1.2900f), X(-0.1820f), Y(1.2900f))
        lineTo(X(0.1820f), Y(1.2900f))
        cubicTo(X(0.2660f), Y(1.2900f), X(0.2800f), Y(1.2849f), X(0.3500f), Y(1.4175f))
        cubicTo(X(0.2660f), Y(1.4345f), X(0.2825f), Y(1.4226f), X(0.1705f), Y(1.4600f))
        lineTo(X(-0.1705f), Y(1.4600f))
        cubicTo(X(-0.2825f), Y(1.4226f), X(-0.2660f), Y(1.4345f), X(-0.3500f), Y(1.4175f))
        close()
    }
    drawPath(roof2, roofC)
    val roof3 = Path().apply {
        moveTo(X(-0.2900f), Y(1.7662f))
        cubicTo(X(-0.2320f), Y(1.6453f), X(-0.2204f), Y(1.6500f), X(-0.1508f), Y(1.6500f))
        lineTo(X(0.1508f), Y(1.6500f))
        cubicTo(X(0.2204f), Y(1.6500f), X(0.2320f), Y(1.6453f), X(0.2900f), Y(1.7662f))
        cubicTo(X(0.2204f), Y(1.7817f), X(0.2339f), Y(1.7709f), X(0.1411f), Y(1.8050f))
        lineTo(X(-0.1411f), Y(1.8050f))
        cubicTo(X(-0.2339f), Y(1.7709f), X(-0.2204f), Y(1.7817f), X(-0.2900f), Y(1.7662f))
        close()
    }
    drawPath(roof3, roofC)
    val roof4 = Path().apply {
        moveTo(X(-0.2400f), Y(2.0837f))
        cubicTo(X(-0.1920f), Y(1.9706f), X(-0.1824f), Y(1.9750f), X(-0.1248f), Y(1.9750f))
        lineTo(X(0.1248f), Y(1.9750f))
        cubicTo(X(0.1824f), Y(1.9750f), X(0.1920f), Y(1.9706f), X(0.2400f), Y(2.0837f))
        cubicTo(X(0.1824f), Y(2.0982f), X(0.1268f), Y(2.0881f), X(0.0500f), Y(2.1200f))
        lineTo(X(-0.0500f), Y(2.1200f))
        cubicTo(X(-0.1268f), Y(2.0881f), X(-0.1824f), Y(2.0982f), X(-0.2400f), Y(2.0837f))
        close()
    }
    drawPath(roof4, roofC)
    box(-0.0500f, 2.1200f, 0.1000f, 0.0500f, inkC)
    box(-0.0120f, 2.1700f, 0.0240f, 0.6200f, inkC)
    box(-0.0425f, 2.2700f, 0.0850f, 0.0140f, inkC)
    box(-0.0405f, 2.3150f, 0.0810f, 0.0140f, inkC)
    box(-0.0385f, 2.3600f, 0.0770f, 0.0140f, inkC)
    box(-0.0365f, 2.4050f, 0.0730f, 0.0140f, inkC)
    box(-0.0345f, 2.4500f, 0.0690f, 0.0140f, inkC)
    box(-0.0325f, 2.4950f, 0.0650f, 0.0140f, inkC)
    box(-0.0305f, 2.5400f, 0.0610f, 0.0140f, inkC)
    box(-0.0285f, 2.5850f, 0.0570f, 0.0140f, inkC)
    box(-0.0265f, 2.6300f, 0.0530f, 0.0140f, inkC)
    drawCircle(inkC, 0.0350f * s, Offset(X(0f), Y(2.9000f)))

    // neblina sobre el primer piso y suelo de tinta
    inkStroke(Washi.copy(alpha = 0.85f), w * 0.8f, 16 * d, seed = 41, startThick = 0.6f, endThick = 1f, dry = false, left = w * 0.1f, top = Y(0.32f))
    inkStroke(Sumi.copy(alpha = 0.30f), w * 0.92f, 8 * d, seed = 21, startThick = 0.4f, endThick = 1f, left = w * 0.04f, top = base - 3 * d)

    // pétalos de sakura
    val petal = Color(0xFFE59A9A)
    val petals = listOf(
        0.10f to 0.30f, 0.22f to 0.55f, 0.35f to 0.20f, 0.46f to 0.62f,
        0.55f to 0.12f, 0.15f to 0.78f, 0.40f to 0.84f, 0.88f to 0.22f,
    )
    petals.forEachIndexed { i, (px, py) ->
        drawCircle(petal.copy(alpha = 0.55f), (3 + i % 3) * d, Offset(w * px, h * py))
    }
}

// ---------- Vuelos: ruta punteada, avioncito de papel y pájaros ----------

private fun DrawScope.bird(x: Float, y: Float, s: Float) {
    val d = 1.dp.toPx()
    val p = Path().apply {
        moveTo(x - s, y)
        cubicTo(x - s * 0.5f, y - s * 0.6f, x - s * 0.1f, y - s * 0.5f, x, y)
        cubicTo(x + s * 0.1f, y - s * 0.5f, x + s * 0.5f, y - s * 0.6f, x + s, y)
    }
    drawPath(p, Sumi.copy(alpha = 0.45f), style = Stroke(width = 2.5f * d, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawBirds() {
    val w = size.width
    val h = size.height
    val d = 1.dp.toPx()

    drawCircle(Shu.copy(alpha = 0.14f), h * 0.20f, Offset(w * 0.72f, h * 0.42f))

    inkStroke(Sumi.copy(alpha = 0.09f), w * 0.55f, 26 * d, seed = 31, startThick = 0.6f, endThick = 1f, dry = false, left = w * 0.40f, top = h * 0.55f)
    inkStroke(Sumi.copy(alpha = 0.07f), w * 0.50f, 22 * d, seed = 32, startThick = 0.6f, endThick = 1f, dry = false, left = w * 0.05f, top = h * 0.70f)
    inkStroke(Sumi.copy(alpha = 0.08f), w * 0.60f, 20 * d, seed = 33, startThick = 0.6f, endThick = 1f, dry = false, left = w * 0.30f, top = h * 0.84f)

    // ruta de vuelo
    val route = Path().apply {
        moveTo(w * 0.08f, h * 0.88f)
        cubicTo(w * 0.30f, h * 0.55f, w * 0.55f, h * 0.82f, w * 0.88f, h * 0.30f)
    }
    drawPath(
        route,
        Shu.copy(alpha = 0.5f),
        style = Stroke(
            width = 2.5f * d,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14 * d, 12 * d)),
        ),
    )
    // avioncito de papel (origami)
    val px = w * 0.88f
    val py = h * 0.30f
    rotate(-48f, Offset(px, py)) {
        val plane = Path().apply {
            moveTo(px + 20 * d, py)
            lineTo(px - 14 * d, py - 11 * d)
            lineTo(px - 6 * d, py)
            lineTo(px - 14 * d, py + 11 * d)
            close()
        }
        drawPath(plane, Sumi.copy(alpha = 0.60f))
    }

    bird(w * 0.22f, h * 0.30f, 16 * d)
    bird(w * 0.34f, h * 0.20f, 11 * d)
    bird(w * 0.12f, h * 0.16f, 9 * d)
}

// ---------- Estadía: casa tradicional con farol y bambú ----------

private fun DrawScope.drawHouse() {
    val w = size.width
    val h = size.height
    val d = 1.dp.toPx()
    val cx = w * 0.46f
    val base = h * 0.93f
    val hw = w * 0.62f

    drawCircle(Kin.copy(alpha = 0.20f), h * 0.15f, Offset(w * 0.82f, h * 0.25f))

    val bodyH = h * 0.20f
    val bodyTop = base - bodyH
    val left = cx - hw * 0.40f
    val bodyW = hw * 0.80f
    drawRect(Sumi.copy(alpha = 0.12f), Offset(left, bodyTop), Size(bodyW, bodyH))
    // shōji
    for (i in 0..5) {
        val x = left + bodyW * i / 5f
        drawLine(Sumi.copy(alpha = 0.22f), Offset(x, bodyTop), Offset(x, base), strokeWidth = 1.5f * d)
    }
    drawLine(Sumi.copy(alpha = 0.22f), Offset(left, bodyTop + bodyH * 0.5f), Offset(left + bodyW, bodyTop + bodyH * 0.5f), strokeWidth = 1.5f * d)
    drawLine(Sumi.copy(alpha = 0.22f), Offset(left, bodyTop + bodyH * 0.2f), Offset(left + bodyW, bodyTop + bodyH * 0.2f), strokeWidth = 1.5f * d)

    // techo
    val rh = h * 0.26f
    val yEave = bodyTop + 4 * d
    val yRidge = yEave - rh
    val roof = Path().apply {
        moveTo(cx - hw * 0.60f, yEave - 6 * d)
        cubicTo(cx - hw * 0.45f, yEave + 5 * d, cx - hw * 0.35f, yEave + 3 * d, cx - hw * 0.30f, yEave)
        lineTo(cx + hw * 0.30f, yEave)
        cubicTo(cx + hw * 0.35f, yEave + 3 * d, cx + hw * 0.45f, yEave + 5 * d, cx + hw * 0.60f, yEave - 6 * d)
        cubicTo(cx + hw * 0.40f, yEave - rh * 0.25f, cx + hw * 0.28f, yRidge + rh * 0.1f, cx + hw * 0.20f, yRidge)
        lineTo(cx - hw * 0.20f, yRidge)
        cubicTo(cx - hw * 0.28f, yRidge + rh * 0.1f, cx - hw * 0.40f, yEave - rh * 0.25f, cx - hw * 0.60f, yEave - 6 * d)
        close()
    }
    drawPath(roof, Sumi.copy(alpha = 0.30f))

    // farol rojo (chōchin)
    val lx = cx - hw * 0.34f
    drawLine(Sumi.copy(alpha = 0.4f), Offset(lx, yEave), Offset(lx, yEave + 22 * d), strokeWidth = 1.5f * d)
    drawOval(Shu.copy(alpha = 0.75f), Offset(lx - 9 * d, yEave + 22 * d), Size(18 * d, 26 * d))
    drawRect(Sumi.copy(alpha = 0.45f), Offset(lx - 5 * d, yEave + 20 * d), Size(10 * d, 3 * d))
    drawRect(Sumi.copy(alpha = 0.45f), Offset(lx - 5 * d, yEave + 46 * d), Size(10 * d, 3 * d))

    // bambú
    for ((bx, bh) in listOf(w * 0.88f to 0.62f, w * 0.94f to 0.50f)) {
        drawLine(Matcha.copy(alpha = 0.45f), Offset(bx, base), Offset(bx, base - h * bh), strokeWidth = 5 * d)
        var yy = base - h * 0.10f
        while (yy > base - h * bh) {
            drawLine(Sumi.copy(alpha = 0.30f), Offset(bx - 4 * d, yy), Offset(bx + 4 * d, yy), strokeWidth = 1.5f * d)
            yy -= h * 0.10f
        }
        val ty = base - h * bh + 30 * d
        rotate(-28f, Offset(bx, ty)) {
            inkStroke(Matcha.copy(alpha = 0.45f), 44 * d, 7 * d, seed = 51, startThick = 1f, endThick = 0.2f, left = bx, top = ty)
        }
        rotate(200f, Offset(bx, ty + 16 * d)) {
            inkStroke(Matcha.copy(alpha = 0.40f), 40 * d, 6 * d, seed = 52, startThick = 1f, endThick = 0.2f, left = bx, top = ty + 16 * d)
        }
    }

    inkStroke(Sumi.copy(alpha = 0.30f), w * 0.92f, 8 * d, seed = 23, startThick = 0.4f, endThick = 1f, left = w * 0.04f, top = base - 3 * d)
}

// ---------- Itinerario: torii con luna ----------

private fun DrawScope.drawTorii() {
    val w = size.width
    val h = size.height
    val d = 1.dp.toPx()
    val cx = w * 0.5f
    val base = h * 0.93f
    val gw = w * 0.50f
    val gh = h * 0.62f
    val yTop = base - gh

    drawCircle(Kin.copy(alpha = 0.22f), h * 0.16f, Offset(cx + gw * 0.55f, yTop - h * 0.02f))

    val red = Shu.copy(alpha = 0.60f)
    val pw = gw * 0.075f
    for (s in listOf(-1f, 1f)) {
        val px = cx + s * gw * 0.36f
        drawRect(red, Offset(px - pw / 2f, yTop + gh * 0.05f), Size(pw, gh * 0.95f))
        drawRect(Sumi.copy(alpha = 0.35f), Offset(px - pw * 0.6f, base - gh * 0.05f), Size(pw * 1.2f, gh * 0.05f))
    }
    // nuki (travesaño) y tablilla central
    drawRect(red, Offset(cx - gw * 0.42f, yTop + gh * 0.22f), Size(gw * 0.84f, gh * 0.045f))
    drawRect(Shu.copy(alpha = 0.5f), Offset(cx - pw * 0.4f, yTop + gh * 0.075f), Size(pw * 0.8f, gh * 0.145f))
    // shimaki + kasagi
    drawRect(Sumi.copy(alpha = 0.45f), Offset(cx - gw * 0.5f, yTop + gh * 0.075f), Size(gw, gh * 0.03f))
    val kasagi = Path().apply {
        moveTo(cx - gw * 0.58f, yTop - gh * 0.06f)
        cubicTo(cx - gw * 0.30f, yTop + gh * 0.035f, cx + gw * 0.30f, yTop + gh * 0.035f, cx + gw * 0.58f, yTop - gh * 0.06f)
        lineTo(cx + gw * 0.53f, yTop + gh * 0.045f)
        cubicTo(cx + gw * 0.30f, yTop + gh * 0.095f, cx - gw * 0.30f, yTop + gh * 0.095f, cx - gw * 0.53f, yTop + gh * 0.045f)
        close()
    }
    drawPath(kasagi, Shu.copy(alpha = 0.70f))

    // escalones de piedra
    for (i in 0..4) {
        val y = base - i * gh * 0.035f
        val half = gw * (0.34f - i * 0.025f)
        drawLine(Sumi.copy(alpha = 0.20f), Offset(cx - half, y), Offset(cx + half, y), strokeWidth = 2f * d)
    }
    inkStroke(Washi.copy(alpha = 0.85f), w * 0.8f, 16 * d, seed = 42, startThick = 0.6f, endThick = 1f, dry = false, left = w * 0.1f, top = base - gh * 0.12f)
    inkStroke(Sumi.copy(alpha = 0.30f), w * 0.92f, 8 * d, seed = 24, startThick = 0.4f, endThick = 1f, left = w * 0.04f, top = base - 3 * d)
}

// ---------- Gastos: sol sobre el mar de olas, con monedas ----------

private fun DrawScope.coin(c: Offset, r: Float) {
    val d = 1.dp.toPx()
    drawCircle(Kin.copy(alpha = 0.50f), r, c)
    drawCircle(Sumi.copy(alpha = 0.35f), r, c, style = Stroke(1.5f * d))
    val s = r * 0.56f
    drawRect(Washi, Offset(c.x - s / 2f, c.y - s / 2f), Size(s, s))
    drawRect(Sumi.copy(alpha = 0.35f), Offset(c.x - s / 2f, c.y - s / 2f), Size(s, s), style = Stroke(1.5f * d))
}

private fun DrawScope.drawWaves() {
    val w = size.width
    val h = size.height
    val d = 1.dp.toPx()

    // sol poniéndose en el mar
    drawCircle(Shu.copy(alpha = 0.13f), h * 0.20f, Offset(w * 0.5f, h * 0.62f))
    coin(Offset(w * 0.26f, h * 0.36f), 30 * d)
    coin(Offset(w * 0.50f, h * 0.22f), 38 * d)
    coin(Offset(w * 0.74f, h * 0.38f), 28 * d)

    // olas (seigaiha)
    val bandTop = h * 0.60f
    val ww = 40.dp.toPx()
    val r = ww / 2f
    val ring = Ai.copy(alpha = 0.28f)
    clipRect(top = bandTop) {
        val rows = ((h - bandTop) / (r / 2f)).toInt() + 2
        val cols = (w / ww).toInt() + 2
        for (row in 0..rows) {
            val cy = bandTop + row * r / 2f
            val off = if (row % 2 == 0) 0f else r
            for (col in -1..cols) {
                val c = Offset(col * ww + off, cy)
                drawCircle(Washi, r, c)
                for (k in listOf(1f, 0.72f, 0.44f, 0.16f)) {
                    drawCircle(ring, r * k, c, style = Stroke(1.2f * d))
                }
            }
        }
    }
    val fadeH = (h - bandTop) * 0.6f
    drawRect(
        Brush.verticalGradient(listOf(Washi, Color.Transparent), startY = bandTop, endY = bandTop + fadeH),
        topLeft = Offset(0f, bandTop),
        size = Size(w, fadeH),
    )
}
