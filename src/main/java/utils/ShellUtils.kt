package utils

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File) {
  ProcessBuilder(*split(" ").toTypedArray())
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
    .redirectError(ProcessBuilder.Redirect.INHERIT)
    .start()
    .waitFor(10, TimeUnit.SECONDS)
}

fun String.executeCommand(workingDir: File): String? = try {
  val parts = this.split("\\s".toRegex())
  ProcessBuilder(*parts.toTypedArray())
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start().run {
      waitFor(10, TimeUnit.SECONDS)
      inputStream.bufferedReader().readText()
    }
} catch (e: IOException) {
  e.printStackTrace()
  null
}
