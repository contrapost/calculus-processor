package me.contrapost.calculusprocessor.calculus

import org.junit.Assert.*
import org.junit.Test

class CalculusTest {

    @Test
    fun `calculus with parenthesis is complex`() {
        val notComplexCalculus = "2 + 3"
        val complexCalculus = "2 + (3 - 12)"
        assertFalse(notComplexCalculus.toCalculus().complex)
        assertTrue(complexCalculus.toCalculus().complex)
    }

    @Test
    fun `calculus with unary operators`() {
        val calculus = "2 + 3!"
        assertTrue(calculus.toCalculus().hasUnaryOperators)
    }

    @Test
    fun `calculus with binary operators without precedence`() {
        val calculus = "2 + 3!"
        assertTrue(calculus.toCalculus().hasBinaryOperatorsWithoutPrecedence)
    }

    @Test
    fun `calculus with binary operators with precedence`() {
        val calculus = "2 * 3!"
        assertTrue(calculus.toCalculus().hasBinaryOperatorsWithPrecedence)
    }
}