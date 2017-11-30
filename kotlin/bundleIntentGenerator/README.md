# Intent and Bundle extensions generator

Steps:

1. Add ```bundleGenerator.gradle``` to your project
2. In build.gradle specify properties:

```
apply from: 'bundleGenerator.gradle'
generateBundleAndIntentExtensions {
    packageName = "app.generated"
    fileName = "BundleIntentHelper"
    list = [ // for example
            ["your.package.app", "FancyModelParcelable", "fancyModel", "FancyModelParcelable"],
            ["kotlin", "String", "password", "Password"],
            ["kotlin", "String", "method", "Method"],
            ["kotlin", "Int", "containerId", "ContainerId"],
            ["kotlin", "Int", "id", "Id"],
    ]
}

```

List follow pattern:

```[package, class name, parameter name, get/remove/has method name]```

Example of generated file for Parcelable:

```
fun Intent.put(parameter: ConfigurationSettings?): Intent = putExtra("configuration", parameter)
fun Intent.getConfigurationSettings(): ConfigurationSettings? = getParcelableExtra("configuration")
fun Intent.hasConfigurationSettings(): Boolean = hasExtra("configuration")
fun Intent.removeConfigurationSettings() = removeExtra("configuration")
fun Bundle.put(parameter: ConfigurationSettings?): Bundle = this.apply { putParcelable("configuration", parameter) }
fun Bundle.getConfigurationSettings(): ConfigurationSettings? = getParcelable("configuration")
fun Bundle.hasConfigurationSettings(): Boolean = containsKey("configuration")
fun Bundle.removeConfigurationSettings() = remove("configuration")
```

Example of generated file for primitives:

```
fun Intent.putId(parameter: Int?): Intent = putExtra("id", parameter)
fun Intent.getId(): Int? = getIntExtra("id", 0)
fun Intent.hasId(): Boolean = hasExtra("id")
fun Intent.removeId() = removeExtra("id")
fun Bundle.putId(parameter: Int?): Bundle = this.apply { putInt("id", parameter!!) }
fun Bundle.getId(): Int? = getInt("id")
fun Bundle.hasId(): Boolean = containsKey("id")
fun Bundle.removeId() = remove("id")
```