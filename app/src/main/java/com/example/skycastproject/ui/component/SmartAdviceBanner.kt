package com.example.skycastproject.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome // Specific import for the icon
import androidx.compose.material3.Icon // Fixed: Missing import
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skycastproject.ui.theme.SkycastProjectTheme

@Composable
fun SmartAdviceBanner(
    message: String,
    urgency: String, // HIGH, MEDIUM, LOW
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (urgency) {
        "HIGH" -> Color(0xFFFFEBEE) // Red tint
        "MEDIUM" -> Color(0xFFFFF9C4) // Yellow tint
        else -> Color(0xFFE3F2FD) // Blue tint
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Using Icons.Filled.AutoAwesome or Icons.Default.AutoAwesome
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Suggestion",
                tint = Color.DarkGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }
}

@Preview(name = "Light Theme", showBackground = true)
@Preview(name = "Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewSmartAdviceBanner() {
    SkycastProjectTheme {
        // Simple background for the preview
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                SmartAdviceBanner(
                    message = "UV is high (8); apply SPF 50 before your run!",
                    urgency = "HIGH"
                )
            }
        }
    }
}