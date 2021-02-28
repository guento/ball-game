import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import commons.*
import mu.KotlinLogging
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.random.Random

val logger = KotlinLogging.logger {}

data class PieceData(val game: BallGame) {
    companion object {
        const val ACTIVE = true
        const val IN_ACTIVE = false

        val resetColor = Color.LightGray
    }

    val identity: UUID? = UUID.randomUUID()
    var selected by mutableStateOf(false)
    var active by mutableStateOf(IN_ACTIVE)
    var color by mutableStateOf(resetColor)

    fun click(p: Point) {
        logger.debug { "PieceData click: ${p.x}, ${p.y} $color ${game.indexFromPoint(p)}" }
        game.click(p)
    }

    fun reset() = setState(resetColor)

    fun randomize() = setState(getRandomColor(game.paletteSize))

    private fun setState(c: Color) {
        selected = false
        active = ACTIVE
        color = c
    }
}

/**
 * Palette used to generate random colors for the deck. The number of different colors
 * is limited by the paletteSize.
 */
val colors: Map<Int, Color> = mapOf(
    0 to Color.Red,
    1 to Color(0x00, 0xAF, 0x00),
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
    val leftToRight: Boolean,
    val xMax: Int,
    val yMax: Int,
    val pieceSize: Int,
    val paletteSize: Int,
) {
    var selection: Array<Point> = emptyArray()
    var score by mutableStateOf(0)
    var highScore = mutableStateListOf<Score>()
    var scoreIncrementPreview by mutableStateOf(0)
    var state by mutableStateOf(GameState.STOPPED)
    val matrix = mutableStateListOf<PieceData>()

    init {
        repeat(yMax * xMax) { matrix.add(PieceData(this)) }
        start()
    }
}

fun BallGame.start(s: Int = 0) {
    matrix.forEach { it.randomize() }
    score = s
    state = GameState.STARTED
}

fun BallGame.stop() {
    matrix.forEach { it.reset() }
    scoreIncrementPreview = 0
    selection = emptyArray()
    state = GameState.STOPPED
    score = 0
}

fun BallGame.click(p: Point) {
    when (state) {
        GameState.STARTED -> select(p)
        GameState.SELECTED -> {
            state = GameState.STARTED
            if (pieceFromPoint(p).selected) {
                removeSelected()
                removeEmptyColumns()

                // check game state
                checkForBonusMatrix()
                if (state != GameState.CONFIRM_CONTINUE_OR_200PTS) checkGameOver()
            } else {
                clearSelection()
            }
            scoreIncrementPreview = 0
        }
    }
}

fun BallGame.removeSelected() {
    logger.debug { "removeSelected" }

    selection
        .map { Pair(pieceFromPoint(it), it) }
        .forEach { selectedPiece ->
            matrix
                .mapIndexed { index, pieceData -> Pair(index, pieceData) }
                .find { it.second.identity == selectedPiece.first.identity }
                ?.let { matrix.removeAt(it.first) }
            matrix.add(indexFromPoint(Point(selectedPiece.second.x, 0)), PieceData(selectedPiece.first.game))
        }
    score += scoreOnSelected()
    state = GameState.STARTED
}

fun BallGame.removeEmptyColumns() {
    val result = matrix
        .mapIndexed { index, pieceData -> Pair(pointFromIndex(index), pieceData) }
        .groupBy { it.first.x }
        .mapValues { resultMapEntry -> resultMapEntry.value.all { resultMapEntryValue -> resultMapEntryValue.second.active == PieceData.IN_ACTIVE } }
        .filter { it.value }
        .mapValues {
            logger.debug { "** row ${it.key} empty = ${it.value}" }
            (0 until yMax).map { y -> pieceFromPoint(Point(it.key, y)).identity }
        }
        .map { uuidList ->
            uuidList.value.forEach { identity ->
                matrix
                    .mapIndexed { index, pieceData -> Pair(index, pieceData) }
                    .find { it.second.identity == identity }
                    ?.let { matrix.removeAt(it.first) }
            }
            if (leftToRight)
                yMax.times { matrix.add(0, PieceData(this)) }
            else
                yMax.times { matrix.add(PieceData(this)) }
        }
}

fun BallGame.checkForBonusMatrix() {
    if (matrix.all { it.active == PieceData.IN_ACTIVE }) {
        state = GameState.CONFIRM_CONTINUE_OR_200PTS
    }
}

fun BallGame.checkGameOver() {
    fun hasSameColor(p: Point, pc: Point): Boolean {
        if (pc.x !in 0 until xMax || pc.y !in 0 until yMax || !pieceFromPoint(p).active || !pieceFromPoint(pc).active) return false
        return pieceFromPoint(p).color == pieceFromPoint(pc).color
    }

    var foundSameColors = false
    for (x in (0 until xMax)) {
        for (y in (0 until yMax)) {
            val p = Point(x, y)
            val piece = pieceFromPoint(p)

            logger.trace {
                "$p $piece right ${hasSameColor(p, p.right())} left ${
                    hasSameColor(p,
                        p.left())
                } above ${hasSameColor(p, p.above())} below ${hasSameColor(p, p.below())}"
            }

            foundSameColors =
                hasSameColor(p, p.right()) || hasSameColor(p, p.left()) || hasSameColor(p, p.above()) || hasSameColor(p,
                    p.below())
            if (foundSameColors) break
        }
        if (foundSameColors) break
    }

    if (!foundSameColors) {
        state = GameState.GAME_OVER
    }
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
        if (me.x !in 0 until xMax || me.y !in 0 until yMax || !pieceFromPoint(p).active) return emptyArray()
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
    pd.selected = true

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
        scoreIncrementPreview = scoreOnSelected()
    }
}

fun BallGame.scoreOnSelected(): Int = selection.size * (selection.size - 1)

fun BallGame.pointFromIndex(i: Int) = Point(i / yMax, i % yMax)
fun BallGame.indexFromPoint(p: Point) = p.x * yMax + p.y
fun BallGame.pieceFromPoint(p: Point) = matrix[indexFromPoint(p)]

fun BallGame.storeHighScore(score: Int) {
    highScore.add(Score("got", score))
    highScore.sortByDescending { it.score }
    val saveList = highScore.take(10)

    val mapper = ObjectMapper().registerModule(KotlinModule())
    val saveListJsonString = mapper.writeValueAsString(saveList)

    logger.debug { saveListJsonString }

    val dir = File("~/.ball-game/")
    dir.mkdirs()
    val file = File(dir, "highscore.json")

    val fos = FileOutputStream(file)
    fos.write(saveListJsonString.toByteArray())
    fos.close()
}

