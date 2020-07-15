package me.contrapost.fantasticcal.operators

import me.contrapost.fantasticcal.operators.NumberRegexes.DOUBLE_OR_INT_REGEX
import me.contrapost.fantasticcal.operators.NumberRegexes.DOUBLE_OR_INT_REGEX_IN_BRACES
import me.contrapost.fantasticcal.operators.NumberRegexes.DOUBLE_OR_INT_REGEX_POSITIVE_IN_BRACES

object NumberRegexes {
    const val DOUBLE_OR_INT_REGEX = "(-)?(\\d{1,1000}(\\.\\d{1,1000})?+|\\.\\d{1,1000})"
    private const val DOUBLE_OR_INT_REGEX_POSITIVE = "(\\d{1,1000}(\\.\\d{1,1000})?+|\\.\\d{1,1000})"
    const val DOUBLE_OR_INT_REGEX_IN_BRACES = "\\[$DOUBLE_OR_INT_REGEX\\]"
    const val DOUBLE_OR_INT_REGEX_POSITIVE_IN_BRACES = "\\[$DOUBLE_OR_INT_REGEX_POSITIVE\\]"
}

enum class UnaryOperatorRegexes(val regex: String, val operatorPosition: UnaryOperatorPosition) {
    EXPONENTIATION("\\^${DOUBLE_OR_INT_REGEX}", UnaryOperatorPosition.SUCCEED_NUMBER),
    ROOT("V${DOUBLE_OR_INT_REGEX_IN_BRACES}", UnaryOperatorPosition.PRECEDE_NUMBER),
    FACTORIAL("!", UnaryOperatorPosition.SUCCEED_NUMBER),
    LOGARITHM("log${DOUBLE_OR_INT_REGEX_POSITIVE_IN_BRACES}", UnaryOperatorPosition.PRECEDE_NUMBER),
    LOGARITHM_E("ln", UnaryOperatorPosition.PRECEDE_NUMBER)
}

enum class ParenthesisRegexes(val regex: String) {
    OPEN_PARENTHESIS("\\("),
    CLOSE_PARENTHESIS("\\)")
}

enum class BinaryOperatorRegexes(val regex: String) {
    ADDITION("\\+"),
    MULTIPLICATION("\\*"),
    DIVISION("/"),
    MODULUS("%"),
    SUBTRACTION(subtractionRegex())
}

fun subtractionRegex(): String {
    val minus = "-"
    val precedingRegexes = precedingUnaryOperatorRegexes() + DOUBLE_OR_INT_REGEX + ParenthesisRegexes.OPEN_PARENTHESIS.regex
    val succeedingRegexes = succeedingUnaryOperatorRegexes() + DOUBLE_OR_INT_REGEX + ParenthesisRegexes.CLOSE_PARENTHESIS.regex
    val regexes = mutableListOf<String>()

    precedingRegexes.forEach { precedingRegex ->
        succeedingRegexes.forEach { succeedingRegex ->
            regexes.add("((?<=$succeedingRegex)$minus(?=$precedingRegex))")
        }
    }

    return regexes.joinToString(separator = "+|")
}

fun precedingUnaryOperatorRegexes() =
    UnaryOperatorRegexes.values().filter { it.operatorPosition == UnaryOperatorPosition.PRECEDE_NUMBER }
        .map { it.regex }

fun succeedingUnaryOperatorRegexes() =
    UnaryOperatorRegexes.values().filter { it.operatorPosition == UnaryOperatorPosition.SUCCEED_NUMBER }
        .map { it.regex }

fun String.keepNumber(): Double = replace("[^0-9.-]".toRegex(), "").toDouble()

fun String.removeWhitespaces() = this.replace(" ", "")