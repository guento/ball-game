package commons

data class Point(
    val x: Int,
    val y: Int,
)

fun Point.left(): Point = Point(x-1, y)
fun Point.right(): Point = Point(x+1, y)
fun Point.above(): Point = Point(x, y-1)
fun Point.below(): Point = Point(x, y+1)

enum class AppError(code: Int) {
    NO_PIECE_DATA_AT_LOCATION(0x00000001)
}