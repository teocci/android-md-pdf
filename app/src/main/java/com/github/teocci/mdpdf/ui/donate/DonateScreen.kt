package com.github.teocci.mdpdf.ui.donate

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.teocci.mdpdf.theme.LocalDesignSystem

@Composable
fun DonateScreen() {
    val context = LocalContext.current
    val designSystem = LocalDesignSystem.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(designSystem.spacing.lg.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_dialog_dialer),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(designSystem.spacing.lg.dp))
        
        Text(
            text = "Support Development",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(designSystem.spacing.md.dp))
        
        Text(
            text = "If you find this app useful, consider supporting its development. Your contribution helps keep the app free and ad-free for everyone.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = designSystem.spacing.lg.dp)
        )
        
        Spacer(modifier = Modifier.height(designSystem.spacing.xl.dp))
        
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/teocci"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Spacer(modifier = Modifier.width(designSystem.spacing.sm.dp))
            Text("Buy me a coffee")
        }
        
        Spacer(modifier = Modifier.height(designSystem.spacing.md.dp))
        
        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patreon.com/teocci"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Spacer(modifier = Modifier.width(designSystem.spacing.sm.dp))
            Text("Patreon")
        }
    }
}