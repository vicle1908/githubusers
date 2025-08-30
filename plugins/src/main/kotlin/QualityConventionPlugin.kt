import org.gradle.api.Plugin
import org.gradle.api.Project

class QualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("githubusers.quality.ktlint")
            apply("githubusers.quality.detekt")
        }
        // Aggregate task could be added here if needed; detekt/ktlint already wire into check
    }
}
