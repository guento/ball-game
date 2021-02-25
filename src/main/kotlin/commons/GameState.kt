package commons

enum class GameState {
    STARTED, STOPPED, SELECTED, GAME_OVER, CONFIRM_CONTINUE_OR_200PTS
}

data class Score(val name: String, val score: Int)
