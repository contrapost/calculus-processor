package me.contrapost.fantasticcal.ui

import me.contrapost.fantasticcal.calculator.Calculator
import me.contrapost.fantasticcal.calculator.CalculusStep
import me.contrapost.fantasticcal.calculator.operators.operatorsWithDescriptions
import java.util.*
import kotlin.system.exitProcess

val calculator = Calculator()

fun showIntro() {
    println(title("0.2.0"))
    println(greetings())
    println(instructions())
}

fun performCalculation() {
    var continueCalculating = true
    val detailed = yesNoPrompt("Do you want to see step by step calculation?")
    while (continueCalculating) {
        val validCalculus = getValidCalculus(calculator)
        val calculationResult = calculator.calculate(validCalculus, detailed)
        println("Result: ${calculationResult.result}")
        if (detailed) {
            println("Calculation steps:")
            calculationResult.calculationSteps.toStringList().forEach { println(it) }
        }
        continueCalculating = yesNoPrompt("Do you want to continue calculating?")
    }
}

private fun getValidCalculus(calculator: Calculator): String {
    print("""
        Insert calculus (you can type 'stop' to exit).
        > 
    """.trimIndent())
    var calculusString = readLine().toCheckedInput()
    var stop = calculusString.toLowerCase() == "stop"
    if (stop) stopProgram()
    var validationResult = calculator.validate(calculusString)
    while (!stop && !validationResult.valid) {
        print("""
            Calculus is invalid: ${validationResult.errors}. Please compose a new calculus or type 'stop' to exit.
            > 
        """.trimIndent())
        calculusString = readLine().toCheckedInput()

        when {
            calculusString.toLowerCase() == "stop" -> stop = true
            else -> {
                validationResult = calculator.validate(calculusString)
            }
        }
    }

    return when {
        stop -> stopProgram()
        else -> calculusString
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
        "N" -> false
        else -> true
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

private fun List<CalculusStep>.toStringList(): List<String> = this.mapIndexed {
        index, calculusStep ->
    "step ${index + 1}: ${calculusStep.calculusStepString}, " +
            "[calculated ${calculusStep.operatorType} operators" +
            "${if (calculusStep.binaryStepWithPrecedence()) " with precedence" else ""}]"
}