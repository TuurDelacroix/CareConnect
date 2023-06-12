package be.howest.tuurdelacroix.careconnect.composables

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.howest.tuurdelacroix.careconnect.R
import be.howest.tuurdelacroix.careconnect.data.exitApplication
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.ArrowLeftSolid
import compose.icons.lineawesomeicons.ExclamationCircleSolid
import kotlinx.coroutines.delay
import java.text.DateFormatSymbols
import java.util.Calendar


@Composable
fun CategoryTitle(text: Int, modifier: Modifier)
{
    Text(
        text = stringResource(text).uppercase(),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.primary,
        modifier = modifier
            .padding(vertical = 12.dp)
    )
}

@Composable
fun PageInfoCard(icon: ImageVector, title: Int) {
    val calendar = Calendar.getInstance()
    val currentDayOfWeek = DateFormatSymbols.getInstance().weekdays[calendar.get(Calendar.DAY_OF_WEEK)]
    val currentMonth = DateFormatSymbols.getInstance().months[calendar.get(Calendar.MONTH)]
    val currentYear = calendar.get(Calendar.YEAR)
    val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    Card(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CurrentDateView(currentDayOfWeek, currentDayOfMonth, currentMonth, currentYear)
            }
        }
    }

}

@Composable
fun CurrentDateView(currentDayOfWeek: String, currentDayOfMonth: Int, currentMonth: String, currentYear: Int)
{
    Text(
        text = currentDayOfWeek,
        style = MaterialTheme.typography.subtitle1
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentDayOfMonth.toString(),
            style = MaterialTheme.typography.h4
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = currentMonth,
                style = MaterialTheme.typography.subtitle2
            )
            Text(
                text = currentYear.toString(),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

@Composable
fun PageTitleCard(icon: ImageVector, title: String) {

    Card(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}

@Composable
fun RoundedCard(icon: ImageVector, title: Int, onClick: () -> Unit) {

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = stringResource(title),
                modifier = Modifier
                    .size(56.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.subtitle2,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun ActionButton(
    onClickAction: () -> Unit,
    buttonColor: Int,
    buttonTextColor: Color,
    buttonText: Int,
    modifier: Modifier
)
{
    Button(
        onClick = { onClickAction() },
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .size(58.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(buttonColor), contentColor = buttonTextColor)
    ) {
        Text(
            text = stringResource(buttonText),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            LineAwesomeIcons.ArrowLeftSolid,
            contentDescription = stringResource(R.string.cc_go_back_button_text))
    }
}

@Composable
fun NoContentToShow(noContentText: Int, modifier: Modifier)
{
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(noContentText),
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun LoadingScreen(isLoading: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.cc_app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(100.dp)
                    .rotate(rotation)
            )
            Text(text = "Laden...")
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String, current: Context) {
    var countDown by remember { mutableStateOf(15) }

    LaunchedEffect(Unit) {
        delay(1000)
        for (i in countDown downTo 0) {
            delay(1000)
            countDown = i
        }
        // Close the app after 5 seconds
        exitApplication(current)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                imageVector = LineAwesomeIcons.ExclamationCircleSolid,
                contentDescription = "Error Icon",
                tint = Color.Red,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "OEPS! IETS GING FOUT...",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Deze app sluit over $countDown seconden...",
                style = MaterialTheme.typography.caption
            )
        }
    }
}