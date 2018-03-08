import org.gradle.api.plugins.ExtensionAware

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension
import org.junit.platform.gradle.plugin.JUnitPlatformPlugin


plugins { `kotlin-dsl` }
repositories { jcenter() }
dependencies {
    compile("com.squareup:kotlinpoet:0.7.0")
    compile("com.squareup.okhttp3:okhttp:3.9.1")
}


//region spek
// setup the plugin
buildscript { dependencies { classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0") } }
apply {
    plugin("org.junit.platform.gradle.plugin")
    plugin("java-gradle-plugin")
}
configure<JUnitPlatformExtension> { filters { engines { include("spek") } } }
// setup dependencies
dependencies {
    testCompile("org.jetbrains.spek:spek-api:1.1.5")
    testCompile("junit:junit:4.12")
    testCompile("org.mockito:mockito-core:2.+")
    testRuntime("org.jetbrains.spek:spek-junit-platform-engine:1.1.5")
}
// extension for configuration
fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}

fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}
//endregion
