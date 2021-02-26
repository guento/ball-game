import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import desktop.goCoLeftSideBar
import desktop.goCoRightSideBar
import view.view

fun main() = Window(
    size = IntSize(450 + 500, 600),
    title = "BallGame"
) {
    MaterialTheme(
//        colors = LightGreenColorPalette
    ) {
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
            goCoLeftSideBar()
            game.view()
            goCoRightSideBar(game)
        }
    }
}

