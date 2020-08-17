package me.contrapost.calculusprocessor

import me.contrapost.calculusprocessor.calculus.toCalculus
import org.junit.Test

class CalculusProcessorTest {

    @Test
    fun xx() {
        val calculus = "235.86+234+39".toCalculus()
        println(calculus.hasBinaryOperatorsWithoutPrecedence)
    }

    @Test
    fun x() {
        val calculator = CalculusProcessor()
        val validation = calculator.validate("235.86+234+39")
        println(validation)
        val result = calculator.calculate("235.86+234+39", true)
        println(result)
    }
}