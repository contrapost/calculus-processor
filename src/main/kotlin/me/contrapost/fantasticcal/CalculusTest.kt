package me.contrapost.fantasticcal

import me.contrapost.fantasticcal.calculus.toCalculusParts
import me.contrapost.fantasticcal.operators.Operator
import java.math.BigDecimal

fun main() {
    print("Insert calculus: ")
    val calculus = readLine()
    val list = calculus!!.replace(" ", "").toCalculusParts()
    list.forEach { println(it) }
}

abstract class CalculusPart {
    abstract val value: Any
}
data class UndefinedPart(override val value: String) : CalculusPart()
data class OperatorPart(override val value: Operator) : CalculusPart()
data class NumberPart(override val value: BigDecimal) : CalculusPart()
data class LeftParenthesisPart(override val value: String = "(") : CalculusPart()
data class RightParenthesisPart(override val value: String = ")") : CalculusPart()