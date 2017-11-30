#SVG to Drawable
Steps:

0. Install Inkscape and add it to your PATH
1. Add ```svgGenerator.gradle``` to your project
2. In build.gradle add proper directories:

```
apply from: 'svgGenerator.gradle'
generateIcons {
    from file(project.rootDir.path + '/svg')
    to file(project.rootDir.path + '/main/src/res')
}
```

SVG file names should follow one on 2 patterns:

```name_{size_in_dp_for_xhdpi}.svg``` for sqaure drawables

```name_{width_in_dp_for_xhdpi}_{height_dp_for_xhdpi}``` for rectangular drawables

Examples: ```icon_check_48.svg``` or ```icon_loading_48_64.svg```