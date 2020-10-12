package me.contrapost.calculusprocessor.operations

import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals

class OperationImplementationsTest {

    @Test
    fun `square root of 4 is 2`() {
        val result = nthRoot(4.0, 2.0)
        assertEquals(result, BigDecimal(2))
    }

    @Test
    fun `root with index 3 of 8 is 2`() {
        val result = nthRoot(8.0, 3.0)
        assertEquals(result, BigDecimal(2))
    }

    @Test
    fun `factorial of 5 is 120`() {
        val result = factorial(5.0)
        assertEquals(result, BigInteger.valueOf(120))
    }
}
