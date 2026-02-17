package pl.slaszu.workbreak.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import pl.slaszu.workbreak.domain.model.Setting
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme

@Composable
fun SettingScreen(
    setting: Setting
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(setting.toString())

        ParamInfo(
            header = "Work start reminder",
            desc = "Show notification when you start work"
        ) {
            Switch(
                checked = false,
                onCheckedChange = { /*TODO*/ },
                modifier = Modifier
            )
        }

        ParamInfo(
            header = "Work end reminder",
            desc = "Show notification when you end work"
        ) {
            Switch(
                checked = false,
                onCheckedChange = { /*TODO*/ },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun ParamInfo(
    header: String,
    desc: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(0.7f)
        ) {

            Text(
                text = header,
                fontSize = TextUnit(4f, TextUnitType.Em),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = desc,
                fontSize = TextUnit(3f, TextUnitType.Em),
            )
        }
        content()
    }
}

@Preview
@Composable
fun SettingScreenPreview() {
    WorkBreakTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                SettingScreen(
                    setting = Setting()
                )
            }
        }
    }
}