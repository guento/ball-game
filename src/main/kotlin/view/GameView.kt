package view

import BallGame
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import commons.dialog.Action
import commons.dialog.dialog
import pointFromIndex
import start
import stop
import storeHighScore

@Composable
fun BallGame.view() {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxHeight()
            .preferredWidth(pieceSize.dp * xMax + 50.dp)
            .background(androidx.compose.material.MaterialTheme.colors.surface)
            .padding(24.dp)
    ) {
        Row(modifier = androidx.compose.ui.Modifier.align(androidx.compose.ui.Alignment.End)) {
            Text("Score")
            Text("$score".padStart(6,'0'), fontSize = 50.sp)
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

        Row {
            Button(
                onClick = {
                    when (state) {
                        commons.GameState.STARTED -> stop()
                        commons.GameState.SELECTED -> stop()
                        commons.GameState.STOPPED -> start()
                    }
                },
            ) {
                when (state) {
                    commons.GameState.STARTED -> Text("Stop")
                    commons.GameState.STOPPED -> Text("Start")
                    commons.GameState.SELECTED -> Text("Stop")
                }
            }
        }
    }
}