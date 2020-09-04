package me.contrapost.calculusprocessor

import me.contrapost.calculusprocessor.calculus.toCalculus
import org.junit.Test

class CalculusProcessorTest {

    @Test
    fun `calculate case 1`() {
        val calculus = "235.86+234+39".toCalculus()
        println(calculus.hasBinaryOperatorsWithoutPrecedence)
    }
}
