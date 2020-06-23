fun main() {
    /*print("Insert calculus: ")
    val calculus = readLine()
    val list = mutableListOf<String>()
    println(calculus.toOperationList(list))*/

    val list = mutableListOf<Pair<Boolean, String>>()

    val calc = "- 3.097 +. dfgh3255. 43 * 11 - V[3]     8+log[2]5".replace(" ", "").toList()
    var isDigit = calc[0].isDigit()
    var currentListIndex = 0
    list.add(isDigit to calc[currentListIndex].toString())
    calc.forEachIndexed { i, ch ->
        if (i == 0) return@forEachIndexed
        when {
            (ch.isDigit() == isDigit || ch == '.') -> list[currentListIndex] = list[currentListIndex].first to list[currentListIndex].second + ch
            else -> {
                isDigit = !isDigit
                list.add(isDigit to ch.toString())
                currentListIndex++
            }
        }
    }

    list.forEach {
        if (!it.first) {
            println(it)
        }
    }
}

private fun String?.toOperationList(mutableList: MutableList<String>): List<String> {
    // if (this.startsWith())
    return listOf()
}
