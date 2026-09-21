package com.rocybyte.weisome.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rocybyte.weisome.ui.WeiSomeBorders
import com.rocybyte.weisome.ui.WeiSomeColors
import com.rocybyte.weisome.ui.WeiSomeShapes
import com.rocybyte.weisome.ui.WeiSomeSpacing
import com.rocybyte.weisome.ui.WeiSomeTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** How long a snackbar stays on screen before dismissing itself. */
private const val SNACKBAR_DURATION_MS = 3_000L

/** One snackbar payload; [id] forces the host timer to restart on repeated identical messages. */
data class WeiSomeSnackbarData(
    val id: Long,
    val message: String,
    val isError: Boolean = false,
)

/** Holds the current snackbar payload owned by the application root. */
class WeiSomeSnackbarState {
    private val _data = MutableStateFlow<WeiSomeSnackbarData?>(null)

    /** Current snackbar content, or null while nothing is shown. */
    val data: StateFlow<WeiSomeSnackbarData?> = _data.asStateFlow()
    private var nextId = 0L

    /** Shows [message] as a snackbar; failures set [isError] to render in the error color. */
    fun show(message: String, isError: Boolean = false) {
        _data.value = WeiSomeSnackbarData(id = ++nextId, message = message, isError = isError)
    }

    /** Clears the current snackbar. Called by the host when the display duration elapses. */
    fun dismiss() {
        _data.value = null
    }
}

/** Provides the application-wide [WeiSomeSnackbarState] so any destination can show feedback. */
val LocalWeiSomeSnackbar = staticCompositionLocalOf<WeiSomeSnackbarState> {
    error("WeiSomeSnackbarState is not provided; WeiSomeApp must supply LocalWeiSomeSnackbar.")
}

/**
 * Bottom-center glass snackbar per the DESIGN.md overlay spec: semi-transparent white fill,
 * thin border, and a soft airy shadow. Slides in on [WeiSomeSnackbarState.show] and
 * auto-dismisses after a short delay.
 */
@Composable
internal fun WeiSomeSnackbarHost(
    state: WeiSomeSnackbarState,
    modifier: Modifier = Modifier,
) {
    val data by state.data.collectAsState()

    // Keep the last payload visible during the exit animation after dismiss() nulls the state.
    var lastData by remember { mutableStateOf<WeiSomeSnackbarData?>(null) }
    if (data != null) lastData = data

    LaunchedEffect(data) {
        if (data != null) {
            delay(SNACKBAR_DURATION_MS)
            state.dismiss()
        }
    }

    AnimatedVisibility(
        visible = data != null,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it },
        modifier = modifier.padding(WeiSomeSpacing.margin),
    ) {
        val current = lastData ?: return@AnimatedVisibility
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shadow(
                    elevation = 32.dp,
                    shape = WeiSomeShapes.lg,
                    spotColor = Color.Black.copy(alpha = 0.05f),
                    ambientColor = Color.Black.copy(alpha = 0.05f),
                )
                .clip(WeiSomeShapes.lg)
                .background(Color.White.copy(alpha = 0.85f))
                .border(WeiSomeBorders.thin, WeiSomeColors.cardBorder, WeiSomeShapes.lg)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            WeiSomeText(
                text = current.message,
                style = WeiSomeTypography.bodyMd,
                color = if (current.isError) WeiSomeColors.error else WeiSomeColors.onSurface,
            )
        }
    }
}
