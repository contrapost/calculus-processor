package me.contrapost.fantasticcal.calculus

import me.contrapost.fantasticcal.calculator.calculus.toCalculus
import me.contrapost.fantasticcal.calculator.calculus.validate
import me.contrapost.fantasticcal.ui.removeWhitespaces
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculusValidatorTest {

    @Test
    fun `case 1 - calculus with undefined parts`() {
        val calculus = "2 + 45 - 32xyz".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
        assertTrue { validationResult.errors[0].contains("xyz") }
    }

    @Test
    fun `case 2 calculus with only one part`() {
        val calculus = "+".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
        assertTrue { validationResult.errors[0].contains("at least one operator and one number") }
    }

    // Parentheses

    @Test
    fun `case 3 correct number of parentheses in correct order`() {
        val calculus = "(9 + 3 * log[4]4) * (V[2]9 - 27)".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertTrue { validationResult.valid }
    }

    @Test
    fun `case 3_1 first parenthesis is closing`() {
        val calculus = "9 + )3log[4]4) * 27".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 3_2 not equal number of left and right parentheses`() {
        val calculus = "(9 + 3log[4]4) * 27)".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    // validation of the parts' position in the calculus

    @Test
    fun `case 5_1 calculus starts with invalid part`() {
        val calculus = "+2 - 67 * log[2]4".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_2 calculus ends with invalid part (+)`() {
        val calculus = "+2 - 67 * log[2]4 +".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_3 calculus ends with invalid part (close parenthesis)`() {
        val calculus = "2 - 67 * log[2]4(".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_4 valid calculus`() {
        val calculus = "2 - 67 * log[2]4 + (5 - V[2]4) - 5^3 - 2".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertTrue { validationResult.valid }
    }

    @Test
    fun `case 5_5 valid calculus`() {
        val calculus = "2 - 67 * log[2]4 + 4^7".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertTrue { validationResult.valid }
    }

    @Test
    fun `case 5_6 number precedes open parenthesis`() {
        val calculus = "2(67 * log[2]4 + 4^7)".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_7 binary operator succeeds open parenthesis`() {
        val calculus = "2 + (+67 * log[2]4 + 4^7)".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_8 unary operator succeeds close parenthesis`() {
        val calculus = "2 + (67 * log[2]4 + 4^7)log[3]4 + 5".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_9 binary operator precedes close parenthesis`() {
        val calculus = "2 + (67 * log[2]4 + 4^7 +) + 5".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_10 binary operator succeeds open parenthesis`() {
        val calculus = "2 + (67 * log[2]4( + 4^7) + 5)".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertFalse { validationResult.valid }
    }

    @Test
    fun `case 5_11 unary operator succeeds close parenthesis`() {
        val  calculus = "34 * (23 - 12) ^3 - 2".removeWhitespaces()
        val validationResult =
            validate(calculus.toCalculus())
        assertTrue { validationResult.valid }
    }
}
