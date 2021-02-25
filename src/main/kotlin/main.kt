import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            goCoBodyContent(game)
            goCoRightSideBar(game)
        }
        goCoBottomBar()
    }
}

@Composable
fun goCoBottomBar() {
}

@Composable
fun goCoBodyContent(g: BallGame) {
    g.view()
}

@Composable
fun goCoLeftSideBar() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .preferredWidth(250.dp)
            .background(Color.LightGray)
            .padding(24.dp)
    ) {
        Text(
            text = "Text",
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(horizontal = 0.dp, vertical = 24.dp)),
            color = MaterialTheme.colors.primary,
        )
        Button(
            modifier = Modifier.fillMaxWidth().background(Color.LightGray),
            onClick = { println("b1 click") },
        ) {
            Text("b1")
        }
        Button(
            modifier = Modifier.fillMaxWidth().background(Color.LightGray),
            onClick = { println("b2 click") },
        ) {
            Text("b2")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun goCoRightSideBar(g: BallGame) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .preferredWidth(250.dp)
            .background(Color.LightGray)
    ) {
        Row(modifier = Modifier.padding(25.dp, 10.dp).fillMaxWidth()) {
            Text(textAlign = TextAlign.Start, fontSize = 40.sp, text = "High")
            Spacer(Modifier.weight(1f))
            Text(fontSize = 40.sp, text = "Score")
        }
        if (g.highScore.size == 0) {
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("---")
            }
        } else {
            g.highScore.forEach {
                Row(modifier = Modifier.padding(25.dp, 10.dp).fillMaxWidth()) {
                    Text(textAlign = TextAlign.Start, fontSize = 20.sp, text = it.name)
                    Spacer(Modifier.weight(1f))
                    Text(fontSize = 20.sp, text = "${it.score}".padStart(6,'0'))
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
