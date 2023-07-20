package br.com.thedon.travellerapp.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.thedon.travellerapp.R
import kotlinx.coroutines.launch

@Composable
fun PlaceRemovalDialog(
    show: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    ConfirmDialog(
        show = show,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.dialog_remove_icon_description),
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
            )

        },
        content = stringResource(R.string.dialog_remove_warning_content),
        dissmissActionText = stringResource(R.string.dialog_remove_action_no),
        confirmActionText = stringResource(R.string.dialog_remove_action_yes),
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        primaryActionContainerColor = MaterialTheme.colorScheme.error,
        primaryActionContentColor = MaterialTheme.colorScheme.onError
    )
}