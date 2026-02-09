package com.javohir.masterbektask.presentation.components
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.presentation.conversation
 * Description:
 */
@Composable
fun StartChatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.6f,
        animationSpec = tween(durationMillis = 300),
        label = "button_alpha"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .alpha(alpha)
            .padding(horizontal = 32.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(
            horizontal = 32.dp,
            vertical = 16.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Start Chat",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Start Chat",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
