package me.contrapost.calculusprocessor.calculus

import me.contrapost.calculusprocessor.operators.BinaryOperatorSpec
import me.contrapost.calculusprocessor.operators.UnaryOperatorSpec
import org.junit.Test
import java.math.BigDecimal
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
    fun `unknown symbol in calculus string results in calculus with undefined part`() {
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

        // parts
        assertTrue { calc.parts[0].parentheses() }
        assertTrue { (calc.parts[1] as NumberPart).value == BigDecimal.valueOf(23) }
        assertTrue { (calc.parts[2] as OperatorPart).value.operatorInput == "+" }
        assertTrue { (calc.parts[2] as OperatorPart).value.operatorSpec == BinaryOperatorSpec.ADDITION }
        assertTrue { (calc.parts[3] as NumberPart).value == BigDecimal.valueOf(14) }
        assertTrue { (calc.parts[4] as OperatorPart).value.operatorInput == "^2" }
        assertTrue { (calc.parts[4] as OperatorPart).value.operatorSpec == UnaryOperatorSpec.EXPONENTIATION }
        assertTrue { calc.parts[5].parentheses() }
        assertTrue { (calc.parts[6] as OperatorPart).value.operatorInput == "^4" }
        assertTrue { (calc.parts[6] as OperatorPart).value.operatorSpec == UnaryOperatorSpec.EXPONENTIATION }
        assertTrue { (calc.parts[7] as OperatorPart).value.operatorInput == "-" }
        assertTrue { (calc.parts[7] as OperatorPart).value.operatorSpec == BinaryOperatorSpec.SUBTRACTION }
        assertTrue { (calc.parts[8] as OperatorPart).value.operatorInput == "V[3]" }
        assertTrue { (calc.parts[8] as OperatorPart).value.operatorSpec == UnaryOperatorSpec.ROOT }
        assertTrue { (calc.parts[9] as NumberPart).value == BigDecimal.valueOf(8) }
        assertTrue { (calc.parts[10] as OperatorPart).value.operatorInput == "%" }
        assertTrue { (calc.parts[10] as OperatorPart).value.operatorSpec == BinaryOperatorSpec.MODULUS }
        assertTrue { (calc.parts[11] as OperatorPart).value.operatorInput == "log[2]" }
        assertTrue { (calc.parts[11] as OperatorPart).value.operatorSpec == UnaryOperatorSpec.LOGARITHM }
        assertTrue { (calc.parts[12] as NumberPart).value == BigDecimal.valueOf(8) }
        assertTrue { (calc.parts[13] as OperatorPart).value.operatorInput == "!" }
        assertTrue { (calc.parts[13] as OperatorPart).value.operatorSpec == UnaryOperatorSpec.FACTORIAL }
    }
}
