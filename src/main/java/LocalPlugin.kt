import generators.BundleAndIntentExtensionsGenerator
import generators.SvgToPngBatikGeneratorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra

class LocalPlugin : Plugin<Project> {
  private enum class TaskGroup(val visibleName: String) {
    GENERATOR("custom generators");

    override fun toString(): String = visibleName
  }

  override fun apply(target: Project?) {
    target?.tasks?.apply {
      create("generateBundleAndIntentExtensions", BundleAndIntentExtensionsGenerator::class.java) {
        description = "Generates kotlin extensions for defined data"
        group = TaskGroup.GENERATOR.toString()
      }
      create("generateIcons", SvgToPngBatikGeneratorTask::class.java) {
        description = "Generates icons from svg"
        group = TaskGroup.GENERATOR.toString()
      }
    }
    target?.extra?.apply {

    }
  }
}