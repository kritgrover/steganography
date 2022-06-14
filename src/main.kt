import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOException
import javax.imageio.ImageIO

lateinit var image: BufferedImage
lateinit var encoded: List<Int>


fun main() {

    while (true) {
        println()
        println("----Menu----")
        println("1. Hide message in image")
        println("2. Retrieve message from image")
        println("3. Exit")
        val choice = readln()

        if (choice == "hide" || choice == "1") {
            hide()

        } else if (choice == "show" || choice == "2") {
            show()

        } else if (choice == "exit" || choice == "3") {
            println("Thank you!")
            break

        } else {
            println("ERROR: Couldn't read output. Please try again.")

        }
    }
}


fun hide() {

    println("Enter input image name: ")
    val inputFileName = readln()

    println("Enter output image name:")
    val outputFileName = readln()

    val encoding = mutableListOf<String>()

    println("Message to hide:")
    val messageString : String = readln()

    println("Password:")
    val password = readln()

    val marker = encode(messageString, password)
    marker.toByteArray().forEach { encoding += it.toString(2).padStart(8, '0') }
    encoded = encoding.joinToString("").map { it.digitToInt() }

    val message = encoded

    try {

        val imageFile = File(inputFileName)
        image = ImageIO.read(imageFile)
        val leastSignificantBits = mutableListOf<Int>()

        for (i in 0 until image.height) {
            for (j in 0 until image.width) {
                val pixelColor = Color(image.getRGB(j, i))
                leastSignificantBits += pixelColor.blue
            }
        }

        if (message.size > leastSignificantBits.size) {
            return println("The input image is not large enough to hold this message.")
        }

        for (i in message.indices) {
            if (leastSignificantBits[i] % 2 == 0) {
                if (message[i] == 1) leastSignificantBits[i] = leastSignificantBits[i] or 1
            } else {
                if (message[i] == 0) leastSignificantBits[i] = (leastSignificantBits[i] xor 1)
            }
        }

        for (i in 0 until image.height) {
            for (j in 0 until image.width) {
                val pixelColor = Color(image.getRGB(j, i))
                image.setRGB(j, i, Color(pixelColor.red, pixelColor.green, leastSignificantBits[i * image.width + j]).rgb
                )
            }
        }

    } catch (e: IIOException) {

        return println("Can't read input file!")

    }

    saveImage(image, outputFileName)
    println("Message saved in $outputFileName image.")
    println()

}


fun show() {

    println("Input image file:")
    val fileName = readln()

    try {
        val image = ImageIO.read(File(fileName))

        println("Enter password:")
        val password = readln()

        println("Retrieving message from $fileName...")
        println()

        println("Encoded Message:")
        println(decode(image, password))
        println()

    } catch (e: IIOException) {
        println("ERROR: Cannot read input file!")
        println()
    }
}


fun encode(message: String, password: String): String {

    var passEnc = password

    if (message.length > password.length) {
        var i = 0
        while (passEnc.length != message.length) {
            passEnc += password[i]
            ++i
            if (password.length == i) {
                i = 0
            }
        }
    }

    val encodedMessage = message xor (passEnc)

    return "$encodedMessage\u0000\u0000\u0003"
}


fun decode(image: BufferedImage, password: String): String {

    val msgAllBits = mutableListOf<Int>()

    for (i in 0 until image.height) {
        for (j in 0 until image.width) {
            val pixelColor = Color(image.getRGB(j, i))
            msgAllBits += (pixelColor.blue).toString(2).takeLast(1).toInt()
        }
    }

    var message = ""
    var passEnc = password
    val end = "000000000000000000000011"

    msgAllBits.joinToString("").split(end).first().chunked(8)
        .forEach { message += it.toInt(2).toChar() }

    if (message.length > password.length) {
        var i = 0
        while (passEnc.length != message.length) {
            passEnc += password[i]
            ++i
            if (password.length == i) i = 0
        }
    }

    return message xor (passEnc)
}


fun saveImage(image: BufferedImage, imageFile: String) {

    ImageIO.write(image, "png", File(imageFile))
}


infix fun String.xor(that: String) = mapIndexed { index, c -> that[index].code.xor(c.code)
    }.joinToString(separator = "") {
        it.toChar().toString()
}