# Quality convention plugin spec

Plugin IDs

- githubusers.quality.ktlint — applies org.jlleitschuh.gradle.ktlint, configures KtLint, wires ktlintCheck into check
- githubusers.quality.detekt — applies io.gitlab.arturbosch.detekt, configures Detekt, wires detekt into check
- githubusers.quality — aggregator that applies both conventions

Detekt configuration

- buildUponDefaultConfig=true, allRules=false
- ignoreFailures = (project.findProperty("detekt.ignoreFailures") as String?)?.toBoolean() ?: false
- config.setFrom("${rootDir}/config/detekt/detekt.yml")
- detektPlugins include detekt-ktlint wrapper
- tasks withType\<Detekt\> set jvmTarget=21, baseline at config/detekt/baseline.xml (if present), and exclude build/**, generated/**, build/generated/**, build/ksp/**

KtLint configuration

- Applies org.jlleitschuh.gradle.ktlint
- KtLintExtension.android=true
- tasks.named("check").dependsOn("ktlintCheck")

Module coverage

- AndroidApplicationConventionPlugin, AndroidLibraryConventionPlugin, JvmLibraryConventionPlugin now apply quality conventions by default
- Feature/Core/Navigation module conventions also apply quality

Result

- Any module applying the standard conventions gets both quality tools and check lifecycle wiring automatically.
