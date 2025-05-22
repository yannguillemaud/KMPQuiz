package ygmd.kmpquiz.android.ui.event

data class ClickActions<T>(
    val onClick: ((T) -> Unit)? = null,
    val onLongClick: ((T) -> Unit)? = null,
    val onDoubleTap: ((T) -> Unit)? = null,
)