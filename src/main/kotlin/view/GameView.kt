package view

import BallGame
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.dialog.Action
import commons.dialog.dialog
import pointFromIndex
import start
import stop
import storeHighScore

@Composable
fun BallGame.highScoreView() {
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
        if (highScore.size == 0) {
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("---")
            }
        } else {
            highScore.sortByDescending { it.score }
            highScore
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
fun BallGame.matchFieldView() {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxHeight()
            .preferredWidth(pieceSize.dp * xMax + 50.dp)
            .background(androidx.compose.material.MaterialTheme.colors.surface)
            .padding(24.dp)
    ) {
        Row(modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.End)) {
            Text("Score")
            Text("$score".padStart(6, '0'), fontSize = 50.sp)
        }
        Box {
            Box(modifier = androidx.compose.ui.Modifier.size(pieceSize.dp * xMax, pieceSize.dp * yMax)) {
                matrix.forEachIndexed { index, field ->
                    Piece(pointFromIndex(index), pieceSize.dp, field)
                }
            }

            if (state == commons.GameState.SELECTED)
                Text(scoreIncrementPreview.toString(),
                    modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.Center),
                    fontSize = 50.sp)

            if (state == commons.GameState.GAME_OVER) {
                Box(modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.Center)) {
                    dialog("Game Over", Action("New Game") {
                        storeHighScore(score)
                        start(0)
                    })
                }
            }

            if (state == commons.GameState.CONFIRM_CONTINUE_OR_200PTS) {
                Box(modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.Center)) {
                    dialog("Continue or 200 Points",
                        Action("Continue") { start(score) },
                        Action("200") {
                            score += 200
                            storeHighScore(score)
                            start(0)
                        }
                    )
                }
            }
        }

        Row(modifier = Modifier.padding(0.dp, 20.dp)) {
            Button(
                onClick = {
                    start()
                },
            ) {
                Text("Reset")
            }
        }
    }
}
