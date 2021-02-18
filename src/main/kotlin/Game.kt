import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import commons.*
import mu.KotlinLogging
import kotlin.math.pow
import kotlin.random.Random

val logger = KotlinLogging.logger {}

data class PieceData(val game: BallGame) {
    companion object {
        const val ACTIVE = true
        const val IN_ACTIVE = false

        val resetColor = Color.LightGray
    }

    var selected by mutableStateOf(false)
    var active by mutableStateOf(IN_ACTIVE)
    var color by mutableStateOf(resetColor)

    fun click(p: Point) {
        logger.debug { "PieceData click: ${p.x}, ${p.y} $color ${game.indexFromPoint(p)}" }
        game.click(p)
    }

    fun reset() = setState(false, ACTIVE, resetColor)

    fun randomize() = setState(false, ACTIVE, getRandomColor(game.paletteSize))

    private fun setState(s: Boolean, a: Boolean, c: Color) {
        selected = s
        active = a
        color = c
    }
}

/**
 * Palette used to generate random colors for the deck. The number of different colors
 * is limited by the paletteSize.
 */
val colors: Map<Int, Color> = mapOf(
    0 to Color.Red,
    1 to Color(0x00, 0xAF, 0x00, 0xff),
    2 to Color.Blue,
    3 to Color(0xFF, 0xA0, 0x00),
    4 to Color(0x00, 0xAA, 0xFF),
    5 to Color.Magenta,
)

fun getRandomColor(paletteSize: Int): Color {
    require(paletteSize <= colors.size) {
        "Requested number of colors[$paletteSize] exceeds maximumColors[${colors.size}]"
    }
    return colors[Random.nextInt(0, paletteSize)]!!
}

data class BallGame(
    val rowsCount: Int,
    val colsCount: Int,
    val pieceSize: Int,
    val paletteSize: Int,
) {
    var selection: Array<Point> = emptyArray()
    var score by mutableStateOf(0)
    var scoreIncrementPreview by mutableStateOf(0)
    var state by mutableStateOf(GameState.STOPPED)
    val matrix = mutableStateListOf<PieceData>()

    init {
        repeat(rowsCount * colsCount) { matrix.add(PieceData(this)) }
    }
}

fun BallGame.start() {
    matrix.forEach { it.randomize() }
    state = GameState.STARTED
}

fun BallGame.stop() {
    matrix.forEach { it.reset() }
    scoreIncrementPreview = 0
    selection = emptyArray()
    state = GameState.STOPPED
}

fun BallGame.click(p: Point) {
    when (state) {
        GameState.STARTED -> select(p)
        GameState.SELECTED -> {
            state = GameState.STARTED
            if (pieceFromPoint(p).selected) {
                score += scoreIncrementPreview
                removeSelected()
            } else {
                clearSelection()
            }
            scoreIncrementPreview = 0
        }
    }
}

fun BallGame.removeSelected() {
    logger.debug { "removeSelected" }

    selection.forEach {
        val index = indexFromPoint(Point(it.x,0))
        val piece = pieceFromPoint(it)

        logger.debug { "remove $piece"}
        matrix.remove(piece)

        logger.debug { "add empty pieces at $index" }
        matrix.add(index, PieceData(this))
    }

    state = GameState.STARTED

//    (0 until colsCount).forEach { x ->
//        logger.debug { "x = $x" }
//        ((rowsCount - 1) downTo 0).forEach { y ->
//            val point = Point(x,y)
//            logger.debug { "check $point" }
//            val p = pieceFromPoint(point)
//            if (p.selected) {
//                matrix.add(indexFromPoint(Point(x,0)), PieceData(this))
//                matrix.remove(p)
//            }
//        }
//    }
}

fun BallGame.removePoint(p: Point) {
    matrix.add(1, pieceFromPoint(p))
}

fun BallGame.clearSelection() {
    logger.debug { "clearSelection" }
    selection.forEach {
        pieceFromPoint(it).selected = false
    }
    selection = emptyArray()
}

fun BallGame.select(p: Point) {
    fun findNeighbour(parent: Point, me: Point): Array<out Point> {
        // drop processing if outside x or y range
        if (me.x !in 0 until rowsCount || me.y !in 0 until colsCount || !pieceFromPoint(p).active) return emptyArray()
        if (!pieceFromPoint(p).active) return emptyArray()

        val cdParent = pieceFromPoint(parent)
        val cdMe = pieceFromPoint(me)

        if (cdMe.selected) return emptyArray()

        if (cdMe.color != cdParent.color) return emptyArray()

        cdMe.selected = true

        return arrayOf(
            me,
            *findNeighbour(me, me.above()),
            *findNeighbour(me, me.right()),
            *findNeighbour(me, me.below()),
            *findNeighbour(me, me.left()),
        )
    }

    // get location piece data
    val pd = pieceFromPoint(p)

    // set selected
    pd.selected = true;

    selection = arrayOf(
        p,
        *findNeighbour(p, p.above()),
        *findNeighbour(p, p.right()),
        *findNeighbour(p, p.below()),
        *findNeighbour(p, p.left()),
    )
    logger.debug { "selection size = ${selection.size}" }

    if (selection.size < 2)
        pd.selected = false
    else {
        state = GameState.SELECTED
        scoreIncrementPreview = 2.0.pow(selection.size - 1).toInt()
    }
}

fun BallGame.pointFromIndex(i: Int) = Point(i / rowsCount, i % rowsCount)
fun BallGame.indexFromPoint(p: Point) = p.x * rowsCount + p.y
fun BallGame.pieceFromPoint(p: Point) = matrix[indexFromPoint(p)]

@Composable
fun BallGame.view() {
    Box(modifier = Modifier.size(pieceSize.dp * colsCount, pieceSize.dp * rowsCount)) {
        matrix.forEachIndexed { index, field ->
            Piece(pointFromIndex(index), pieceSize.dp, field)
        }
    }
}

@Composable
fun Piece(location: Point, boxSize: Dp, piece: PieceData) {
    Box(
        Modifier
            .offset(boxSize * location.x, boxSize * location.y)
            .shadow(30.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
    ) {
        Box(
            Modifier
                .size(boxSize, boxSize)
                .background(piece.color)
                .clickable(onClick = { piece.click(location) })
        ) {
            if (logger.isDebugEnabled) {
                Text("${location.x},${location.y}", modifier = Modifier.align(Center))
            }
            if (piece.selected) Text("O", modifier = Modifier.align(Center))
        }
    }
}
