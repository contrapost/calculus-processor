package me.contrapost

import me.contrapost.fantasticcal.calculator.calculate
import me.contrapost.fantasticcal.calculus.CalculusPart
import me.contrapost.fantasticcal.calculus.toCalculusParts
import me.contrapost.fantasticcal.calculus.validate
import me.contrapost.fantasticcal.ui.stopProgram
import me.contrapost.fantasticcal.ui.toCheckedInput
import me.contrapost.fantasticcal.util.removeWhitespaces

fun main() {
    val validCalculusPartList = getValidCalculus()
    validCalculusPartList.forEach { println(it) }

    val calculationResult = calculate(validCalculusPartList)
    println("Result: $calculationResult")
    //test()
}

fun getValidCalculus(): List<CalculusPart> {
    print("""
        Insert calculus (you can type 'stop' to exit).
        > 
    """.trimIndent())
    var calculus = readLine().toCheckedInput().removeWhitespaces()
    var stop = calculus.toLowerCase() == "stop"
    if (stop) stopProgram()
    var calculusList = calculus.toCalculusParts()
    var validationResult = validate(calculusList)
    while (!stop && !validationResult.valid) {
        print("""
            Calculus is invalid: ${validationResult.errors}. Please compose a new calculus or type 'stop' to exit.
            > 
        """.trimIndent())
        calculus = readLine().toCheckedInput().removeWhitespaces()

        when {
            calculus.toLowerCase() == "stop" -> stop = true
            else -> {
                calculusList = calculus.toCalculusParts()
                validationResult = validate(calculusList)
            }
        }
    }

    return when {
        stop -> stopProgram()
        else -> calculusList
    }
}

fun test() {
    print("""
        Insert calculus (you can type 'stop' to exit).
        > 
    """.trimIndent())
    readLine().toCheckedInput().removeWhitespaces().toCalculusParts().forEach {
        println(it)
    }
}
