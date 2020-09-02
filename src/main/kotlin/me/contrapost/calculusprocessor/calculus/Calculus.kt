package me.contrapost.calculusprocessor.calculus

import me.contrapost.calculusprocessor.operators.BinaryOperatorPrecedence
import me.contrapost.calculusprocessor.operators.BinaryOperatorSpec

data class Calculus(val parts: List<CalculusPart>) {
    private val binaryOperators = parts.filterIsInstance(OperatorPart::class.java).filter { it.binaryOperator() }

    val complex = parts.filterIsInstance(ParenthesisPart::class.java).isNotEmpty()
    val hasUnaryOperators = parts.filterIsInstance(OperatorPart::class.java).any { it.unaryOperator() }
    val hasBinaryOperatorsWithPrecedence =
        binaryOperators.any { (it.value.operatorSpec as BinaryOperatorSpec).precedence == BinaryOperatorPrecedence.FIRST }
    val hasBinaryOperatorsWithoutPrecedence =
        binaryOperators.any { (it.value.operatorSpec as BinaryOperatorSpec).precedence == BinaryOperatorPrecedence.SECOND }
}