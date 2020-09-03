package me.contrapost.calculusprocessor.calculus

import org.junit.Test
import kotlin.test.assertTrue

class CalculusConverterTest {

    @Test
    fun `empty calculus string results in calculus with one undefined part`() {
        val calculusString = "                   "
        val calc = calculusString.toCalculus()
        assertTrue { calc.parts.size == 1 }
        assertTrue { calc.parts[0] is UndefinedPart }
    }

    @Test
    fun `unknown symbol in calculus string results in calculus with one undefined part`() {
        val calculusString = "@"
        val calc = calculusString.toCalculus()
        assertTrue { calc.parts.size == 1 }
        assertTrue { calc.parts[0] is UndefinedPart }
    }

    @Test
    fun `normal complex calculus converted as expected`() {
        val calculusString = "(23 + 14 ^ 2) ^4 - V[3]8 % log[2]8!"
        val calc = calculusString.toCalculus()
        assertTrue { calc.parts.size == 14 }
        assertTrue { calc.parts.filterIsInstance(UndefinedPart::class.java).isEmpty() }
    }
}
