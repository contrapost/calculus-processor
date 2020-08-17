package me.contrapost.calculusprocessor.calculator.operators

import me.contrapost.calculusprocessor.calculator.operations.factorial
import me.contrapost.calculusprocessor.calculator.operations.nthRoot
import me.contrapost.calculusprocessor.calculator.util.NumberRegexes
import java.math.BigDecimal
import kotlin.math.ln
import kotlin.math.log
import kotlin.math.pow

enum class OperatorType {
    UNARY,
    BINARY
}

interface OperatorSpec {
    val symbol: String
    val regex: Regex
    val description: String
    val calculation: (Double, Double?) -> BigDecimal

    fun operatorWithBase() = this is UnaryOperatorSpec && this.operatorWithBase
}

enum class UnaryOperatorSpec(
    override val symbol: String,
    override val regex: Regex,
    override val description: String,
    val operatorPosition: UnaryOperatorPosition,
    val operatorWithBase: Boolean,
    override inline val calculation: (Double, Double?) -> BigDecimal
) : OperatorSpec {
    EXPONENTIATION(
        symbol = "^i",
        regex = "\\^${NumberRegexes.DOUBLE_OR_INT_REGEX}".toRegex(),
        description = "exponentiation with power i",
        operatorPosition = UnaryOperatorPosition.SUCCEED_NUMBER,
        operatorWithBase = true,
        calculation = { firstNumber, secondNumber ->
            BigDecimal(firstNumber.pow(secondNumber!!))
        }
    ),
    ROOT(
        symbol = "V[i]",
        regex = "V${NumberRegexes.DOUBLE_OR_INT_REGEX_IN_BRACES}".toRegex(),
        description = "root with index i",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = true,
        calculation = { firstNumber, secondNumber ->
            require(firstNumber >= 0.0) { "It is impossible to get even radical of negative number!" }
            nthRoot(
                firstNumber,
                secondNumber!!
            )
        }),
    FACTORIAL(
        symbol = "!",
        regex = "!".toRegex(),
        description = "factorial",
        operatorPosition = UnaryOperatorPosition.SUCCEED_NUMBER,
        operatorWithBase = false,
        calculation = { firstNumber, _ ->
            require(firstNumber - firstNumber.toInt() == 0.0) { "Can calculate factorial only of a positive integer" }
            BigDecimal(factorial(firstNumber))
        }
    ),
    LOGARITHM(
        symbol = "log[b]",
        regex = "log${NumberRegexes.DOUBLE_OR_INT_REGEX_POSITIVE_IN_BRACES}".toRegex(),
        description = "logarithm with base b",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = true,
        calculation = { firstNumber, secondNumber ->
            require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
            BigDecimal(log(firstNumber, secondNumber!!))
        }
    ),
    LOGARITHM_E(
        symbol = "ln",
        regex = "ln".toRegex(),
        description = "logarithm with base e",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = false,
        calculation = { firstNumber, _ ->
            require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
            BigDecimal(ln(firstNumber))
        }
    );

    fun precedeNumber() = operatorPosition == UnaryOperatorPosition.PRECEDE_NUMBER
    fun succeedNumber() = operatorPosition == UnaryOperatorPosition.SUCCEED_NUMBER
}

enum class BinaryOperatorSpec(
    override val symbol: String,
    override val regex: Regex,
    override val description: String,
    val precedence: BinaryOperatorPrecedence,
    override inline val calculation: (Double, Double?) -> BigDecimal
): OperatorSpec {
    ADDITION(
        symbol = "+",
        regex = "\\+".toRegex(),
        description = "addition",
        precedence = BinaryOperatorPrecedence.SECOND,
        calculation = { firstNumber, secondNumber ->
            require(secondNumber != null) {
                binaryOperationError(
                    "addition"
                )
            }
            BigDecimal(firstNumber + secondNumber)
        }
    ),
    MULTIPLICATION(
        symbol = "*",
        regex = "\\*".toRegex(),
        description = "multiplication",
        precedence = BinaryOperatorPrecedence.FIRST,
        calculation = { firstNumber, secondNumber ->
            require(secondNumber != null) {
                binaryOperationError(
                    "multiplication"
                )
            }
            BigDecimal(firstNumber * secondNumber)
        }
    ),
    DIVISION(
        symbol = "/",
        regex = "/".toRegex(),
        description = "division",
        precedence = BinaryOperatorPrecedence.FIRST,
        calculation = { firstNumber, secondNumber ->
            require(secondNumber != null) {
                binaryOperationError(
                    "division"
                )
            }
            require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
            BigDecimal(firstNumber / secondNumber)
        }
    ),
    MODULUS(
        symbol = "%",
        regex = "%".toRegex(),
        description = "modulus",
        precedence = BinaryOperatorPrecedence.FIRST,
        calculation = { firstNumber, secondNumber ->
            require(secondNumber != null) {
                binaryOperationError(
                    "modulus"
                )
            }
            require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
            BigDecimal(firstNumber % secondNumber)
        }
    ),
    SUBTRACTION(
        symbol = "-",
        regex = subtractionRegex().toRegex(),
        description = "subtraction",
        precedence = BinaryOperatorPrecedence.SECOND,
        calculation = { firstNumber, secondNumber ->
            require(secondNumber != null) {
                binaryOperationError(
                    "subtraction"
                )
            }
            BigDecimal(firstNumber - secondNumber)
        }
    )
}

enum class BinaryOperatorPrecedence(val precedence: Int) {
    FIRST(1),
    SECOND(2)
}

enum class ParenthesisRegexes(val regex: String) {
    OPEN_PARENTHESIS("\\("),
    CLOSE_PARENTHESIS("\\)")
}

enum class UnaryOperatorPosition {
    PRECEDE_NUMBER,
    SUCCEED_NUMBER
}

private fun subtractionRegex(): String {
    val minus = "-"
    val precedingRegexes = precedingUnaryOperatorRegexes() + NumberRegexes.DOUBLE_OR_INT_REGEX + ParenthesisRegexes.OPEN_PARENTHESIS.regex
    val succeedingRegexes = succeedingUnaryOperatorRegexes() + NumberRegexes.DOUBLE_OR_INT_REGEX + ParenthesisRegexes.CLOSE_PARENTHESIS.regex
    val regexes = mutableListOf<String>()

    precedingRegexes.forEach { precedingRegex ->
        succeedingRegexes.forEach { succeedingRegex ->
            regexes.add("((?<=$succeedingRegex)$minus(?=$precedingRegex))")
        }
    }

    return regexes.joinToString(separator = "+|")
}

private fun precedingUnaryOperatorRegexes() =
    UnaryOperatorSpec.values().filter { it.operatorPosition == UnaryOperatorPosition.PRECEDE_NUMBER }
        .map { it.regex }

private fun succeedingUnaryOperatorRegexes() =
    UnaryOperatorSpec.values().filter { it.operatorPosition == UnaryOperatorPosition.SUCCEED_NUMBER }
        .map { it.regex }

data class Operator(val operatorInput: String, val operatorSpec: OperatorSpec)

val operators: List<OperatorSpec> = UnaryOperatorSpec.values().toList() + BinaryOperatorSpec.values().toList()

fun binaryOperationError(operationDescription: String) =
    "Binary operation $operationDescription requires two operands. Second number is missing."

val operatorsWithDescriptions = operators.associate { it.symbol to it.description }