package me.contrapost.fantasticcal.calculator

import me.contrapost.fantasticcal.calculus.Calculus
import me.contrapost.fantasticcal.calculus.CalculusPart
import me.contrapost.fantasticcal.calculus.NumberPart
import me.contrapost.fantasticcal.calculus.OperatorPart
import me.contrapost.fantasticcal.operators.Operator
import me.contrapost.fantasticcal.operators.BinaryOperatorPrecedence
import me.contrapost.fantasticcal.operators.BinaryOperatorSpec
import me.contrapost.fantasticcal.operators.UnaryOperatorPosition.*
import me.contrapost.fantasticcal.operators.UnaryOperatorSpec
import me.contrapost.fantasticcal.util.keepNumber
import java.math.BigDecimal

fun calculate(calculus: Calculus): BigDecimal {
    val result = calculus.simplifyComplexCalculus().parts.calculateParts()
    if (result.size != 1 || result[0] !is NumberPart) throw IllegalArgumentException("Unexpected exception")

    return result[0].value as BigDecimal
}

fun List<CalculusPart>.calculateParts(): List<CalculusPart> = this.calculateUnaryParts()
    .calculateBinaryParts(BinaryOperatorPrecedence.FIRST)
    .calculateBinaryParts(BinaryOperatorPrecedence.SECOND)

tailrec fun Calculus.simplifyComplexCalculus(): Calculus =
    if (!this.complex) this
    else Calculus(this.parts.calculateParts().removeExcessiveParenthesis()).simplifyComplexCalculus()

fun List<CalculusPart>.removeExcessiveParenthesis(): List<CalculusPart> {
    val calculusPartList = mutableListOf<CalculusPart>()
    var index = 0
    while (index < this.size) {
        val entry = this[index]
        if (entry is NumberPart && (calculusPartList.isNotEmpty() && calculusPartList[calculusPartList.size - 1].parentheses()) && this[index + 1].parentheses()) {
            calculusPartList[calculusPartList.size - 1] = entry
            index++
        } else {
            calculusPartList.add(entry)
        }
        index++
    }
    return calculusPartList
}

fun List<CalculusPart>.calculateBinaryParts(precedence: BinaryOperatorPrecedence): MutableList<CalculusPart> {
    val calculusPartList = mutableListOf<CalculusPart>()
    var index = 0
    while (index < this.size) {
        val entry = this[index]
        if (entry is OperatorPart && entry.binaryOperator() && (entry.value.operatorSpec as BinaryOperatorSpec).precedence == precedence) {
            if (!calculusPartList[calculusPartList.size - 1].parentheses() && !this[index + 1].parentheses()) {
                calculusPartList[calculusPartList.size - 1] = NumberPart(
                    value = calculate(
                        (calculusPartList[calculusPartList.size - 1].value as BigDecimal).toDouble(),
                        entry.value,
                        (this[index + 1].value as BigDecimal).toDouble()
                    )
                )
                index++
            } else {
                calculusPartList.add(entry)
            }
        } else calculusPartList.add(entry)
        index++
    }
    return calculusPartList
}

fun List<CalculusPart>.calculateUnaryParts(): List<CalculusPart> {
    val calculusPartList = mutableListOf<CalculusPart>()
    var index = 0
    while (index < this.size) {
        val entry = this[index]
        if (entry is OperatorPart && entry.unaryOperator()) {
            when ((entry.value.operatorSpec as UnaryOperatorSpec).operatorPosition) {
                PRECEDE_NUMBER -> {
                    if (!this[index + 1].parentheses()) {
                        calculusPartList.add(
                            NumberPart(
                                value = calculate(
                                    (this[index + 1].value as BigDecimal).toDouble(),
                                    entry.value
                                )
                            )
                        )
                        index++
                    } else calculusPartList.add(entry)
                }
                SUCCEED_NUMBER -> {
                    if (!this[index - 1].parentheses()) {
                        calculusPartList[calculusPartList.size - 1] = NumberPart(
                            value = calculate(
                                (calculusPartList[calculusPartList.size - 1].value as BigDecimal).toDouble(),
                                entry.value
                            )
                        )
                    } else calculusPartList.add(entry)
                }
            }
        } else {
            calculusPartList.add(entry)
        }
        index++
    }

    return calculusPartList
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
