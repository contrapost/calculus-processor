import java.util.*

// Calculator program
fun main() {
    printTitle("0.0.1")
    print(greetings())
}

fun printTitle(version: String = "unknown") {
    val title = """
                    FANTASTIC CALCULATOR
                        (or just FanC)
        Welcome to the FANTASTIC CALCULATOR version $version!
        
    """.trimIndent()
    print(title)
}

fun greetings(): String {
    val dayTime = dayTime()
    print("Please, stay calm and print your name: ")
    val name = readLine()
    val verifiedName = when {
        name.isNullOrBlank() -> "- whatever your name is"
        else -> name
    }
    val greetingText = "Good $dayTime"
    return "$greetingText, $verifiedName! "
}

fun dayTime(): String {
    val time = Calendar.getInstance()
    val hourOfDay = time.get(Calendar.HOUR_OF_DAY)
    val timeZone = time.timeZone.id
    val dayTime = when (hourOfDay) {
        in 0..5 -> "night"
        in 6..12 -> "morning"
        in 12..18 -> "day"
        else -> "evening"
    }
    return "$dayTime ($timeZone)"
}
