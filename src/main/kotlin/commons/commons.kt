package commons

data class Point(
    val x: Int,
    val y: Int,
)

public fun Point.left(): Point = Point(x-1, y)
public fun Point.right(): Point = Point(x+1, y)
public fun Point.above(): Point = Point(x, y-1)
public fun Point.below(): Point = Point(x, y+1)

enum class AppError(code: Int) {
    NO_PIECE_DATA_AT_LOCATION(0x00000001)
}