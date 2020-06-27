import CalculusPartType.*
import java.lang.IllegalArgumentException

fun main() {
    /*print("Insert calculus: ")
    val calculus = readLine()
    val list = mutableListOf<String>()
    println(calculus.toOperationList(list))*/

    val list = mutableListOf<CalculusPart>()

    //val calc = "- 3.097 +. 3255. 43 * 11 - V[3]  67n./434   8+log[2]5.0 / .8"
    //val calcAsList = "- 3.097 +. 3255. 43 * 11 - V[3]  67n./434   8+log[2]5.0 / .8".replace(" ", "").toList()
    val calc = "- (3.097 + 3255 43 * 11 - V[3]  67/434)  - 8+log[2]5.0 / .8 + 5^2"
    val calcAsList = calc.replace(" ", "").toList()

    var validationResult = calc
    operatorsWithBase.forEach {
        validationResult = validationResult.replace(it, "")
    }
    validationResult = validationResult.replace(DOUBLE_OR_INT_REGEX.toRegex(), "")
    validationResult = validationResult.replace("(", "").replace(")", "")
    validationResult = validationResult.replace(" ", "")
    operatorsWithoutBase.forEach {
        validationResult = validationResult.replace(it, "")
    }

    if (validationResult.isNotEmpty()) throw IllegalArgumentException("Calculus contains illegal chars ${validationResult.toList()}")
    var isDigit = calcAsList[0].isDigit()
    var currentListIndex = 0
    list.add(
            CalculusPart(
                    when {
                        calcAsList[0] == '(' -> LEFT_PARENTHESIS
                        calcAsList[0] == ')' -> throw IllegalArgumentException("Calculus cannot start with ')'")
                        isDigit -> NUMBER
                        else -> OPERATOR
                    },
                    calcAsList[currentListIndex].toString())
    )
    var currentCalculusPartType = list[0].type
    calcAsList.forEachIndexed { i, ch ->
        if (i == 0) return@forEachIndexed
        when {
            ch in listOf('(', ')') -> {
                list.add(
                        CalculusPart(
                                type = when (ch) {
                                    '(' -> LEFT_PARENTHESIS
                                    else -> RIGHT_PARENTHESIS
                                },
                                calculusPart = ch.toString())
                )
                isDigit = false
                currentCalculusPartType = when (ch) {
                    '(' -> LEFT_PARENTHESIS
                    else -> RIGHT_PARENTHESIS
                }
                currentListIndex++
            }
            isDigit -> {
                if (ch.isDigit() == isDigit || (isDigit && ch == '.')) {
                    list[currentListIndex] = CalculusPart(list[currentListIndex].type, list[currentListIndex].calculusPart + ch)
                } else {
                    isDigit = !isDigit
                    list.add(CalculusPart(if (isDigit) NUMBER else OPERATOR, calculusPart = ch.toString()))
                    currentListIndex++
                }
            }
            else -> {
                if (ch.isDigit() == isDigit && ch != '.' && ch !in listOf('(', ')')) {
                    list[currentListIndex] = CalculusPart(list[currentListIndex].type, list[currentListIndex].calculusPart + ch)
                } else {
                    isDigit = !isDigit
                    list.add(CalculusPart(if (isDigit) NUMBER else OPERATOR, calculusPart = ch.toString()))
                    currentListIndex++
                }
            }
        }
    }

    list.forEach { println(it) }

    list.forEachIndexed { index, element ->
        when (element.type) {
            OPERATOR -> when {
                element.calculusPart.length > 1 -> {
                    var validationResultX = element.calculusPart
                    operatorsWithoutBase.forEach { regex ->
                        validationResultX = validationResultX.replace(regex, "")
                    }
                    if (validationResultX.isEmpty()) throw IllegalArgumentException("Multiple unary operators in sequence are not allowed: ${element.calculusPart}.")
                    if (operators.filter { it.canPrecedeNumber }.none { it.symbol.contains(validationResultX) }) throw IllegalArgumentException("Unary")
                    val currentOperator = element.calculusPart
                    list[index].calculusPart = currentOperator[0].toString()
                    list[index + 1].calculusPart = currentOperator.substring(1) + list[index + 1].calculusPart + list[index + 2].calculusPart
                    list[index + 2].calculusPart = ""
                }
                element.calculusPart.length == 1 && operators.filter { it.operatorWithBase }.any { it.symbol.contains(element.calculusPart) } -> {
                    list[index].calculusPart = list[index].calculusPart + list[index + 1].calculusPart
                    list[index + 1].calculusPart = ""
                }
            }
            NUMBER -> {
            }
            LEFT_PARENTHESIS -> {
                require(index == 0 || list[index - 1].type == OPERATOR || list[index - 1].type == LEFT_PARENTHESIS) { "'(' cannot be preceded by number" }
            }
            RIGHT_PARENTHESIS -> {
                println(list[index + 1])
                println(operators.filter { it.canPrecedeNumber && it.operatorWithBase }.map { it.regex })
                require(index == list.size - 1 ||
                        (list[index + 1].type == OPERATOR && operators.filter { it.canPrecedeNumber && it.operatorWithBase }.none { it.regex.matches(list[index + 1].calculusPart) })
                        || list[index + 1].type == RIGHT_PARENTHESIS) { "')' cannot be followed by number or operator that precedes number: ${list[index].calculusPart + list[index + 1].calculusPart}" }
            }
        }
    }

    val resultList = list.filter { it.calculusPart.isNotEmpty() }

    resultList.forEach { println(it) }
}

data class CalculusPart(val type: CalculusPartType, var calculusPart: String)

enum class CalculusPartType {
    OPERATOR,
    NUMBER,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS
}