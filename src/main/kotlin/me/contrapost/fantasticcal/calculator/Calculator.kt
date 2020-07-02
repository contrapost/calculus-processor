package me.contrapost.fantasticcal.calculator

import me.contrapost.fantasticcal.operators.Operator
import me.contrapost.fantasticcal.util.keepNumber

fun calculate(firstNumber: Double, operator: Operator, secondNumber: Double? = null): String {
    val processedSecondNumber = when {
        operator.operatorSpec.operatorWithBase -> operator.operatorInput.keepNumber()
        else -> secondNumber
    }
    return operator.operatorSpec.calculation.invoke(firstNumber, processedSecondNumber).toPlainString()
}