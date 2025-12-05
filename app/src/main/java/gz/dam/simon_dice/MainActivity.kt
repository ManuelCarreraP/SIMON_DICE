package gz.dam.simon_dice


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import gz.dam.simon_dice.ui.theme.SimonDiceTheme



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SimonDiceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SimonDiceUI()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundPlayer.getInstance(this).release()
    }
}