package br.com.thedon.travellerapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.thedon.travellerapp.R
import kotlinx.coroutines.launch

@Composable
fun ConfirmDialog(
    show: Boolean = false,
    title: String? = null,
    icon: @Composable () -> Unit,
    content: String,
    confirmActionText: String = stringResource(id = R.string.dialog_action_yes),
    dissmissActionText: String = stringResource(id = R.string.dialog_action_no),
    onDismiss: () -> Unit,
    onConfirm: suspend () -> Unit,
    primaryActionContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    primaryActionContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    val coroutineScope = rememberCoroutineScope()
    if (show) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {
                onDismiss()
            },
            icon = icon,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    if (!title.isNullOrEmpty()) {
                        Text(
                            title,
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Text(
                        content,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(dissmissActionText)
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        onConfirm()
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = primaryActionContainerColor,
                    contentColor = primaryActionContentColor
                )) {
                    Text(confirmActionText)
                }
            }
        )
    }
}