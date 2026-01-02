package pl.slaszu.workbreak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import pl.slaszu.workbreak.domain.NotificationService
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme
import javax.inject.Inject

@AndroidEntryPoint
class NotificationRequestActivity : ComponentActivity() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationService.prepareRequest(this) {
            this.startActivity(
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }

        setContent {
            WorkBreakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotificationRequest(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        notificationService.launchRequest()
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRequest(modifier: Modifier = Modifier, allowAction: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth().fillMaxHeight()
    ) {
        Text("We need your permission to show notifications")
        Button(
            onClick =  allowAction
        ) {
            Text("Allow")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRequestPreview() {
    WorkBreakTheme {
        NotificationRequest() {

        }
    }
}