package generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import generatorGroup
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.task
import java.io.File

fun Project.provideBundleAndIntentExtensionsGenerator() =
  task<BundleAndIntentExtensionsGenerator>("generateBundleAndIntentExtensions") {
    description = "Generates kotlin extensions for defined data"
    group = generatorGroup
  }

open class BundleAndIntentExtensionsGenerator : DefaultTask() {
  var packageName = "app.generated"
  var fileName = "BundleIntentHelper"
  var list: Array<Array<String>> = arrayOf()

  @TaskAction fun generateStuff() {
    generate(packageName, fileName, list)
  }

  fun generate(packageName: String, fileName: String, list: Array<Array<String>>) {
    val intent = ClassName("android.content", "Intent")
    val bundle = ClassName("android.os", "Bundle")
    val bool = ClassName("kotlin", "Boolean")

    val javaFile = FileSpec.builder(packageName, fileName)
    list.forEach { array ->
      val type = ClassName(array[0].trim(), array[1].trim()).asNullable()

      // intent put
      javaFile.addFunction(FunSpec.builder(putName(type, array[3].trim()))
        .receiver(intent)
        .returns(intent)
        .addParameter(ParameterSpec.builder("parameter", type).build())
        .addCode("return putExtra(\"${array[2].trim()}\", parameter)")
        .build())
      // intent get
      javaFile.addFunction(FunSpec.builder("get${array[3].trim()}")
        .receiver(intent)
        .returns(type)
        .addCode(intentGet(type, array[2].trim()))
        .build())
      // intent has
      javaFile.addFunction(FunSpec.builder("has${array[3].trim()}")
        .receiver(intent)
        .returns(bool)
        .addCode("return hasExtra(\"${array[2].trim()}\")")
        .build())
      // intent remove
      javaFile.addFunction(FunSpec.builder("remove${array[3].trim()}")
        .receiver(intent)
        .addCode("return removeExtra(\"${array[2].trim()}\")")
        .build())
      // bundle put
      javaFile.addFunction(FunSpec.builder(putName(type, array[3].trim()))
        .receiver(bundle)
        .returns(bundle)
        .addParameter(ParameterSpec.builder("parameter", type).build())
        .addCode(bundlePut(type, array[2].trim()))
        .build())
      // bundle get
      javaFile.addFunction(FunSpec.builder("get${array[3].trim()}")
        .receiver(bundle)
        .returns(type)
        .addCode(bundleGet(type, array[2].trim()))
        .build())
      // bundle has
      javaFile.addFunction(FunSpec.builder("has${array[3].trim()}")
        .receiver(bundle)
        .returns(bool)
        .addCode("return containsKey(\"${array[2].trim()}\")")
        .build())
      // bundle remove
      javaFile.addFunction(FunSpec.builder("remove${array[3].trim()}")
        .receiver(bundle)
        .addCode("return remove(\"${array[2].trim()}\")\n")
        .build())
    }
    javaFile.build().writeTo(File("${project.projectDir.path}/src/main/java/"))
  }

  private fun bundlePut(className: ClassName, name: String) = when (className.simpleName()) {
    "Boolean",
    "Int",
    "Long",
    "Float",
    "Double",
    "Byte",
    "Char",
    "Short" -> "return this.apply { put${className.simpleName()}(\"$name\", parameter!!) }"
    "String" -> "return this.apply { put${className.simpleName()}(\"$name\", parameter) }"
    else -> "return this.apply { putParcelable(\"$name\", parameter) }"
  }

  private fun intentGet(className: ClassName, name: String) = when (className.simpleName()) {
    "Boolean" -> "return get${className.simpleName()}Extra(\"$name\", false)"
    "Int" -> "return get${className.simpleName()}Extra(\"$name\", 0)"
    "Long" -> "return get${className.simpleName()}Extra(\"$name\", 0)"
    "Float" -> "return get${className.simpleName()}Extra(\"$name\". 0.0.toFloat()"
    "Double" -> "return get${className.simpleName()}Extra(\"$name\", 0.0)"
    "String" -> "return get${className.simpleName()}Extra(\"$name\")"
    "Byte" -> "return get${className.simpleName()}Extra(\"$name\", 0.toByte())"
    "Char" -> "return get${className.simpleName()}Extra(\"$name\", 0.toChar())"
    "Short" -> "return get${className.simpleName()}Extra(\"$name\", 0.toShort())"
    else -> "return getParcelableExtra(\"${name}\")"
  }

  private fun bundleGet(className: ClassName, name: String) = when (className.simpleName()) {
    "Boolean",
    "Int",
    "Long",
    "Float",
    "Double",
    "String",
    "Byte",
    "Char",
    "Short" -> "return get${className.simpleName()}(\"$name\")"
    else -> "return getParcelable(\"$name\")"
  }

  private fun putName(className: ClassName, name: String) = when (className.simpleName()) {
    "Boolean",
    "Int",
    "Long",
    "Float",
    "Double",
    "String",
    "Byte",
    "Char",
    "Short" -> "put$name"
    else -> "put"
  }
}

