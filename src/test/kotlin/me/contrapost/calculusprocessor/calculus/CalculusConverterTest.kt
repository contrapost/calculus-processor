package me.contrapost.calculusprocessor.calculus

import org.junit.Test
import kotlin.test.assertTrue

class CalculusConverterTest {

    @Test
    fun `empty calculus string results in calculus with one undefined part`() {
        val calculusString = "                   +"
        val calc = calculusString.toCalculus()
        assertTrue { calc.parts.size == 1 }
        assertTrue { calc.parts[0] is UndefinedPart }
    }
}