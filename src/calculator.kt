import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.*

const val DOUBLE_OR_INT_REGEX = "(\\d+(\\.\\d+)?+|\\.\\d+)"
const val DOUBLE_OR_INT_REGEX_IN_BRACES = "\\[$DOUBLE_OR_INT_REGEX\\]"

val operators = listOf(
        OperatorSpec(
                symbol = "+",
                regex = "\\+".toRegex(),
                description = "addition",
                canPrecedeNumber = false,
                unaryOperator = false,
                operatorWithBase = false
        ) { firstNumber, secondNumber ->
            require(secondNumber != null) { binaryOperationError("addition") }
            BigDecimal(firstNumber + secondNumber)
        },
        OperatorSpec(
                symbol = "-",
                regex = "-".toRegex(),
                description = "subtraction",
                canPrecedeNumber = true,
                unaryOperator = false,
                operatorWithBase = false
        ) { firstNumber, secondNumber ->
            require(secondNumber != null) { binaryOperationError("subtraction") }
            BigDecimal(firstNumber - secondNumber)
        },
        OperatorSpec(
                symbol = "*",
                regex = "\\*".toRegex(),
                description = "multiplication",
                canPrecedeNumber = false,
                unaryOperator = false,
                operatorWithBase = false
        ) { firstNumber, secondNumber ->
            require(secondNumber != null) { binaryOperationError("multiplication") }
            BigDecimal(firstNumber * secondNumber)
        },
        OperatorSpec(
                symbol = "/",
                regex = "/".toRegex(),
                description = "division",
                canPrecedeNumber = false,
                unaryOperator = false,
                operatorWithBase = false
        ) { firstNumber, secondNumber ->
            require(secondNumber != null) { binaryOperationError("division") }
            require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
            BigDecimal(firstNumber / secondNumber)
        },
        OperatorSpec(
                symbol = "^i",
                regex = "\\^$DOUBLE_OR_INT_REGEX".toRegex(),
                description = "exponentiation with power i",
                canPrecedeNumber = false,
                unaryOperator = true,
                operatorWithBase = true
        ) { firstNumber, secondNumber ->
            BigDecimal(firstNumber.pow(secondNumber!!))
        },
        OperatorSpec(
                symbol = "%",
                regex = "%".toRegex(),
                description = "modulus",
                canPrecedeNumber = false,
                unaryOperator = false,
                operatorWithBase = false
        ) { firstNumber, secondNumber ->
            require(secondNumber != null) { binaryOperationError("modulus") }
            require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
            BigDecimal(firstNumber % secondNumber)
        },
        OperatorSpec(
                symbol = "V[i]",
                regex = "V$DOUBLE_OR_INT_REGEX_IN_BRACES".toRegex(),
                description = "root with index i",
                canPrecedeNumber = true,
                unaryOperator = true,
                operatorWithBase = true
        ) { firstNumber, secondNumber ->
            require(firstNumber >= 0.0) { "It is impossible to get even radical of negative number!" }
            nthRoot(firstNumber, secondNumber!!)
        },
        OperatorSpec(
                symbol = "!",
                regex = "!".toRegex(),
                description = "factorial",
                canPrecedeNumber = false,
                unaryOperator = true,
                operatorWithBase = false
        ) { firstNumber, _ ->
            require(firstNumber - firstNumber.toInt() == 0.0) { "Can calculate factorial only of a positive integer" }
            BigDecimal(factorial(firstNumber))
        },
        OperatorSpec(
                symbol = "log[b]",
                regex = "log$DOUBLE_OR_INT_REGEX_IN_BRACES".toRegex(),
                description = "logarithm with base b",
                canPrecedeNumber = true,
                unaryOperator = true,
                operatorWithBase = true
        ) { firstNumber, secondNumber ->
            require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
            BigDecimal(log(firstNumber, secondNumber!!))
        },
        OperatorSpec(
                symbol = "ln",
                regex = "ln".toRegex(),
                description = "logarithm with base e",
                canPrecedeNumber = true,
                unaryOperator = true,
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

fun calculate(firstNumber: Double, operator: Operator, secondNumber: Double? = null): String {
    val processedSecondNumber = when {
        operator.operatorSpec.operatorWithBase -> operator.operatorInput.getBase()
        else -> secondNumber
    }
    return operator.operatorSpec.calculation.invoke(firstNumber, processedSecondNumber).toPlainString()
}

private fun String.getBase(): Double = replace("[^0-9.]".toRegex(), "").toDouble()

data class OperatorSpec(
        val symbol: String,
        val regex: Regex,
        val description: String,
        val canPrecedeNumber: Boolean,
        val unaryOperator: Boolean,
        val operatorWithBase: Boolean,
        inline val calculation: (Double, Double?) -> BigDecimal
)

data class Operator(val operatorInput: String, val operatorSpec: OperatorSpec)

fun nthRoot(num: Double, index: Double): BigDecimal {
    val temporaryResult = Math.E.pow(ln(num) / index)
    val rounded = round(temporaryResult)
    return BigDecimal(when {
        abs(rounded - temporaryResult) < 0.00000000000002 -> rounded
        else -> temporaryResult
    })
}

fun factorial(number: Double): BigInteger {
    var result = BigInteger.ONE
    (2..number.toInt()).forEach { result = result.multiply(BigInteger.valueOf(it.toLong())) }
    return result
}
