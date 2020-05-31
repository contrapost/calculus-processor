import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

val binaryOperators = mapOf(
    "+" to OperationDescriptionAndFunction("addition") { firstNumber, secondNumber -> firstNumber + secondNumber!! },
    "-" to OperationDescriptionAndFunction("subtraction") { firstNumber, secondNumber -> firstNumber - secondNumber!! },
    "*" to OperationDescriptionAndFunction("multiplication") { firstNumber, secondNumber -> firstNumber * secondNumber!! },
    "/" to OperationDescriptionAndFunction("division") { firstNumber, secondNumber -> firstNumber / secondNumber!! } ,
    "**" to OperationDescriptionAndFunction("exponentiation") { firstNumber, secondNumber -> firstNumber.pow(secondNumber!!) } ,
    "%" to OperationDescriptionAndFunction("modulus") { firstNumber, secondNumber -> firstNumber % secondNumber!! }
)

val unaryOperators = mapOf(
    "V" to OperationDescriptionAndFunction("square root") { firstNumber, _ -> sqrt(firstNumber) },
    "!" to OperationDescriptionAndFunction("factorial") { firstNumber, _ -> factorial(firstNumber)},
    "log2x" to OperationDescriptionAndFunction("logarithm based on 2") { firstNumber, _ -> log2(firstNumber)},
    "lg" to OperationDescriptionAndFunction("logarithm based on 10") { firstNumber, _ -> log10(firstNumber)}
)

fun factorial(first: Double) {
    TODO("Not yet implemented")
}

val operators = binaryOperators + unaryOperators

fun secondNumberRequired(operator: String) = binaryOperators.containsKey(operator)

fun calculate(firstNumber: Double, operator: String, secondNumber: Double? = null): Double {
    val operationDescriptionAndFunction = operators[operator]
    return operationDescriptionAndFunction!!.calculation.invoke(firstNumber, secondNumber) as Double
}

class OperationDescriptionAndFunction(val description: String, val calculation: (Double, Double?) -> Any)
