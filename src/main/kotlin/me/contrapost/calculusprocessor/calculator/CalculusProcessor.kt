package me.contrapost.calculusprocessor.calculator

import me.contrapost.calculusprocessor.calculator.calculus.*
import me.contrapost.calculusprocessor.calculator.operators.*
import me.contrapost.calculusprocessor.calculator.operators.UnaryOperatorPosition.PRECEDE_NUMBER
import me.contrapost.calculusprocessor.calculator.operators.UnaryOperatorPosition.SUCCEED_NUMBER
import me.contrapost.calculusprocessor.calculator.util.keepNumber
import me.contrapost.calculusprocessor.calculator.util.toCalculusString
import java.math.BigDecimal
import kotlin.collections.LinkedHashMap

class CalculusProcessor {

    private val validCalculus: MutableMap<String, CalculusData> = object : LinkedHashMap<String, CalculusData>() {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CalculusData>): Boolean = when {
            size > 1_000 -> true
            else -> false
        }
    }

    fun validate(calculusString: String): ValidationResult {
        val calculus = calculusString.toCalculus()
        val result = validate(calculus)
        if (result.valid) validCalculus.putIfAbsent(calculusString, CalculusData(calculus, null))
        return result
    }

    fun calculate(calculusString: String, detailed: Boolean): CalculusResult =
        when {
            // valid calculus with result
            calculusString in validCalculus && validCalculus[calculusString]!!.calculusResult != null -> validCalculus[calculusString]!!.calculusResult!!
            // valid calculus without result
            calculusString in validCalculus -> calculateValidCalculus(calculusString, detailed)
            // new calculus that isn't validated yet
            else -> {
                val validationResult = validate(calculusString)
                if (!validationResult.valid) throw Exception(validationResult.errors.joinToString(", "))
                calculateValidCalculus(calculusString, detailed)
            }
        }

    private fun calculateValidCalculus(calculusString: String, detailed: Boolean): CalculusResult {
        val calculusSteps = mutableListOf<CalculusStep>()
        val compiledCalculus = validCalculus[calculusString]!!.compiledCalculus
        val result = compiledCalculus.calculate(calculusSteps, detailed).parts[0].value as BigDecimal
        val calculusResult =
            CalculusResult(result, calculusSteps)
        validCalculus[calculusString] =
            CalculusData(compiledCalculus, calculusResult)
        return calculusResult
    }

    private tailrec fun Calculus.calculate(
        calculusSteps: MutableList<CalculusStep>,
        detailed: Boolean
    ): Calculus =
        if (!this.complex && this.parts.size == 1) this
        else Calculus(
            this.calculateParts(calculusSteps, detailed).removeExcessiveParenthesis()
        ).calculate(calculusSteps, detailed)

    private fun Calculus.calculateParts(
        calculusSteps: MutableList<CalculusStep>,
        detailed: Boolean
    ): List<CalculusPart> =
        this.parts.calculateUnaryParts(calculusSteps, this.hasUnaryOperators, detailed)
            .calculateBinaryParts(BinaryOperatorPrecedence.FIRST, this.hasBinaryOperatorsWithPrecedence, calculusSteps, detailed)
            .calculateBinaryParts(BinaryOperatorPrecedence.SECOND, this.hasBinaryOperatorsWithoutPrecedence, calculusSteps, detailed)

    private fun List<CalculusPart>.removeExcessiveParenthesis(): List<CalculusPart> {
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

    private fun List<CalculusPart>.calculateBinaryParts(
        precedence: BinaryOperatorPrecedence,
        hasBinaryOperators: Boolean,
        calculusSteps: MutableList<CalculusStep>,
        detailed: Boolean
    ): List<CalculusPart> {
        if (!hasBinaryOperators) return this
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
                    calculusPartList[calculusPartList.size - 1] =
                        NumberPart(
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

        if (detailed) calculusSteps.add(
            CalculusStep(
                operatorType = OperatorType.BINARY,
                precedence = precedence,
                calculusStepString = calculusPartList.toCalculusString()
            )
        )
        return calculusPartList
    }

    private fun List<CalculusPart>.calculateUnaryParts(
        calculusSteps: MutableList<CalculusStep>,
        hasUnaryOperators: Boolean,
        detailed: Boolean
    ): List<CalculusPart> {
        if (!hasUnaryOperators) return this
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
                            calculusPartList[calculusPartList.size - 1] =
                                NumberPart(
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

        if (detailed) calculusSteps.add(
            CalculusStep(
                operatorType = OperatorType.UNARY,
                calculusStepString = calculusPartList.toCalculusString()
            )
        )
        return calculusPartList
    }

    private fun calculate(firstNumber: Double, operator: Operator, secondNumber: Double? = null): BigDecimal =
        operator
            .operatorSpec
            .calculation
            .invoke(firstNumber, processedSecondNumber(operator, secondNumber))

    private fun processedSecondNumber(operator: Operator, secondNumber: Double?) = when {
        operator.operatorSpec.operatorWithBase() -> operator.operatorInput.keepNumber()
        else -> secondNumber
    }
}

data class CalculusData(val compiledCalculus: Calculus, val calculusResult: CalculusResult?)

data class CalculusResult(val result: BigDecimal, val calculationSteps: List<CalculusStep>)

data class CalculusStep(
    val operatorType: OperatorType,
    val precedence: BinaryOperatorPrecedence? = null,
    val calculusStepString: String
) {
    fun binaryStepWithPrecedence() = precedence != null && precedence == BinaryOperatorPrecedence.FIRST
}