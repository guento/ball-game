import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class PieceData(val game: BallGame) {
    companion object {
        const val FILLED = true
        const val EMPTY = false
    }

    var status by mutableStateOf(EMPTY)
    var color by mutableStateOf( Color.Red)

    fun click(location: Point) {
        // todo: change click handler
        println("location = ${location.x}, ${location.y} $color")
        status = FILLED
        color = Color.Blue
    }

    fun reset() {
        status = FILLED
        color = getRandomColor(game.paletteSize)
    }
}

/**
 * Palette used to generate random colors for the deck. The number of different colors
 * is limited by the paletteSize.
 */
val colors: Map<Int, Color> = mapOf(
    0 to Color.Red,
    1 to Color.Green,
    2 to Color.Blue,
    3 to Color.Yellow,
    4 to Color.Cyan,
    5 to Color.Magenta,
)
fun getRandomColor(paletteSize: Int): Color {
    require(paletteSize < colors.size)
    return colors[Random.nextInt(0, paletteSize)]!!
}

data class BallGame (
    val rowsCount: Int,
    val colsCount: Int,
    val pieceSize: Int,
    val paletteSize: Int,
) {
    val matrix = mutableStateListOf<PieceData>()

    init {
        repeat(rowsCount * colsCount) { matrix.add(PieceData(this)) }
    }
}

fun BallGame.reset() = matrix.forEach { it.reset() }

fun BallGame.pointFromIndex(i: Int) = Point(i / rowsCount, i % rowsCount)

@Composable
fun BallGame.view() {
    Box ( modifier =  Modifier.size(pieceSize.dp * colsCount, pieceSize.dp * rowsCount)) {
        matrix.forEachIndexed { index, field ->
            Piece(pointFromIndex(index), pieceSize.dp, field)
        }
    }
}

data class Point(
    val x: Int,
    val y: Int,
)

@Composable
fun Piece(location: Point, boxSize: Dp, piece: PieceData) {
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
                .clickable(onClick = { piece.click(location) })
        )
    }
}
