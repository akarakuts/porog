/** AboutScreen — версия, лицензия и контакты автора. */
package ru.akarakuts.porog.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import ru.akarakuts.porog.R
import ru.akarakuts.porog.ui.components.PorogCard
import ru.akarakuts.porog.ui.components.PorogMark

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val info = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = info.versionName ?: "1.0.0"
    val versionCode = PackageInfoCompat.getLongVersionCode(info).toInt()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PorogMark(size = 88.dp)
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
        Text(
            stringResource(R.string.about_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        PorogCard {
            Text(
                stringResource(R.string.about_version, versionName, versionCode),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(stringResource(R.string.about_author), style = MaterialTheme.typography.bodyLarge)
            Text(
                stringResource(R.string.about_license),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        FilledTonalButton(
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:aleksey@karakuts.com")),
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Icon(Icons.Filled.Mail, contentDescription = null)
            Text(stringResource(R.string.about_email), modifier = Modifier.padding(start = 8.dp))
        }
        FilledTonalButton(
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/akarakuts/porog")),
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Icon(Icons.Filled.Code, contentDescription = null)
            Text(stringResource(R.string.about_source), modifier = Modifier.padding(start = 8.dp))
        }
    }
}
