import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

val binaryOperators = mapOf(
    "+" to OperationDescriptionAndFunction("addition") { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("addition") }
        firstNumber + secondNumber
    },
    "-" to OperationDescriptionAndFunction("subtraction") { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("subtraction") }
        firstNumber - secondNumber
    },
    "*" to OperationDescriptionAndFunction("multiplication") { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("multiplication") }
        firstNumber * secondNumber
    },
    "/" to OperationDescriptionAndFunction("division") { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("division") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        firstNumber / secondNumber
    } ,
    "**" to OperationDescriptionAndFunction("exponentiation") { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("exponentiation") }
        firstNumber.pow(secondNumber)
    } ,
    "%" to OperationDescriptionAndFunction("modulus") { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("modulus") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        firstNumber % secondNumber
    }
)

fun binaryOperationError(operationDescription: String) = "Binary operation $operationDescription requires two operands. Second number is missing."

val unaryOperators = mapOf(
    "V" to OperationDescriptionAndFunction("square root") { firstNumber, _ ->
        require(firstNumber >= 0.0) { "It is impossible to get even radical of negative number!" }
        sqrt(firstNumber)
    },
    "!" to OperationDescriptionAndFunction("factorial") { firstNumber, _ ->
        require(firstNumber >= 0.0) { "It is possible to get factorial only of integer! You must use integer." }
        factorial(firstNumber)
    },
    "log2x" to OperationDescriptionAndFunction("logarithm based on 2") { firstNumber, _ ->
        require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
        log2(firstNumber)
    },
    "lg" to OperationDescriptionAndFunction("logarithm based on 10") { firstNumber, _ ->
        require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
        log10(firstNumber)
    }
)

fun factorial(first: Double): Double {
    TODO("Not yet implemented")
}

val operators = binaryOperators + unaryOperators

fun secondNumberRequired(operator: String) = binaryOperators.containsKey(operator)

fun calculate(firstNumber: Double, operator: String, secondNumber: Double? = null): Double {
    val operationDescriptionAndFunction = operators[operator]
    return operationDescriptionAndFunction!!.calculation.invoke(firstNumber, secondNumber) as Double
}

class OperationDescriptionAndFunction(val description: String, val calculation: (Double, Double?) -> Double)
