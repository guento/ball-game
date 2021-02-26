import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import desktop.goCoLeftSideBar
import desktop.sideBar
import view.highScoreView
import view.matchFieldView

fun main() = Window(
    size = IntSize(450 + 250, 600),
    title = "BallGame"
) {
//    (
//        colors = LightGreenColorPalette
//    )

    MaterialTheme {
        goCoApp()
    }
}

@Composable
fun goCoApp() {
    val game = remember {
        BallGame(true, 10, 10, 40, 2)
    }
    Box {
        Row {
//            goCoLeftSideBar()
            game.matchFieldView()
            sideBar(250.dp, Color.LightGray) {
                game.highScoreView()
            }
        }
    }
}

