package me.contrapost.calculusprocessor

import me.contrapost.calculusprocessor.calculus.toCalculus
import me.contrapost.calculusprocessor.operators.BinaryOperatorPrecedence
import me.contrapost.calculusprocessor.operators.OperatorType
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class CalculusProcessorTest {

    @Test
    fun `basic calculation with two integers and one binary operator`() {
        val processor = CalculusProcessor()
        val result = processor.calculate("2 + 2", detailed = true)
        assertEquals(
            result,
            CalculusResult(
                BigDecimal(4),
                listOf(
                    CalculusStep(
                        operatorType = OperatorType.BINARY,
                        precedence = BinaryOperatorPrecedence.SECOND,
                        calculusStepString = "4"
                    )
                )
            )
        )
    }

    @Test
    fun `complex calculation with doubles and operator of different type`() {
        val processor = CalculusProcessor()
        val result = processor.calculate("2.1^4 + (log[2]4 - (V[5]32 + 4.6))^2", detailed = true)
        assertEquals(
            result,
            CalculusResult(
                BigDecimal("40.60810000000000030695446184836328029632568359375"),
                listOf(
                    CalculusStep(
                        operatorType = OperatorType.UNARY,
                        precedence = null,
                        calculusStepString = "19.44810000000000371755959349684417247772216796875 + ( 2 - ( 2 + 4.6 ) ) ^2"
                    ),
                    CalculusStep(
                        operatorType = OperatorType.BINARY,
                        precedence = BinaryOperatorPrecedence.SECOND,
                        calculusStepString = "19.44810000000000371755959349684417247772216796875 + ( 2 - ( 6.5999999999999996447286321199499070644378662109375 ) ) ^2"
                    ),
                    CalculusStep(
                        operatorType = OperatorType.UNARY,
                        precedence = null,
                        calculusStepString = "19.44810000000000371755959349684417247772216796875 + ( 2 - 6.5999999999999996447286321199499070644378662109375 ) ^2"
                    ),
                    CalculusStep(
                        operatorType = OperatorType.BINARY,
                        precedence = BinaryOperatorPrecedence.SECOND,
                        calculusStepString = "19.44810000000000371755959349684417247772216796875 + ( -4.5999999999999996447286321199499070644378662109375 ) ^2"
                    ),
                    CalculusStep(
                        operatorType = OperatorType.UNARY,
                        precedence = null,
                        calculusStepString = "19.44810000000000371755959349684417247772216796875 + 21.159999999999996589394868351519107818603515625"
                    ),
                    CalculusStep(
                        operatorType = OperatorType.BINARY,
                        precedence = BinaryOperatorPrecedence.SECOND,
                        calculusStepString = "40.60810000000000030695446184836328029632568359375"
                    )
                )
            )
        )
    }
}
