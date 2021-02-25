package view

import PieceData
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import commons.Point
import logger

@Composable
fun Piece(location: Point, boxSize: Dp, piece: PieceData) {
    Box(
        Modifier
            .offset(boxSize * location.x, boxSize * location.y)
            .shadow(30.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape)
    ) {
        Box(
            Modifier
                .size(boxSize, boxSize)
                .background(piece.color)
                .clickable(onClick = { piece.click(location) })
        ) {
            if (logger.isDebugEnabled) {
                Text("${location.x},${location.y}", modifier = Modifier.align(Alignment.Center))
            }
            if (piece.selected) {
                Box(
                    Modifier
                        .size(boxSize, boxSize)
                        .background(Color(0xff, 0xff, 0xff, 0x7F))
                        .clickable(onClick = { piece.click(location) })
                ) {
                }
            }
        }
    }
}