package me.contrapost.fantasticcal.calculus

fun validate(calculusList: List<CalculusPart>): ValidationResult {
    val errors: MutableList<String> = mutableListOf()
    val undefinedParts = calculusList.filterIsInstance<UndefinedPart>()
    val definedParts = calculusList.filter { it !is UndefinedPart }

    // case 1: calculus has undefined parts
    if (undefinedParts.isNotEmpty()) {
        errors.add("Calculus contains undefined part(s): ${undefinedParts.map { it.value }}")
    }

    // case 2: calculus contains only 1 part
    if (definedParts.size == 1) {
        errors.add("Calculus should contain at least one operator and one number: registered calculus is ${calculusList.first()}")
    }

    // case 3: calculus contains 2 parts: operator and number

    return ValidationResult(errors.isEmpty(), errors)
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