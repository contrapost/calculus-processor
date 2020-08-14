package me.contrapost.fantasticcal.ui

import me.contrapost.fantasticcal.calculator.calculate
import me.contrapost.fantasticcal.calculator.calculus.Calculus
import me.contrapost.fantasticcal.calculator.calculus.toCalculus
import me.contrapost.fantasticcal.calculator.calculus.validate
import me.contrapost.fantasticcal.calculator.operators.operatorsWithDescriptions
import java.util.*
import kotlin.system.exitProcess

fun showIntro() {
    println(title("0.2.0"))
    println(greetings())
    println(instructions())
}

fun performCalculation() {
    var stop = false
    val detailed = yesNoPrompt("Do you want to see step by step calculation?")
    while (!stop) {
        val validCalculus = getValidCalculus()
        val calculationResult = calculate(validCalculus, detailed)
        println("Result: $calculationResult")
        stop = yesNoPrompt("Do you want to continue calculating?")
    }
}

private fun getValidCalculus(): Calculus {
    print("""
        Insert calculus (you can type 'stop' to exit).
        > 
    """.trimIndent())
    var calculusString = readLine().toCheckedInput().removeWhitespaces()
    var stop = calculusString.toLowerCase() == "stop"
    if (stop) stopProgram()
    var calculus = calculusString.toCalculus()
    var validationResult = validate(calculus)
    while (!stop && !validationResult.valid) {
        print("""
            Calculus is invalid: ${validationResult.errors}. Please compose a new calculus or type 'stop' to exit.
            > 
        """.trimIndent())
        calculusString = readLine().toCheckedInput().removeWhitespaces()

        when {
            calculusString.toLowerCase() == "stop" -> stop = true
            else -> {
                calculus = calculusString.toCalculus()
                validationResult = validate(calculus)
            }
        }
    }

    return when {
        stop -> stopProgram()
        else -> calculus
    }
}

private fun yesNoPrompt(prompt: String): Boolean {
    print("$prompt (Y/N): ")
    var answer = readLine()
    while (answer == null || (answer.toUpperCase() != "Y" && answer.toUpperCase() != "N")) {
        print("I didn't understand you. Please, type 'Y' or 'N' for 'Yes' and 'No' (Y/N): ")
        answer = readLine()
    }

    return when (answer.toUpperCase()) {
        "N" -> true
        else -> false
    }
}

private fun instructions(): String {
    val instructionsTextBeginning = "Let's do some Math! This calculator can do following operations:"
    val operators = operatorsWithDescriptions.map { "${it.key} -> ${it.value}" }.toList().joinToString("\n")
    val instructionsTextEnding = """
        Please, if your number is decimal, use '.'
        Calculator can perform complex calculations with multiple operations (precedence can be set by using parenthesis).
    """.trimIndent()

    return "$instructionsTextBeginning\n $operators \n$instructionsTextEnding"
}

private fun title(version: String = "unknown"): String {
    return """
                    FANTASTIC CALCULATOR
                        (or just FanC)
        Welcome to the FANTASTIC CALCULATOR version $version!
    """.trimIndent()
}

private fun greetings(): String {
    val timeInfo = timeInfo()
    print("Please, stay calm and print your name: ")
    val name = readLine()
    val verifiedName = when {
        name.isNullOrBlank() -> "- whatever your name is"
        else -> name
    }
    val greetingText = "Good ${timeInfo.dayTime}"
    return "$greetingText, $verifiedName! You are in zone: ${timeInfo.zone}"
}

private fun timeInfo(): TimeInfo {
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val timeZone = calendar.timeZone.id
    val dayTime = when (hourOfDay) {
        in 0..5 -> "night"
        in 6..12 -> "morning"
        in 12..18 -> "day"
        else -> "evening"
    }
    return TimeInfo(dayTime = dayTime, zone = timeZone)
}

class TimeInfo(val dayTime: String, val zone: String)

private fun String?.toCheckedInput(): String = when (this) {
    null -> {
        println("Something went wrong with I/O. Terminating the program!")
        exitProcess(1)
    }
    else -> this
}

private fun stopProgram(): Nothing = exitProcess(0)

fun String.removeWhitespaces() = this.replace(" ", "")