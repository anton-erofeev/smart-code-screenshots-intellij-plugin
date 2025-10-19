package com.github.antonerofeev.smartcodescreenshots.enums

enum class WatermarkStyle(val displayName: String) {
    HORIZONTAL("Horizontal"),
    DIAGONAL("Diagonal");

    override fun toString(): String = displayName

    companion object {
        fun fromDisplayName(value: String): WatermarkStyle =
            entries.firstOrNull { it.displayName.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown display name: $value")
    }
}