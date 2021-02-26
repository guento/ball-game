package commons.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Action(val name: String, val f: ()->Unit)

@Composable
fun dialog(title: String, vararg actions: Action)
{
    Box(modifier = Modifier.size(300.dp, 150.dp).background(Color(0xFF, 0xA0, 0x00))) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(title, fontSize = 20.sp)
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                actions.map {
                    Button(
                        modifier = Modifier.padding(5.dp, 10.dp),
                        onClick = {
                            it.f()
                        },
                    ) {
                        Text(it.name)
                    }
                }
            }
        }
    }
}