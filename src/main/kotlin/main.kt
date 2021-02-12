import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

fun main() = Window(
    title = "GoCo"
) {
    MaterialTheme(
        colors = LightGreenColorPalette
    ) {
        goCoApp()
    }
}

@Composable
fun goCoApp() {
    Box {
        Row {
            goCoSideBar()
            goCoBodyContent()
        }
        goCoBottomBar()
    }
}

@Composable
fun goCoBottomBar() {

}

@Composable
fun goCoBodyContent() {
    val game = remember { PlayGround(10,10,4) }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(24.dp)
    ) {
        game.view()
    }
}

@Composable
fun goCoSideBar() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .preferredWidth(250.dp)
            .background(MaterialTheme.colors.surface)
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
            modifier = Modifier.fillMaxWidth(),
            onClick = { println("b1 click") },
        ) {
            Text("b1")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { println("b2 click") },
        ) {
            Text("b2")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
