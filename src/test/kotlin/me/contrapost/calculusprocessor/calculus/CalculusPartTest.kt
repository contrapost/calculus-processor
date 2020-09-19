package me.contrapost.calculusprocessor.calculus

import org.junit.Assert.*
import org.junit.Test

class CalculusPartTest {

    private val binaryOperators = arrayOf(
            "+".toCalculus().parts[0] as OperatorPart,
            "3 - 3".toCalculus().parts[1] as OperatorPart,
            "*".toCalculus().parts[0] as OperatorPart,
            "/".toCalculus().parts[0] as OperatorPart,
            "%".toCalculus().parts[0] as OperatorPart
    )

    private val precedingUnaryOperators = arrayOf(
            "V[2]".toCalculus().parts[0] as OperatorPart,
            "log[2]".toCalculus().parts[0] as OperatorPart,
            "ln".toCalculus().parts[0] as OperatorPart
    )

    private val succeedingUnaryOperators = arrayOf(
            "^2".toCalculus().parts[0] as OperatorPart,
            "!".toCalculus().parts[0] as OperatorPart
    )

    private val openParenthesis = "(".toCalculus().parts[0] as OpenParenthesisPart
    private val closeParenthesis = ")".toCalculus().parts[0] as CloseParenthesisPart
    private val number = "4".toCalculus().parts[0] as NumberPart

    // Number part
    @Test
    fun `number part position test`() {
        // 1. can open calculus
        assertTrue(number.canBeOpeningPart())
        // 2. can close calculus
        assertTrue(number.canBeClosingPart())
        // 3. cannot succeed close parenthesis
        assertFalse(number.canSucceedCloseParenthesis())
        // 4. cannot precede open parenthesis
        assertFalse(number.canPrecedeOpenParenthesis())
        // 5. cannot succeed number
        assertFalse(number.canSucceedNumber())
        // 6. cannot precede number
        assertFalse(number.canPrecedeNumber())
        // 7.1 can succeed operator
        binaryOperators.forEach { assertTrue(number.canSucceedOperator(it)) }
        precedingUnaryOperators.forEach { assertTrue(number.canSucceedOperator(it)) }
        // 7.2 cannot succeed operator
        succeedingUnaryOperators.forEach { assertFalse(number.canSucceedOperator(it)) }
        // 8.1 can precede operator
        binaryOperators.forEach { assertTrue(number.canPrecedeOperator(it)) }
        succeedingUnaryOperators.forEach { assertTrue(number.canPrecedeOperator(it)) }
        // 8.2 cannot precede operator
        precedingUnaryOperators.forEach { assertFalse(number.canPrecedeOperator(it)) }
    }

    @Test
    fun `close parenthesis position`() {
        // 1. cannot open calculus
        assertFalse(closeParenthesis.canBeOpeningPart())
        // 2. can close calculus
        assertTrue(closeParenthesis.canBeClosingPart())
        // 3. can succeed close parenthesis
        assertTrue(closeParenthesis.canSucceedCloseParenthesis())
        // 4. cannot precede open parenthesis
        assertFalse(closeParenthesis.canPrecedeOpenParenthesis())
        // 5. can succeed number
        assertTrue(closeParenthesis.canSucceedNumber())
        // 6. cannot precede number
        assertFalse(closeParenthesis.canPrecedeNumber())
        // 7.1 can succeed operator
        succeedingUnaryOperators.forEach { assertTrue(closeParenthesis.canSucceedOperator(it)) }
        // 7.2 cannot succeed operator
        precedingUnaryOperators.forEach { assertFalse(closeParenthesis.canSucceedOperator(it)) }
        binaryOperators.forEach { assertFalse(closeParenthesis.canSucceedOperator(it)) }
        // 8.1 can precede operator
        binaryOperators.forEach { assertTrue(closeParenthesis.canPrecedeOperator(it)) }
        succeedingUnaryOperators.forEach { assertTrue(closeParenthesis.canPrecedeOperator(it)) }
        // 8.2 cannot precede operator
        precedingUnaryOperators.forEach { assertFalse(closeParenthesis.canPrecedeOperator(it)) }
    }

    @Test
    fun `open parenthesis position`() {
        // 1. can open calculus
        assertTrue(openParenthesis.canBeOpeningPart())
        // 2. cannot close calculus
        assertFalse(openParenthesis.canBeClosingPart())
        // 3. cannot succeed close parenthesis
        assertFalse(openParenthesis.canSucceedCloseParenthesis())
        // 4. can precede open parenthesis
        assertTrue(openParenthesis.canPrecedeOpenParenthesis())
        // 5. cannot succeed number
        assertFalse(openParenthesis.canSucceedNumber())
        // 6. can precede number
        assertTrue(openParenthesis.canPrecedeNumber())
        // 7.1 can succeed operator
        binaryOperators.forEach { assertTrue(openParenthesis.canSucceedOperator(it)) }
        precedingUnaryOperators.forEach { assertTrue(openParenthesis.canSucceedOperator(it)) }
        // 7.2 cannot succeed operator
        succeedingUnaryOperators.forEach { assertFalse(openParenthesis.canSucceedOperator(it)) }
        // 8.1 can precede operator
        precedingUnaryOperators.forEach { assertTrue(openParenthesis.canPrecedeOperator(it)) }
        // 8.2 cannot precede operator
        succeedingUnaryOperators.forEach { assertFalse(openParenthesis.canPrecedeOperator(it)) }
        binaryOperators.forEach { assertFalse(openParenthesis.canPrecedeOperator(it)) }
    }

    @Test
    fun `number part can precede binary operator`() {
        val numberPart = "4".toCalculus().parts[0]
        val binaryOperatorPart = "+".toCalculus().parts[0]
        assertTrue(numberPart.canPrecedeOperator(binaryOperatorPart as OperatorPart))
    }

    @Test
    fun `number part can precede unary operator`() {
        val numberPart = "4".toCalculus().parts[0]
        val exponentiation = "^2".toCalculus().parts[0]
        val factorial = "!".toCalculus().parts[0]
        assertTrue(numberPart.canPrecedeOperator(exponentiation as OperatorPart))
        assertTrue(numberPart.canPrecedeOperator(factorial as OperatorPart))
    }



    @Test
    fun `ds`() {
        val calculusPart = "log[3]".toCalculus().parts[0]
        assertTrue(calculusPart.canPrecedeNumber())
    }
}