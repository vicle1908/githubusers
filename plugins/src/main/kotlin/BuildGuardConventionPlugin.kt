import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildGuardConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("verifyNoLocalBuildLogic") {
            group = "verification"
            description = "Fails if module build.gradle.kts contains forbidden build logic DSL blocks"
            doLast {
                val enforce = (target.findProperty("enforceNoLocalBuildLogic") as String?)?.toBoolean() ?: false
                if (!enforce) return@doLast
                val file = target.buildFile
                if (!file.exists()) return@doLast
                val text = file.readText()
                val forbidden = listOf(
                    "\nandroid {",
                    "\ncomposeOptions {",
                    "\npublishing {",
                    "\ndetekt {",
                    "\nktlint {"
                )
                val hit = forbidden.firstOrNull { pattern -> text.contains(pattern) }
                if (hit != null) {
                    throw org.gradle.api.GradleException("Forbidden build logic block detected in ${file}: '${hit.trim()}' - move logic into convention plugins")
                }
            }
        }
    }
}
