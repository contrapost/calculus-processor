package me.contrapost.fantasticcal.calculator

import me.contrapost.fantasticcal.calculus.CalculusPart
import me.contrapost.fantasticcal.operators.Operator
import me.contrapost.fantasticcal.util.keepNumber
import java.math.BigDecimal

fun calculate(calculationParts: List<CalculusPart>): BigDecimal {
    // println(calculationParts)
    return calculate(
        (calculationParts[0].value as BigDecimal).toDouble(),
        calculationParts[1].value as Operator,
        when (calculationParts.size) {
            3 -> (calculationParts[2].value as BigDecimal).toDouble()
            else -> null
        }
    )
}

fun calculate(firstNumber: Double, operator: Operator, secondNumber: Double? = null): BigDecimal =
    operator
        .operatorSpec
        .calculation
        .invoke(firstNumber, processedSecondNumber(operator, secondNumber))

private fun processedSecondNumber(operator: Operator, secondNumber: Double?) = when {
    operator.operatorSpec.operatorWithBase() -> operator.operatorInput.keepNumber()
    else -> secondNumber
}
