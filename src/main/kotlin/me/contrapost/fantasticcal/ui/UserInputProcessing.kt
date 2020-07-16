package me.contrapost.fantasticcal.ui

import me.contrapost.fantasticcal.calculator.calculate
import me.contrapost.fantasticcal.operators.BinaryOperatorSpec
import me.contrapost.fantasticcal.operators.Operator
import me.contrapost.fantasticcal.operators.operatorsWithDescriptions
import me.contrapost.fantasticcal.operators.validOperator
import java.util.*
import kotlin.system.exitProcess

fun showIntro() {
    println(title("0.1.0"))
    println(greetings())
    println(instructions())
}

fun performCalculation() {
    var shouldBeStopped = false
    while (!shouldBeStopped) {

        val firstNumber = getNumberInput("Print your first number here -> |")
        val operator = getOperatorInput()

        val result = when (operator.operatorSpec) {
            is BinaryOperatorSpec -> {
                val secondNumber = getNumberInput("Print your second number here -> |")
                calculate(firstNumber, operator, secondNumber)
            }
            else -> calculate(firstNumber, operator)
        }

        println("Your result is -> ${result.toPlainString()}")

        shouldBeStopped = exitPrompt()
    }
}

fun getOperatorInput(): Operator {
    val operatorsAsString = operatorsWithDescriptions.keys.joinToString(" ")
    print("Choose one of this operators: $operatorsAsString -> |")
    var operatorInput = readLine()
    var operator = validOperator(operatorInput)
    while (operator == null) {
        print("Sadly, this operator is unsupported by FanC! Did you choose one of given operators? Try some of them again! Choose one of this operators: $operatorsAsString -> |")
        operatorInput = readLine()
        operator = validOperator(operatorInput)
    }
    return operator
}

fun getNumberInput(prompt: String): Double {
    print(prompt)
    var numberAsString = readLine()
    while (numberAsString == null || numberAsString.toDoubleOrNull() == null) {
        print(
            "Are you sure, that you printed a number? Try again! " +
                    "Valid number should follow pattern: X or X.X where X is a digit (i.e. 4 or -5.7) -> |"
        )
        numberAsString = readLine()
    }
    return numberAsString.toDouble()
}

fun exitPrompt(): Boolean {
    print("Do you want to continue calculating? (Y/N): ")
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

fun instructions(): String {
    val instructionsTextBeginning = "Let's do some Math! This calculator can do following operations:"
    val operators = operatorsWithDescriptions.map { "${it.key} -> ${it.value}" }.toList().joinToString("\n")
    val instructionsTextEnding = "Please, if your number is decimal, use '.'"

    return "$instructionsTextBeginning\n $operators \n$instructionsTextEnding"
}

fun title(version: String = "unknown"): String {
    return """
                    FANTASTIC CALCULATOR
                        (or just FanC)
        Welcome to the FANTASTIC CALCULATOR version $version!
    """.trimIndent()
}

fun greetings(): String {
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

fun timeInfo(): TimeInfo {
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

fun String?.toCheckedInput(): String = when (this) {
    null -> {
        println("Something went wrong with I/O. Terminating the program!")
        exitProcess(1)
    }
    else -> this
}

fun stopProgram(): Nothing = exitProcess(0)