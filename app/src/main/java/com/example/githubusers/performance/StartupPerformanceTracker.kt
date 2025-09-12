package com.example.githubusers.performance
import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Tracks app startup performance and identifies optimization opportunities
 * @Suppress("ImportOrdering")

 *
 * Measures:
 * - Cold start time (process creation to first frame)
 * - Warm start time (activity recreation to first frame)
 * - Hot start time (activity resume to first frame)
 * - Time-to-interactive (TTI)
 * - Component initialization times
 */
@Singleton
class StartupPerformanceTracker @Inject constructor() {

    companion object {
        // Startup phases
        private const val PHASE_PROCESS_CREATION = "process_creation"
        private const val PHASE_APPLICATION_ONCREATE = "application_onCreate"
        private const val PHASE_ACTIVITY_ONCREATE = "activity_onCreate"
        private const val PHASE_FIRST_FRAME = "first_frame"
        private const val PHASE_TIME_TO_INTERACTIVE = "time_to_interactive"

        // Performance thresholds (ms)
        private const val COLD_START_THRESHOLD = 1500L // 1.5s
        private const val WARM_START_THRESHOLD = 1000L // 1s
        private const val HOT_START_THRESHOLD = 500L // 0.5s

        // Analysis thresholds (ms)
        private const val APP_INIT_SLOW_THRESHOLD = 500L
        private const val APP_INIT_OPTIMIZE_THRESHOLD = 200L
        private const val ACTIVITY_INIT_SLOW_THRESHOLD = 300L
        private const val ACTIVITY_INIT_OPTIMIZE_THRESHOLD = 100L
        private const val FRAME_RENDER_SLOW_THRESHOLD = 200L
        private const val FRAME_RENDER_OPTIMIZE_THRESHOLD = 100L
        private const val TTI_SLOW_THRESHOLD = 500L
        private const val TTI_OPTIMIZE_THRESHOLD = 200L
        private const val COMPONENT_SLOW_THRESHOLD = 100L
    }

    private val startupTimestamps = mutableMapOf<String, Long>()
    private var processStartTime: Long = 0
    private var startupType: StartupType = StartupType.COLD

    enum class StartupType {
        COLD, // Process creation
        WARM, // Activity recreation
        HOT // Activity resume
    }

    /**
     * Mark the start of app process creation (called from Application.onCreate)
     */
    fun markProcessStart() {
        processStartTime = SystemClock.elapsedRealtime()
        startupTimestamps[PHASE_PROCESS_CREATION] = processStartTime

        Timber.tag("StartupPerf").d("🚀 Process creation started")
    }

    /**
     * Mark application onCreate completion
     */
    fun markApplicationCreated() {
        val timestamp = SystemClock.elapsedRealtime()
        startupTimestamps[PHASE_APPLICATION_ONCREATE] = timestamp

        val duration = timestamp - processStartTime
        Timber.tag("StartupPerf").d("📱 Application created in ${duration}ms")
    }

    /**
     * Mark activity onCreate completion
     */
    fun markActivityCreated() {
        val timestamp = SystemClock.elapsedRealtime()
        startupTimestamps[PHASE_ACTIVITY_ONCREATE] = timestamp

        val duration = timestamp - processStartTime
        Timber.tag("StartupPerf").d("🎭 Activity created in ${duration}ms")
    }

    /**
     * Mark first frame rendered
     */
    fun markFirstFrameRendered() {
        val timestamp = SystemClock.elapsedRealtime()
        startupTimestamps[PHASE_FIRST_FRAME] = timestamp

        val totalStartupTime = timestamp - processStartTime
        determineStartupType(totalStartupTime)

        Timber.tag("StartupPerf").i(
            "🎨 First frame rendered! ${startupType.name} start: ${totalStartupTime}ms"
        )

        // Log warning if startup is slow
        when (startupType) {
            StartupType.COLD -> if (totalStartupTime > COLD_START_THRESHOLD) {
                Timber.tag(
                    "StartupPerf"
                ).w("⚠️ Slow cold start: ${totalStartupTime}ms (target: <${COLD_START_THRESHOLD}ms)")
            }
            StartupType.WARM -> if (totalStartupTime > WARM_START_THRESHOLD) {
                Timber.tag(
                    "StartupPerf"
                ).w("⚠️ Slow warm start: ${totalStartupTime}ms (target: <${WARM_START_THRESHOLD}ms)")
            }
            StartupType.HOT -> if (totalStartupTime > HOT_START_THRESHOLD) {
                Timber.tag(
                    "StartupPerf"
                ).w("⚠️ Slow hot start: ${totalStartupTime}ms (target: <${HOT_START_THRESHOLD}ms)")
            }
        }
    }

    /**
     * Mark time-to-interactive (when user can interact with the app)
     */
    fun markTimeToInteractive() {
        val timestamp = SystemClock.elapsedRealtime()
        startupTimestamps[PHASE_TIME_TO_INTERACTIVE] = timestamp

        val ttiTime = timestamp - processStartTime
        Timber.tag("StartupPerf").i("✨ Time-to-interactive: ${ttiTime}ms")

        generateStartupReport()
    }

