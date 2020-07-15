package me.contrapost.fantasticcal.operators

import me.contrapost.fantasticcal.operations.factorial
import me.contrapost.fantasticcal.operations.nthRoot
import me.contrapost.fantasticcal.util.BinaryOperatorRegexes.ADDITION
import me.contrapost.fantasticcal.util.BinaryOperatorRegexes.DIVISION
import me.contrapost.fantasticcal.util.BinaryOperatorRegexes.MODULUS
import me.contrapost.fantasticcal.util.BinaryOperatorRegexes.MULTIPLICATION
import me.contrapost.fantasticcal.util.BinaryOperatorRegexes.SUBTRACTION
import me.contrapost.fantasticcal.util.UnaryOperatorRegexes.EXPONENTIATION
import me.contrapost.fantasticcal.util.UnaryOperatorRegexes.FACTORIAL
import me.contrapost.fantasticcal.util.UnaryOperatorRegexes.LOGARITHM
import me.contrapost.fantasticcal.util.UnaryOperatorRegexes.LOGARITHM_E
import me.contrapost.fantasticcal.util.UnaryOperatorRegexes.ROOT
import java.math.BigDecimal
import kotlin.math.ln
import kotlin.math.log
import kotlin.math.pow

abstract class OperatorSpec {
    abstract val symbol: String
    abstract val regex: Regex
    abstract val description: String
    abstract val calculation: (Double, Double?) -> BigDecimal

    fun operatorWithBase() = this is UnaryOperatorSpec && this.operatorWithBase
}

data class BinaryOperatorSpec(
    override val symbol: String,
    override val regex: Regex,
    override val description: String,
    override inline val calculation: (Double, Double?) -> BigDecimal
): OperatorSpec()

data class UnaryOperatorSpec(
    override val symbol: String,
    override val regex: Regex,
    override val description: String,
    val operatorPosition: UnaryOperatorPosition,
    val operatorWithBase: Boolean,
    override inline val calculation: (Double, Double?) -> BigDecimal
): OperatorSpec() {
    fun precedeNumber() = operatorPosition == UnaryOperatorPosition.PRECEDE_NUMBER
    fun succeedNumber() = operatorPosition == UnaryOperatorPosition.SUCCEED_NUMBER
}

enum class UnaryOperatorPosition {
    PRECEDE_NUMBER,
    SUCCEED_NUMBER
}

data class Operator(val operatorInput: String, val operatorSpec: OperatorSpec)

val operators = listOf(
    BinaryOperatorSpec(
        symbol = "+",
        regex = ADDITION.regex.toRegex(),
        description = "addition"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("addition") }
        BigDecimal(firstNumber + secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "-",
        regex = SUBTRACTION.regex.toRegex(),
        description = "subtraction"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("subtraction") }
        BigDecimal(firstNumber - secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "*",
        regex = MULTIPLICATION.regex.toRegex(),
        description = "multiplication"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("multiplication") }
        BigDecimal(firstNumber * secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "/",
        regex = DIVISION.regex.toRegex(),
        description = "division"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("division") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        BigDecimal(firstNumber / secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "%",
        regex = MODULUS.regex.toRegex(),
        description = "modulus"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("modulus") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        BigDecimal(firstNumber % secondNumber)
    },
    UnaryOperatorSpec(
        symbol = "^i",
        regex = EXPONENTIATION.regex.toRegex(),
        description = "exponentiation with power i",
        operatorPosition = UnaryOperatorPosition.SUCCEED_NUMBER,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        BigDecimal(firstNumber.pow(secondNumber!!))
    },
    UnaryOperatorSpec(
        symbol = "V[i]",
        regex = ROOT.regex.toRegex(),
        description = "root with index i",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        require(firstNumber >= 0.0) { "It is impossible to get even radical of negative number!" }
        nthRoot(firstNumber, secondNumber!!)
    },
    UnaryOperatorSpec(
        symbol = "!",
        regex = FACTORIAL.regex.toRegex(),
        description = "factorial",
        operatorPosition = UnaryOperatorPosition.SUCCEED_NUMBER,
        operatorWithBase = false
    ) { firstNumber, _ ->
        require(firstNumber - firstNumber.toInt() == 0.0) { "Can calculate factorial only of a positive integer" }
        BigDecimal(factorial(firstNumber))
    },
    UnaryOperatorSpec(
        symbol = "log[b]",
        regex = LOGARITHM.regex.toRegex(),
        description = "logarithm with base b",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
        BigDecimal(log(firstNumber, secondNumber!!))
    },
    UnaryOperatorSpec(
        symbol = "ln",
        regex = LOGARITHM_E.regex.toRegex(),
        description = "logarithm with base e",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = false
    ) { firstNumber, _ ->
        require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
        BigDecimal(ln(firstNumber))
    }
)

fun binaryOperationError(operationDescription: String) =
    "Binary operation $operationDescription requires two operands. Second number is missing."

val operatorsWithDescriptions = operators.associate { it.symbol to it.description }

fun validOperator(operatorInput: String?): Operator? = operatorInput?.let {
    when (val operatorSpec = operatorInput.toOperatorSpec()) {
        null -> null
        else -> Operator(operatorInput, operatorSpec)
    }
}

fun String.toOperatorSpec() = operators.firstOrNull { this.matches(it.regex) }