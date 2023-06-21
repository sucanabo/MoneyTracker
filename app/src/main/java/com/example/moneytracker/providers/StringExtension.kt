package com.example.moneytracker.providers

fun String.capitalize(allWords: Boolean = false): String {
    if (!allWords) {
        return this.replaceFirstChar { it.uppercase() }
    }
    return this.split(" ")
        .joinToString {
            char -> char.replaceFirstChar {
                letter -> letter.uppercase()
            }
        }
}