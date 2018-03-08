package utils

import org.gradle.api.Project

object GitUtils {
  @JvmStatic fun gitHash(project: Project): String? {
    val res = "git rev-parse --short HEAD".executeCommand(project.rootDir)?.trim()
    return "git diff".executeCommand(project.rootDir)?.trim()?.isNotEmpty()?.let {
      if (it) "$res-dirty" else res
    }
  }

  @JvmStatic fun gitCommitCount(project: Project) =
    "git rev-list --all --count".executeCommand(project.rootDir)?.trim()?.toInt()

  @JvmStatic fun gitLastTag(project: Project): String? =
    "git describe --abbrev=0 --tags".executeCommand(project.rootDir)?.trim()
}
