package com.titanos.domain.model

data class AppInfo(
    val packageName: String,
    val label: String,
    val isGame: Boolean,
    val supportsProton: Boolean
)
