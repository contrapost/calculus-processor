package me.contrapost.fantasticcal.calculator.calculus

import kotlin.math.abs

fun validate(calculus: Calculus): ValidationResult {
    val errors: MutableList<String> = mutableListOf()
    val undefinedParts = calculus.parts.filterIsInstance<UndefinedPart>()
    val definedParts = calculus.parts.filter { it !is UndefinedPart }

    when {
        // case 1: calculus contains undefined parts
        undefinedParts.isNotEmpty() ->
            errors.add("Calculus contains undefined part(s): ${undefinedParts.map { it.value }}")
        else -> errors.addAll(
            validateDefinedParts(
                definedParts
            )
        )
    }

    return ValidationResult(errors.isEmpty(), errors)
}

fun validateDefinedParts(definedParts: List<CalculusPart>): List<String> {
    val errors = mutableListOf<String>()
    // case 2: calculus contains only 1 part
    if (definedParts.size == 1) {
        errors.add(
            "Calculus should contain at least one operator and one number: registered calculus is ${definedParts.first()}"
        )
    }

    // case 3: check parentheses
    errors.addAll(validateParenthesis(definedParts))

    definedParts.forEachIndexed { index, part ->
        when (index) {
            0 -> {
                if (!part.canBeOpeningPart()) errors.add("Calculus starts with invalid part: ${part.value}")
            }
            definedParts.size - 1 -> {
                if (!part.canBeClosingPart()) errors.add("Calculus ends with invalid part: ${part.value}")
            }
            else -> {
                val precedingPart = definedParts[index - 1]
                val succeedingPart = definedParts[index + 1]
                when (part) {
                    is OpenParenthesisPart -> {
                        if (!precedingPart.canPrecedeOpenParenthesis())
                            errors.add(
                                precedingPartOrderError(
                                    precedingPart,
                                    index,
                                    part
                                )
                            )
                        if (!succeedingPart.canBeOpeningPart())
                            errors.add(
                                succeedingPartOrderError(
                                    succeedingPart,
                                    index + 2,
                                    part
                                )
                            )
                    }
                    is CloseParenthesisPart -> {
                        if (!precedingPart.canBeClosingPart())
                            errors.add(
                                precedingPartOrderError(
                                    precedingPart,
                                    index,
                                    part
                                )
                            )
                        if (!succeedingPart.canSucceedCloseParenthesis())
                            errors.add(
                                succeedingPartOrderError(
                                    succeedingPart,
                                    index + 2,
                                    part
                                )
                            )

                    }
                    is NumberPart -> {
                        if (!precedingPart.canPrecedeNumber())
                            errors.add(
                                precedingPartOrderError(
                                    precedingPart,
                                    index,
                                    part
                                )
                            )
                        if (!succeedingPart.canSucceedNumber())
                            errors.add(
                                succeedingPartOrderError(
                                    succeedingPart,
                                    index + 2,
                                    part
                                )
                            )
                    }
                    is OperatorPart -> {
                        if (!precedingPart.canPrecedeOperator(part))
                            errors.add(
                                precedingPartOrderError(
                                    precedingPart,
                                    index,
                                    part
                                )
                            )
                        if (!succeedingPart.canSucceedOperator(part))
                            errors.add(
                                succeedingPartOrderError(
                                    succeedingPart,
                                    index + 2,
                                    part
                                )
                            )
                    }
                }
            }
        }
    }

    return errors
}

fun precedingPartOrderError(precedingPart: CalculusPart, partNumber: Int, thisPart: CalculusPart) =
    "${precedingPart.toShortDescription()} (part number $partNumber) cannot precede ${thisPart.type}"

fun succeedingPartOrderError(succeedingPart: CalculusPart, partNumber: Int, thisPart: CalculusPart) =
    "${succeedingPart.toShortDescription()} (part number $partNumber) cannot succeed ${thisPart.type}"

fun validateParenthesis(definedParts: List<CalculusPart>): List<String> {
    val parenthesisParts = definedParts.filter { it is OpenParenthesisPart || it is CloseParenthesisPart }
    if (parenthesisParts.isEmpty()) return emptyList()

    val errors = mutableListOf<String>()

    if (parenthesisParts.first() is CloseParenthesisPart)
        errors.add("First parenthesis in the calculus cannot be closing")

    val numberOfExcessParentheses = parenthesisParts.numberOfExcessParentheses()
    val numberOfExcessParenthesesText = when {
        numberOfExcessParentheses > 0 -> "open parentheses is $numberOfExcessParentheses"
        numberOfExcessParentheses < 0 -> "close parentheses is ${abs(numberOfExcessParentheses)}"
        else -> null
    }
    if (numberOfExcessParenthesesText != null)
        errors.add("Parentheses mismatch: number of excess $numberOfExcessParenthesesText")

    return errors
}

fun List<CalculusPart>.numberOfExcessParentheses(): Int {
    var counter = 0
    this.forEach {
        when (it) {
            is OpenParenthesisPart -> counter++
            else -> counter--
        }
    }
    return counter
}

data class ValidationResult(
    val valid: Boolean,
    val errors: List<String>
) {
    init {
        require(valid || !valid && errors.isNotEmpty()) {
            "${ValidationResult::errors.name} should contain data when result is invalid."
        }
    }
}
