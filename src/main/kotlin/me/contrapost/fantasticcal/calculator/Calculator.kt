package me.contrapost.fantasticcal.calculator

import me.contrapost.fantasticcal.operators.Operator
import me.contrapost.fantasticcal.util.keepNumber

fun calculate(firstNumber: Double, operator: Operator, secondNumber: Double? = null): String =
    operator
        .operatorSpec
        .calculation
        .invoke(firstNumber, processedSecondNumber(operator, secondNumber))
        .toPlainString()

private fun processedSecondNumber(operator: Operator, secondNumber: Double?) = when {
    operator.operatorSpec.operatorWithBase() -> operator.operatorInput.keepNumber()
    else -> secondNumber
}