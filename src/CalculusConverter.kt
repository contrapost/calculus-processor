fun String.toCalculusParts() {

    val operatorMatches: MutableMap<IntRange, CalculusPart> = mutableMapOf()

    operators.forEach { operatorSpec ->
        operatorSpec.regex.findAll(this).forEach {
            operatorMatches[it.range] = OperatorPart(Operator(it.value, operatorSpec))
        }
    }

    val calculusPartList = this.extractMatches(operatorMatches)

    val calculusPartListComplete: MutableList<CalculusPart> = mutableListOf()

    when {
        calculusPartList.isEmpty() -> {
            addNumbersAndParentheses(calculusPartListComplete, this)
        }
        else -> {
            calculusPartList.forEach { entry ->
                when (entry) {
                    is OperatorPart -> calculusPartListComplete.add(entry)
                    else -> {
                        val undefinedCalculusPart = (entry as UndefinedPart).value
                        addNumbersAndParentheses(calculusPartListComplete, undefinedCalculusPart)
                    }
                }
            }
        }
    }

    calculusPartListComplete.forEach { println(it) }
}

fun addNumbersAndParentheses(list: MutableList<CalculusPart>, undefinedCalculusPart: String) {
    val matches: MutableMap<IntRange, CalculusPart> = mutableMapOf()
    DOUBLE_OR_INT_REGEX.toRegex().findAll(undefinedCalculusPart).forEach {
        matches[it.range] = NumberPart(it.value.toBigDecimal())
    }
    "\\(".toRegex().findAll(undefinedCalculusPart).forEach {
        matches[it.range] = LeftParenthesisPart()
    }
    "\\)".toRegex().findAll(undefinedCalculusPart).forEach {
        matches[it.range] = RightParenthesisPart()
    }
    when {
        matches.isEmpty() -> {
            list.add(UndefinedPart(undefinedCalculusPart))
        }
        else -> {
            val definedCalculusPartSublist = undefinedCalculusPart.extractMatches(matches)
            list.addAll(definedCalculusPartSublist)
        }
    }
}

fun String.extractMatches(listOfRanges: MutableMap<IntRange, CalculusPart>): List<CalculusPart> {
    var stopIndex = 0
    val stringAsListOfRanges: MutableList<CalculusPart> = mutableListOf()
    listOfRanges.entries.sortedBy { it.key.first }.forEachIndexed { index, entry ->
        when (index) {
            // last match
            listOfRanges.size - 1 -> {
                when {
                    // part of undefined string is longer than match
                    stopIndex until this.length != entry.key -> {
                        when {
                            entry.key.last == this.length - 1 -> {
                                stringAsListOfRanges.add(UndefinedPart(this.substring(stopIndex until entry.key.first)))
                                stringAsListOfRanges.add(entry.value)
                                stopIndex = entry.key.last + 1
                            }
                            entry.key.first == stopIndex -> {
                                stringAsListOfRanges.add(entry.value)
                                stringAsListOfRanges.add(UndefinedPart(this.substring(entry.key.last + 1 until this.length)))
                                stopIndex = this.length - 1
                            }
                            else -> {
                                stringAsListOfRanges.add(UndefinedPart(this.substring(stopIndex until entry.key.first)))
                                stringAsListOfRanges.add(entry.value)
                                stringAsListOfRanges.add(UndefinedPart(this.substring(entry.key.last + 1 until this.length)))
                                stopIndex = this.length - 1
                            }
                        }
                    }
                    else -> {
                        stringAsListOfRanges.add(entry.value)
                    }
                }
            }
            // all other cases
            else -> {
                when (entry.key.first) {
                    // the match is right after the last processed part of the string
                    stopIndex -> {
                        stringAsListOfRanges.add(entry.value)
                        stopIndex = entry.key.last + 1
                    }
                    // there are undefined part of the string before the match
                    else -> {
                        stringAsListOfRanges.add(UndefinedPart(value = this.substring(stopIndex until entry.key.first)))
                        stringAsListOfRanges.add(entry.value)
                        stopIndex = entry.key.last + 1
                    }
                }
            }
        }
    }
    return stringAsListOfRanges
}

data class CalculusPartWithRange(
        val range: IntRange,
        val calculusPart: CalculusPart
)