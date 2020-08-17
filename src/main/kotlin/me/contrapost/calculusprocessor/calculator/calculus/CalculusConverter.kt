package me.contrapost.calculusprocessor.calculator.calculus

import me.contrapost.calculusprocessor.calculator.operators.*
import me.contrapost.calculusprocessor.calculator.util.NumberRegexes.DOUBLE_OR_INT_REGEX

val calculusRegexes = listOf(
    NumberPart::class to DOUBLE_OR_INT_REGEX.toRegex(),
    OpenParenthesisPart::class to ParenthesisRegexes.OPEN_PARENTHESIS.regex.toRegex(),
    CloseParenthesisPart::class to ParenthesisRegexes.CLOSE_PARENTHESIS.regex.toRegex()
)

fun String.toCalculus(): Calculus {
    val withoutWhitespaces = this.removeWhitespaces()
    val operatorsWithRanges = operators.map { operatorSpec ->
        operatorSpec.regex.findAll(withoutWhitespaces).map {
            CalculusPartWithRange(
                it.range,
                OperatorPart(
                    Operator(
                        it.value,
                        operatorSpec
                    ),
                    type = when (operatorSpec) {
                        is UnaryOperatorSpec -> "unary operator"
                        is BinaryOperatorSpec -> "binary operator"
                        else -> throw UnsupportedClassVersionError("") // TODO
                    }
                )
            )
        }.toList()
    }.flatten()

    val calculusPartList = withoutWhitespaces.extractMatches(operatorsWithRanges)

    val calculusPartListComplete: MutableList<CalculusPart> = mutableListOf()

    when {
        calculusPartList.isEmpty() -> {
            calculusPartListComplete.addNumbersAndParentheses(withoutWhitespaces)
        }
        else -> {
            calculusPartList.forEach { entry ->
                when (entry) {
                    is OperatorPart -> calculusPartListComplete.add(entry)
                    else -> {
                        val undefinedCalculusPart = (entry as UndefinedPart).value
                        calculusPartListComplete.addNumbersAndParentheses(undefinedCalculusPart)
                    }
                }
            }
        }
    }

    return Calculus(calculusPartListComplete)
}

@Suppress("LiftReturnOrAssignment")
fun String.extractMatches(calculusPartsWithRanges: List<CalculusPartWithRange>): List<CalculusPart> {
    var stopIndex = 0
    val stringAsListOfRanges: MutableList<CalculusPart> = mutableListOf()
    calculusPartsWithRanges.sortedBy { it.range.first }.forEachIndexed { index, entry ->
        when (index) {
            // last match
            calculusPartsWithRanges.size - 1 -> {
                when {
                    // part of undefined string is longer than match
                    stopIndex until this.length != entry.range -> {
                        when {
                            entry.range.last == this.length - 1 -> {
                                stringAsListOfRanges.add(
                                    UndefinedPart(
                                        this.substring(
                                            stopIndex until entry.range.first
                                        )
                                    )
                                )
                                stringAsListOfRanges.add(entry.calculusPart)
                                stopIndex = entry.range.last + 1
                            }
                            entry.range.first == stopIndex -> {
                                stringAsListOfRanges.add(entry.calculusPart)
                                stringAsListOfRanges.add(
                                    UndefinedPart(
                                        this.substring(
                                            entry.range.last + 1 until this.length
                                        )
                                    )
                                )
                                stopIndex = this.length - 1
                            }
                            else -> {
                                stringAsListOfRanges.add(
                                    UndefinedPart(
                                        this.substring(
                                            stopIndex until entry.range.first
                                        )
                                    )
                                )
                                stringAsListOfRanges.add(entry.calculusPart)
                                stringAsListOfRanges.add(
                                    UndefinedPart(
                                        this.substring(
                                            entry.range.last + 1 until this.length
                                        )
                                    )
                                )
                                stopIndex = this.length - 1
                            }
                        }
                    }
                    else -> {
                        stringAsListOfRanges.add(entry.calculusPart)
                        stopIndex = this.length - 1
                    }
                }
            }
            // all other cases
            else -> {
                when (entry.range.first) {
                    // the match is right after the last processed part of the string
                    stopIndex -> {
                        stringAsListOfRanges.add(entry.calculusPart)
                        stopIndex = entry.range.last + 1
                    }
                    // there are undefined part of the string before the match
                    else -> {
                        stringAsListOfRanges.add(
                            UndefinedPart(
                                value = this.substring(
                                    stopIndex until entry.range.first
                                )
                            )
                        )
                        stringAsListOfRanges.add(entry.calculusPart)
                        stopIndex = entry.range.last + 1
                    }
                }
            }
        }
    }
    return stringAsListOfRanges
}

fun MutableList<CalculusPart>.addNumbersAndParentheses(undefinedCalculusPart: String) {

    val matches = calculusRegexes.map { regex ->
        regex.second.findAll(undefinedCalculusPart).map {
            CalculusPartWithRange(
                it.range,
                when (regex.first) {
                    NumberPart::class -> NumberPart(
                        it.value.toBigDecimal()
                    )
                    OpenParenthesisPart::class -> OpenParenthesisPart()
                    else -> CloseParenthesisPart()
                }
            )
        }.toList()
    }.flatten()

    when {
        matches.isEmpty() -> {
            this.add(
                UndefinedPart(
                    undefinedCalculusPart
                )
            )
        }
        else -> {
            val definedCalculusPartSublist = undefinedCalculusPart.extractMatches(matches)
            this.addAll(definedCalculusPartSublist)
        }
    }
}

fun String.removeWhitespaces() = this.replace(" ", "")

data class CalculusPartWithRange(
    val range: IntRange,
    val calculusPart: CalculusPart
)

data class Calculus(
    val parts: List<CalculusPart>
) {
    private val binaryOperators = parts.filterIsInstance(OperatorPart::class.java).filter { it.binaryOperator() }

    val complex = parts.filterIsInstance(ParenthesisPart::class.java).isNotEmpty()
    val hasUnaryOperators = parts.filterIsInstance(OperatorPart::class.java).any { it.unaryOperator() }
    val hasBinaryOperatorsWithPrecedence =
        binaryOperators.any { (it.value.operatorSpec as BinaryOperatorSpec).precedence == BinaryOperatorPrecedence.FIRST }
    val hasBinaryOperatorsWithoutPrecedence =
        binaryOperators.any { (it.value.operatorSpec as BinaryOperatorSpec).precedence == BinaryOperatorPrecedence.FIRST }
}
