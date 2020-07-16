package me.contrapost.fantasticcal.calculator

import me.contrapost.fantasticcal.calculus.CalculusPart
import me.contrapost.fantasticcal.operators.Operator
import me.contrapost.fantasticcal.operators.keepNumber
import java.math.BigDecimal

fun calculate(calculationParts: List<CalculusPart>): BigDecimal {
    TODO("Not yet implemented")
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