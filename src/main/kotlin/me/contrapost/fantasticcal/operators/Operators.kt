package me.contrapost.fantasticcal.operators

import me.contrapost.fantasticcal.operations.factorial
import me.contrapost.fantasticcal.operations.nthRoot
import me.contrapost.fantasticcal.util.DOUBLE_OR_INT_REGEX
import me.contrapost.fantasticcal.util.DOUBLE_OR_INT_REGEX_IN_BRACES
import me.contrapost.fantasticcal.util.DOUBLE_OR_INT_REGEX_POSITIVE_IN_BRACES
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
        regex = "\\+".toRegex(),
        description = "addition"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("addition") }
        BigDecimal(firstNumber + secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "-",
        regex = "(?<=(-)?$DOUBLE_OR_INT_REGEX)-(?=(-)?$DOUBLE_OR_INT_REGEX)".toRegex(),
        description = "subtraction"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("subtraction") }
        BigDecimal(firstNumber - secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "*",
        regex = "\\*".toRegex(),
        description = "multiplication"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("multiplication") }
        BigDecimal(firstNumber * secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "/",
        regex = "/".toRegex(),
        description = "division"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("division") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        BigDecimal(firstNumber / secondNumber)
    },
    BinaryOperatorSpec(
        symbol = "%",
        regex = "%".toRegex(),
        description = "modulus"
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("modulus") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        BigDecimal(firstNumber % secondNumber)
    },
    UnaryOperatorSpec(
        symbol = "^i",
        regex = "\\^$DOUBLE_OR_INT_REGEX".toRegex(),
        description = "exponentiation with power i",
        operatorPosition = UnaryOperatorPosition.SUCCEED_NUMBER,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        BigDecimal(firstNumber.pow(secondNumber!!))
    },
    UnaryOperatorSpec(
        symbol = "V[i]",
        regex = "V$DOUBLE_OR_INT_REGEX_IN_BRACES".toRegex(),
        description = "root with index i",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        require(firstNumber >= 0.0) { "It is impossible to get even radical of negative number!" }
        nthRoot(firstNumber, secondNumber!!)
    },
    UnaryOperatorSpec(
        symbol = "!",
        regex = "!".toRegex(),
        description = "factorial",
        operatorPosition = UnaryOperatorPosition.SUCCEED_NUMBER,
        operatorWithBase = false
    ) { firstNumber, _ ->
        require(firstNumber - firstNumber.toInt() == 0.0) { "Can calculate factorial only of a positive integer" }
        BigDecimal(factorial(firstNumber))
    },
    UnaryOperatorSpec(
        symbol = "log[b]",
        regex = "log$DOUBLE_OR_INT_REGEX_POSITIVE_IN_BRACES".toRegex(),
        description = "logarithm with base b",
        operatorPosition = UnaryOperatorPosition.PRECEDE_NUMBER,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
        BigDecimal(log(firstNumber, secondNumber!!))
    },
    UnaryOperatorSpec(
        symbol = "ln",
        regex = "ln".toRegex(),
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