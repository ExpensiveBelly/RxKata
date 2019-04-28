package algorithm


fun toBinary(number: Int): String =
        when (number) {
            0 -> "0"
            1 -> "1"
            else -> "${toBinary(number / 2)}${number % 2}"
        }