    /**
     * Track component initialization time
     */
    @Suppress("unused")
    fun trackComponentInitialization(componentName: String, duration: Long) {
        Timber.tag("StartupPerf").d("🔧 $componentName initialized in ${duration}ms")

        if (duration > COMPONENT_SLOW_THRESHOLD) {
            Timber.tag("StartupPerf").w("⚠️ Slow component init: $componentName (${duration}ms)")
        }
    }

    private fun determineStartupType(totalTime: Long) {
        startupType = when {
            totalTime <= HOT_START_THRESHOLD -> StartupType.HOT
            totalTime <= WARM_START_THRESHOLD -> StartupType.WARM
            else -> StartupType.COLD
        }
    }

    private fun generateStartupReport() {
        val report = buildString {
            appendLine("📊 STARTUP PERFORMANCE REPORT")
            appendLine("================================")
            appendLine("Startup Type: ${startupType.name}")
            appendLine()

            val processStart = startupTimestamps[PHASE_PROCESS_CREATION] ?: 0

            startupTimestamps.entries
                .sortedBy { it.value }
                .forEach { (phase, timestamp) ->
                    val duration = timestamp - processStart
                    val phaseName = phase.replace("_", " ").uppercase()
                    appendLine("$phaseName: ${duration}ms")
                }

            appendLine()
            appendLine("Performance Analysis:")
            analyzePerformance().forEach { analysis ->
                appendLine("• $analysis")
            }
        }

        Timber.tag("StartupPerf").i("\n$report")
    }

    private fun analyzeApplicationInit(processStart: Long, appCreated: Long): String {
        if (appCreated <= processStart) return "No app init data"
        val appInitTime = appCreated - processStart
        return when {
            appInitTime > APP_INIT_SLOW_THRESHOLD -> "App init slow: ${appInitTime}ms"
            appInitTime > APP_INIT_OPTIMIZE_THRESHOLD -> "App init optimize: ${appInitTime}ms"
            else -> "App init fast: ${appInitTime}ms"
        }
    }

    private fun analyzeActivityInit(appCreated: Long, activityCreated: Long): String {
        if (activityCreated <= appCreated) return "No activity init data"
        val activityInitTime = activityCreated - appCreated
        return when {
            activityInitTime > ACTIVITY_INIT_SLOW_THRESHOLD -> "Activity init slow: ${activityInitTime}ms"
            activityInitTime > ACTIVITY_INIT_OPTIMIZE_THRESHOLD -> "Activity init optimize: ${activityInitTime}ms"
            else -> "Activity init fast: ${activityInitTime}ms"
        }
    }

    private fun analyzeFirstFrame(activityCreated: Long, firstFrame: Long): String {
        if (firstFrame <= activityCreated) return "No frame data"
        val frameRenderTime = firstFrame - activityCreated
        return when {
            frameRenderTime > FRAME_RENDER_SLOW_THRESHOLD -> "Frame render slow: ${frameRenderTime}ms"
            frameRenderTime > FRAME_RENDER_OPTIMIZE_THRESHOLD -> "Frame render optimize: ${frameRenderTime}ms"
            else -> "Frame render fast: ${frameRenderTime}ms"
        }
    }

    private fun analyzeTTI(firstFrame: Long, tti: Long): String {
        if (tti <= firstFrame) return "No TTI data"
        val interactiveTime = tti - firstFrame
        return when {
            interactiveTime > TTI_SLOW_THRESHOLD -> "TTI slow: ${interactiveTime}ms after frame"
            interactiveTime > TTI_OPTIMIZE_THRESHOLD -> "TTI optimize: ${interactiveTime}ms after frame"
            else -> "TTI fast: ${interactiveTime}ms after frame"
        }
    }

    private fun analyzePerformance(): List<String> {
        val analyses = mutableListOf<String>()

        val processStart = startupTimestamps[PHASE_PROCESS_CREATION] ?: 0
        val appCreated = startupTimestamps[PHASE_APPLICATION_ONCREATE] ?: 0
        val activityCreated = startupTimestamps[PHASE_ACTIVITY_ONCREATE] ?: 0
        val firstFrame = startupTimestamps[PHASE_FIRST_FRAME] ?: 0
        val tti = startupTimestamps[PHASE_TIME_TO_INTERACTIVE] ?: 0

        analyses.add(analyzeApplicationInit(processStart, appCreated))
        analyses.add(analyzeActivityInit(appCreated, activityCreated))
        analyses.add(analyzeFirstFrame(activityCreated, firstFrame))
        analyses.add(analyzeTTI(firstFrame, tti))

        return analyses
    }

    /**
     * Get current startup metrics
     */
    @Suppress("unused")
    fun getStartupMetrics(): StartupMetrics {
        val processStart = startupTimestamps[PHASE_PROCESS_CREATION] ?: 0
        val firstFrame = startupTimestamps[PHASE_FIRST_FRAME] ?: 0
        val tti = startupTimestamps[PHASE_TIME_TO_INTERACTIVE] ?: 0

        return StartupMetrics(
            startupType = startupType,
            totalStartupTime = if (firstFrame > processStart) firstFrame - processStart else 0,
            timeToInteractive = if (tti > processStart) tti - processStart else 0,
            timestamps = startupTimestamps.toMap()
        )
    }
}

/**
 * Data class for startup metrics
 */
data class StartupMetrics(
    val startupType: StartupPerformanceTracker.StartupType,
    val totalStartupTime: Long,
    val timeToInteractive: Long,
    val timestamps: Map<String, Long>
)
