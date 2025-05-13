// Arquivo legado, esvaziado para evitar conflitos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.cebolao.R

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name)
            )
        }
        Text(
            text = stringResource(R.string.home_screen_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Text(text = stringResource(R.string.welcome_message))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp),
            fontSize = 30.sp
        )
        Text(
            text = "Bem-vindo ao Cebolão Lotofácil Generator!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )
        Text(
            text = "Este aplicativo foi feito para te auxiliar na geração de jogos da lotofácil com alguns filtros customizados.",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { /* TODO: Navigate to MenuScreen */ },
            modifier = Modifier.padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text("Começar", color = Color.White)
        }
    }
}
