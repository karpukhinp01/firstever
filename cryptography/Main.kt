import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun getBin(a: Int): String {
    var res = ""
    var realNum = a
    if (a != 0) {
        while (realNum > 0) {
            if (realNum % 2 == 0) res += "0" else res+= "1"
            realNum /=  2
        }
    } else res = "00000000"
    while (res.length < 8) res += "0"
    return res.toString().reversed()
}




fun getDec(a: String): Int {
    val c = a.reversed()
    var count = 0
    var translate = 1
    for (i in c.indices) {
        count += translate * c[i].toString().toInt()
        translate *= 2
    }
    return count
}




fun setLeastSignificantBitToDes(bit: Int, newBit: Int): Int {       //input is pixel.blue and newBit
    var a = getBin(bit).toMutableList()

    if (a[7].toString() == newBit.toString()) {
    } else {
        a[7] = newBit.toString().toCharArray()[0]
    }
    return getDec(a.joinToString(""))
}

fun encr (fileName: String, newName: String): String {
    var res = 0
    try {
        val image: BufferedImage = ImageIO.read(File(fileName))
        println("Message to hide:")
        var mes = readLine()!!.toByteArray(Charsets.UTF_8).toMutableList()
        var toEncr = mutableListOf<String>()
        for (i in mes) {
            toEncr.add(getBin(i.toInt()))
        }

        var toEncrFinal = mutableListOf<Int>()
        for (i in toEncr) {
            for (j in i) {
                toEncrFinal.add(j.toString().toInt())
            }
        }
        println("Password:")
        val password = readLine()!!.toByteArray(Charsets.UTF_8).toMutableList()
        val passwordFin = mutableListOf<String>()
        for (i in password) {
            passwordFin.add(getBin(i.toInt()))
        }

        val pasEncr = mutableListOf<Int>()
        for (i in passwordFin) {
            for (j in i) {
                pasEncr.add(j.toString().toInt())
            }
        }
        var passwordIndex = 0
        for (i in toEncrFinal.indices) {
            toEncrFinal[i] = toEncrFinal[i] xor pasEncr[passwordIndex]
            if (passwordIndex < pasEncr.size - 1) {
                passwordIndex++
            } else passwordIndex = 0
        }

        repeat(22) {
            toEncrFinal.add(0)
        }
        repeat(2) {
            toEncrFinal.add(1)
        }

        var counter = 0
        cycy@for (i in 0 until image.height) {
            for (j in 0 until image.width) {
                val c = Color(image.getRGB(j, i))

                val rgb = Color(
                    c.red,
                    c.green,
                    setLeastSignificantBitToDes(c.blue, toEncrFinal[counter])
                ).rgb
                image.setRGB(
                    j,
                    i,
                    rgb
                )
                if (counter < toEncrFinal.size - 1) counter++ else break@cycy
            }
        }
        if (counter < toEncrFinal.size - 1) res = 2
        ImageIO.write(image, "png", File(newName))

    } catch (e: Exception) {
        println(e)
        res++
    }
    return if (res == 1) "Can't read input file!" else if (res == 2) "The input image is not large enough to hold this message."
    else "Message saved in $newName image."
}

fun show (fileName: String): String {
    val image: BufferedImage = ImageIO.read(File(fileName))
    var blues = mutableListOf<Int>()
    for (i in 0 until image.height) {
        for (j in 0 until  image.width) {
            val c = Color(image.getRGB(j, i))
            blues.add(c.blue % 2)
        }
    }

    println("Password:")
    val password = readLine()!!.toByteArray(Charsets.UTF_8)
    val passwordNext = mutableListOf<Int>()
    for (i in password) {
        passwordNext.add(i.toInt())
    }

    var newAr = mutableListOf<Int>()
    var counterept = 0
    repeat(2) {
        newAr.add(getDec(blues.slice(counterept..7+counterept).joinToString("")))
        counterept+=8
    }
    while (true) {
        newAr.add(getDec(blues.slice(counterept..7+counterept).joinToString("")))
        counterept+=8
        if (newAr[newAr.lastIndex] == 3 && newAr[newAr.lastIndex - 1] == 0 && newAr[newAr.lastIndex - 1] == 0) break
    }
    repeat(3) {
        newAr.removeLast()
    }
    var passwordIndex = 0
    for (i in newAr.indices) {
        newAr[i] = newAr[i] xor passwordNext[passwordIndex]
        if (passwordIndex < passwordNext.size - 1) {
            passwordIndex++
        } else passwordIndex = 0
    }
    var final = mutableListOf<Char>()
    for (i in newAr)
        final.add(i.toChar())
    print("Message: ")
    return final.joinToString("")
}

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        var task = readLine()!!
        when (task) {
            "exit" -> {
                println("Bye!")
                break
            }
            "hide" -> {
                println("Input image file:")
                val fileName = readLine()!!
                println("Output image file:")
                val newFile = readLine()!!
                println(encr(fileName, newFile))
            }
            "show" -> {
                println("Input image file:")
                val fileName = readLine()!!
                println(show(fileName))
            }
        }
    }
}



