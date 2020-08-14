package me.contrapost.fantasticcal.calculator.operations

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round

fun nthRoot(num: Double, index: Double): BigDecimal {
    val temporaryResult = Math.E.pow(ln(num) / index)
    val rounded = round(temporaryResult)
    return BigDecimal(when {
        abs(rounded - temporaryResult) < 0.00000000000002 -> rounded
        else -> temporaryResult
    })
}

fun factorial(number: Double): BigInteger {
    var result = BigInteger.ONE
    (2..number.toInt()).forEach { result = result.multiply(BigInteger.valueOf(it.toLong())) }
    return result
}