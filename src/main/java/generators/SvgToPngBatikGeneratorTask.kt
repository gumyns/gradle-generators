package generators

import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import utils.runCommand
import java.io.*
import java.util.zip.ZipInputStream
import kotlin.math.roundToInt

open class SvgToPngBatikGeneratorTask : DefaultTask() {
  var batik: File = File(project.rootDir.path + "/batik")
  var from: File = project.rootDir
  var to: File = project.rootDir

  val targets = arrayOf(
    arrayOf("drawable-ldpi", 0.75),
    arrayOf("drawable-mdpi", 1.0),
    arrayOf("drawable-hdpi", 1.5),
    arrayOf("drawable-xhdpi", 2.0),
    arrayOf("drawable-xxhdpi", 3.0),
    arrayOf("drawable-xxxhdpi", 4.0)
  )

  @TaskAction fun generate() {
    batik.mkdirs()
    if (!File(batik.absolutePath, "batik-1.9").exists()) {
      // get batik
      println("Downloading Batik")
      val request = Request.Builder()
        .url("http://ftp.ps.pl/pub/apache/xmlgraphics/batik/binaries/batik-bin-1.9.zip")
        .build()

      OkHttpClient().newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response");
        FileOutputStream(File(batik, "batik-bin-1.9.zip")).use {
          it.write(response.body()?.bytes());
        }
      }
      File(batik, "batik-bin-1.9.zip").also {
        unzip(it.absolutePath, batik.absolutePath)
      }.delete()
    }
    targets.forEach {
      File(to, it[0].toString()).mkdirs()
    }

    from.listFiles().forEach {
      val filename = it.name.substring(0, it.name.lastIndexOf('.')) + ".png"
      if ("[0-9]+_[0-9]+".toPattern().matcher(it.name).find()) {
        "[0-9]+".toRegex().findAll(it.name).map { it.value }.toList().also { entry ->
          generate(batik, to, it.absolutePath, filename, entry[0].toInt(), entry[1].toInt())
        }
      } else {
        "[0-9]+".toRegex().findAll(it.name).map { it.value }.toList().also { entry ->
          generate(batik, to, it.absolutePath, filename, entry[0].toInt(), entry[0].toInt())
        }
      }
      println("Generated $filename")
    }
  }

  fun unzip(zipFilePath: String, destDirectory: String) {
    val destDir = File(destDirectory)
    if (!destDir.exists()) {
      destDir.mkdir()
    }
    ZipInputStream(FileInputStream(zipFilePath)).use { zipIn ->
      var entry = zipIn.nextEntry
      while (entry != null) {
        val filePath = destDirectory + File.separator + entry.name
        if (!entry.isDirectory) {
          extractFile(zipIn, filePath)
        } else {
          File(filePath).mkdir()
        }
        zipIn.closeEntry()
        entry = zipIn.nextEntry
      }
    }
  }

  fun extractFile(zipIn: ZipInputStream, filePath: String) {
    BufferedOutputStream(FileOutputStream(filePath)).use {
      val bytesIn = ByteArray(4096)
      var read = zipIn.read(bytesIn)
      while (read != -1) {
        it.write(bytesIn, 0, read)
        read = zipIn.read(bytesIn)
      }
    }
  }

  fun generate(batik: File, to: File, from: String, filename: String, width: Int, height: Int) {
    targets.forEach {
      val target = File(File(to, it[0].toString()), filename).absolutePath
      val newWidth = (width * it[1].toString().toDouble())
      val newHeight = (height * it[1].toString().toDouble())
      "java -jar ${batik.absolutePath}/batik-1.9/batik-rasterizer-1.9.jar -d $target -w ${newWidth.roundToInt()} -h ${newHeight.roundToInt()} $from".runCommand(project.rootDir)
    }
  }
}