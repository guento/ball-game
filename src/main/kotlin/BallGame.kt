import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class BallGame(row: Int, col: Int) {
    val playingField = PlayGround(row, col, 4)
}

data class Field(
    var status: FieldStatus,
    var color: Color = Color.Red,
)

data class Palette(val paletteSize: Int) {
    val colors: Map<Int, Color> = mapOf(
        0 to Color.Red,
        1 to Color.Green,
        2 to Color.Blue,
        3 to Color.Yellow,
        4 to Color.Cyan,
        5 to Color.Magenta,
    )

    init { require(paletteSize <= colors.size) }

    fun getRandomColors(numValues: Int): List<Color> =
        List(numValues) { colors[Random.nextInt(0, paletteSize)]!! }

    fun getRandomColor(): Color = colors[Random.nextInt(0, paletteSize)]!!
}

data class PlayGround(
    val rowsCount: Int,
    val colsCount: Int,
    val paletteSize: Int,
) {
    val palette = Palette(paletteSize)
    val matrix = MutableList<Field>(rowsCount * colsCount) { Field(FieldStatus.EMPTY) }
}

fun PlayGround.reset() = matrix.replaceAll { Field(FieldStatus.FILLED, palette.getRandomColor()) }
fun PlayGround.pointFromIndex(i: Int) = Point(i / rowsCount, i % rowsCount)

@Composable
fun PlayGround.view() {
    Box {
        matrix.forEachIndexed { index, field ->
            Piece(pointFromIndex(index), field)
        }
    }
}

enum class FieldStatus {
    EMPTY,
    FILLED
}

data class Point(
    val x: Int,
    val y: Int,
)

@Composable
fun Piece(location: Point, piece: Field) {
    val boxSize = 40.dp
    Box(
        Modifier
            .offset(boxSize * location.x, boxSize * location.y)
            .shadow(30.dp)
            .clip(CircleShape)
    ) {
        Box(
            Modifier
                .size(boxSize, boxSize)
                .background(piece.color)
                .clickable(onClick = {
                    println("location = ${location.x}, ${location.y} ${piece.color.toString()}")
                    piece.status = FieldStatus.FILLED
                    piece.color = Color.Blue
                })
        )
    }
}
