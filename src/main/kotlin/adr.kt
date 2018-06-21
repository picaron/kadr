import kotlinx.cinterop.*
import platform.posix.*

const val NEWLINE = "\n"

fun main(args: Array<String>) {
    if (args.size < 1) {
        println("args: infile outfile")
        return
    }
    val lines = readLines(args[0])
    writeLines(args[1], lines)
}

fun readLines(filename: String) : List<String> {
    val file = fopen(filename, "r")
    if (file == null) {
        perror("cannot open input file $filename")
        return listOf<String>()
    }
    val lines = mutableListOf<String>()
    try {
        memScoped {
            val bufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(bufferLength)
            while(true) {
                val nextLine = fgets(buffer, bufferLength, file)?.toKString()
                if (nextLine == null) break
                lines.add(nextLine)
            }
        }
    } finally {
        fclose(file)
    }
    return lines
}

fun writeLines(filename: String, lines: List<String>, addNewline: Boolean = false) {
    val file = fopen(filename, "w")
    if (file == null) {
        perror("cannot open output file $filename")
        return
    }
    try {
        for (line in lines) {
            fwrite(line.cstr, line.length.toLong(), 1, file)
            if (addNewline) {
                fwrite(NEWLINE.cstr, NEWLINE.length.toLong(), 1, file)
            }
        }
    } finally {
        fclose(file)
    }
}
