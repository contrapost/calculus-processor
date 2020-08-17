package me.contrapost.calculusprocessor.calculus

import me.contrapost.calculusprocessor.operators.BinaryOperatorSpec
import me.contrapost.calculusprocessor.operators.Operator
import me.contrapost.calculusprocessor.operators.UnaryOperatorPosition
import me.contrapost.calculusprocessor.operators.UnaryOperatorSpec
import java.math.BigDecimal

abstract class CalculusPart {
    abstract val type: String
    abstract val value: Any

    fun canSucceedOperator(operatorPart: OperatorPart): Boolean =
        when (val operatorSpec = operatorPart.value.operatorSpec) {
            is BinaryOperatorSpec ->
                // + 4                // + (                         // + log[2]
                this is NumberPart || this is OpenParenthesisPart || this is OperatorPart && precedeNumber()
            is UnaryOperatorSpec -> {
                (this is NumberPart && operatorSpec.precedeNumber())                                    // log[4]4
                        || (this is OperatorPart && binaryOperator() && operatorSpec.succeedNumber())   // ^2 +
                        || (this is CloseParenthesisPart && operatorSpec.succeedNumber())               // ^2)
                        || (this is OpenParenthesisPart && operatorPart.precedeNumber())                // log[4](
            }
            else -> throw UnsupportedOperationException()
        }

    fun canPrecedeOperator(operatorPart: OperatorPart) = when (val operatorSpec = operatorPart.value.operatorSpec) {
        is BinaryOperatorSpec ->
            // 4 +                // ) +                           // 4! +
            this is NumberPart || this is CloseParenthesisPart || this is OperatorPart && succeedNumber()
        is UnaryOperatorSpec -> {
            (operatorSpec.precedeNumber() && this is OperatorPart && binaryOperator())  // + log[4]
                    || (operatorSpec.precedeNumber() && this is OpenParenthesisPart)    // (log[4]
                    || (operatorSpec.succeedNumber() && this is NumberPart)             // 4^2
                    || (operatorSpec.succeedNumber() && this is CloseParenthesisPart)   // )^2
        }
        else -> throw UnsupportedOperationException()
    }

    fun canSucceedNumber() =
        // 4)                                                   // 4 +               // 4!
        this is CloseParenthesisPart || this is OperatorPart && (binaryOperator() || succeedNumber())

    fun canPrecedeNumber() =
        // (4                                                  // + 4               // log[4]4
        this is OpenParenthesisPart || this is OperatorPart && (binaryOperator() || precedeNumber())

    fun canBeOpeningPart() =
        // (calculus                   // 4calculus          // log[4]calculus
        this is OpenParenthesisPart || this is NumberPart || (this is OperatorPart && precedeNumber())

    fun canBeClosingPart() =
        // calculus)                    calculus4             // calculus^2
        this is CloseParenthesisPart || this is NumberPart || (this is OperatorPart && succeedNumber())

    fun canPrecedeOpenParenthesis() =
        // ((                                                   // + (               // log[4](
        this is OpenParenthesisPart || (this is OperatorPart && (binaryOperator() || precedeNumber()))

    fun canSucceedCloseParenthesis() =
        // ))                                                    // ) +               // )^2
        this is CloseParenthesisPart || (this is OperatorPart && (binaryOperator() || succeedNumber()))

    fun parentheses() = this is ParenthesisPart

    fun toShortDescription(): String = when(this) {
        is OperatorPart -> "$type with value ${value.operatorInput}"
        is NumberPart, is UndefinedPart ->  "$type with value $value"
        is OpenParenthesisPart, is CloseParenthesisPart -> type
        else -> throw UnsupportedClassVersionError("") // TODO
    }
}

data class UndefinedPart(override val value: String, override val type: String = "undefined part") : CalculusPart()

data class OperatorPart(override val value: Operator, override val type: String) : CalculusPart() {
    fun precedeNumber() = value.operatorSpec is UnaryOperatorSpec &&
            value.operatorSpec.operatorPosition == UnaryOperatorPosition.PRECEDE_NUMBER

    fun succeedNumber() = value.operatorSpec is UnaryOperatorSpec &&
            value.operatorSpec.operatorPosition == UnaryOperatorPosition.SUCCEED_NUMBER

    fun binaryOperator() = value.operatorSpec is BinaryOperatorSpec

    fun unaryOperator() = value.operatorSpec is UnaryOperatorSpec
}

data class NumberPart(override val value: BigDecimal, override val type: String = "number") : CalculusPart()

abstract class ParenthesisPart : CalculusPart()

data class OpenParenthesisPart(
    override val value: String = "(",
    override val type: String = "open parenthesis"
) : ParenthesisPart()

data class CloseParenthesisPart(
    override val value: String = ")",
    override val type: String = "close parenthesis"
) : ParenthesisPart()
