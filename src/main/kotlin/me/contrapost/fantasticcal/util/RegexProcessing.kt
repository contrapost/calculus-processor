package me.contrapost.fantasticcal.util

const val DOUBLE_OR_INT_REGEX = "(\\d+(\\.\\d+)?+|\\.\\d+)"
const val DOUBLE_OR_INT_REGEX_IN_BRACES = "\\[$DOUBLE_OR_INT_REGEX\\]"

fun String.keepNumber(): Double = replace("[^0-9.]".toRegex(), "").toDouble()

fun String.removeWhitespaces() = this.replace(" ", "")