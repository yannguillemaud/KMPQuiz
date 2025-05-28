package ygmd.kmpquiz.android.event

data class ClickActions<T>(
    val onClick: ((T) -> Unit)? = null,
    val onLongClick: ((T) -> Unit)? = null,
    val onDoubleTap: ((T) -> Unit)? = null,
)