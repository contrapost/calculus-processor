package me.contrapost.fantasticcal.util

import me.contrapost.fantasticcal.calculus.CalculusPart
import me.contrapost.fantasticcal.calculus.OperatorPart

object NumberRegexes {
    const val DOUBLE_OR_INT_REGEX = "(-)?(\\d{1,1000}(\\.\\d{1,1000})?+|\\.\\d{1,1000})"
    private const val DOUBLE_OR_INT_REGEX_POSITIVE = "(\\d{1,1000}(\\.\\d{1,1000})?+|\\.\\d{1,1000})"
    const val DOUBLE_OR_INT_REGEX_IN_BRACES = "\\[$DOUBLE_OR_INT_REGEX\\]"
    const val DOUBLE_OR_INT_REGEX_POSITIVE_IN_BRACES = "\\[$DOUBLE_OR_INT_REGEX_POSITIVE\\]"
}

fun String.keepNumber(): Double = replace("[^0-9.-]".toRegex(), "").toDouble()

fun String.removeWhitespaces() = this.replace(" ", "")

fun List<CalculusPart>.toCalculusString() = this.map {
    when (it) {
        is OperatorPart -> it.value.operatorInput
        else -> it.value
    }
}.joinToString(" ")
