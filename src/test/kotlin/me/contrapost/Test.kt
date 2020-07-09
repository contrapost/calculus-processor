package me.contrapost

import me.contrapost.fantasticcal.util.DOUBLE_OR_INT_REGEX
import me.contrapost.fantasticcal.util.removeWhitespaces
import org.junit.Test
import kotlin.test.assertEquals

class Test {

}

fun main() {
    val x = "- 102".removeWhitespaces()
    val y = "-.98 - -555".removeWhitespaces()
    val regex = "$DOUBLE_OR_INT_REGEX-$DOUBLE_OR_INT_REGEX".toRegex()
    val regex2 = "(-)?$DOUBLE_OR_INT_REGEX".toRegex()
    val regex3 = "(?<=(-)?$DOUBLE_OR_INT_REGEX)-(?=(-)?$DOUBLE_OR_INT_REGEX)".toRegex()
    //val regex3 = "^$DOUBLE_OR_INT_REGEX(?:-$DOUBLE_OR_INT_REGEX)".toRegex()
    /*println(x.matches(regex))
    println(x.matches(regex2))*/

    println(x.matches(regex3))
    println(y.matches(regex3))

    regex3.findAll(y).forEach {
        println("${it.range} ${it.value}")
    }
}