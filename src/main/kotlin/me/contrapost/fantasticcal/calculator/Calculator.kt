package me.contrapost.fantasticcal.calculator

import me.contrapost.fantasticcal.calculus.Calculus
import me.contrapost.fantasticcal.calculus.CalculusPart
import me.contrapost.fantasticcal.calculus.NumberPart
import me.contrapost.fantasticcal.calculus.OperatorPart
import me.contrapost.fantasticcal.operators.BinaryOperatorPrecedence
import me.contrapost.fantasticcal.operators.BinaryOperatorSpec
import me.contrapost.fantasticcal.operators.Operator
import me.contrapost.fantasticcal.operators.UnaryOperatorPosition.PRECEDE_NUMBER
import me.contrapost.fantasticcal.operators.UnaryOperatorPosition.SUCCEED_NUMBER
import me.contrapost.fantasticcal.operators.UnaryOperatorSpec
import me.contrapost.fantasticcal.util.keepNumber
import me.contrapost.fantasticcal.util.toCalculusString
import java.math.BigDecimal

fun calculate(calculus: Calculus, detailed: Boolean) = calculus.calculateParts(detailed).parts[0].value as BigDecimal

tailrec fun Calculus.calculateParts(detailed: Boolean): Calculus =
    if (!this.complex && this.parts.size == 1) this
    else Calculus(this.parts.calculateParts().removeExcessiveParenthesis()).calculateParts(detailed)

fun List<CalculusPart>.calculateParts(): List<CalculusPart> =
    this.calculateUnaryParts()
        .calculateBinaryParts(BinaryOperatorPrecedence.FIRST)
        .calculateBinaryParts(BinaryOperatorPrecedence.SECOND)

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
            if (
                (calculusPartList[calculusPartList.size - 1] is NumberPart
                        && (calculusPartList.getOrNull(calculusPartList.size - 2) == null
                        || calculusPartList[calculusPartList.size - 2] !is OperatorPart
                        || (calculusPartList[calculusPartList.size - 2] as OperatorPart).value.operatorSpec !is BinaryOperatorSpec
                        || ((calculusPartList[calculusPartList.size - 2] as OperatorPart).value.operatorSpec as BinaryOperatorSpec).precedence.precedence >= precedence.precedence
                        )
                        )
                &&
                (this[index + 1] is NumberPart
                        && (this.getOrNull(index + 2) == null
                        || this[index + 2] !is OperatorPart
                        || (this[index + 2] as OperatorPart).value.operatorSpec !is BinaryOperatorSpec
                        || ((this[index + 2] as OperatorPart).value.operatorSpec as BinaryOperatorSpec).precedence.precedence >= precedence.precedence
                        )
                        )
            ) {
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
    println("BINARY " + calculusPartList.toCalculusString())
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

    println("UNARY: " + calculusPartList.toCalculusString())
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
