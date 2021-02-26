package desktop

import BallGame
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            g.highScore.sortByDescending { it.score }
            g.highScore
                .take(10)
                .forEach {
                    Row(modifier = Modifier.padding(25.dp, 10.dp).fillMaxWidth()) {
                        Text(textAlign = TextAlign.Start, fontSize = 20.sp, text = it.name)
                        Spacer(Modifier.weight(1f))
                        Text(fontSize = 20.sp, text = "${it.score}".padStart(6, '0'))
                    }
                }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun sideBar(width: Dp, background: Color, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .preferredWidth(width)
            .background(background)
    ) {
        content()
    }
}