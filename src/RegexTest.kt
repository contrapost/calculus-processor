import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

fun main() {
    /*val string = "**[2.03]"
    println(string.replace("[^0-9]".toRegex(), "").toDouble())*/

    /*val regex = "\\^\\[\\d+\\]".toRegex()
    val operator = "^[9]"
    val operator2 = "^[100]"
    val operator3 = "^[-2]"
    println(operator.matches(regex))
    println(operator2.matches(regex))
    println(operator3.matches(regex))*/

    /*val bd = 0.23.toBigDecimal().setScale(3, RoundingMode.DOWN)
    println(bd.toString())
    println(BigDecimal.valueOf(bd.toString().toDouble()))*/

    val bds = listOf(BigDecimal(1.250).setScale(3), BigDecimal(1.250).setScale(3), BigDecimal(1.250).setScale(3), BigDecimal(1.250).setScale(3))
    println(bds.reduce { a, b -> a!! + b!! })
}
