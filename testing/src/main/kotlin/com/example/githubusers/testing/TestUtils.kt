package com.example.githubusers.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Shared test utilities for consistent testing across all modules.
 * 
 * Provides:
 * - Coroutine testing utilities
 * - Test data factories
 * - Common test fixtures
 * - Performance testing helpers
 */
object TestUtils {
    
    /**
     * Standard test dispatcher for consistent coroutine testing
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun createTestDispatcher(scheduler: TestCoroutineScheduler = TestCoroutineScheduler()): TestDispatcher {
        return StandardTestDispatcher(scheduler)
    }
    
    /**
     * Run test with proper coroutine context
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun runTestWithDispatcher(
        dispatcher: TestDispatcher = createTestDispatcher(),
        testBody: suspend () -> Unit
    ) = runTest(dispatcher) {
        testBody()
    }
    
    /**
     * Performance testing helper
     */
    inline fun measureExecutionTime(block: () -> Unit): Long {
        val startTime = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - startTime
    }
    
    /**
     * Performance assertion helper
     */
    fun assertExecutionTimeUnder(expectedMaxTimeMs: Long, block: () -> Unit) {
        val actualTime = measureExecutionTime(block)
        if (actualTime > expectedMaxTimeMs) {
            throw AssertionError(
                "Expected execution time to be under ${expectedMaxTimeMs}ms, but was ${actualTime}ms"
            )
        }
    }
    
    /**
     * Memory testing helper
     */
    fun measureMemoryUsage(): Long {
        System.gc() // Suggest garbage collection
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory()
    }
}

/**
 * JUnit rule for managing test coroutine dispatcher
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestRule(
    val testDispatcher: TestDispatcher = TestUtils.createTestDispatcher()
) : TestRule {
    
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                Dispatchers.setMain(testDispatcher)
                try {
                    base.evaluate()
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }
    }
}

/**
 * Performance testing rule
 */
class PerformanceTestRule(
    private val maxExecutionTimeMs: Long = 5000L
) : TestRule {
    
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val executionTime = TestUtils.measureExecutionTime {
                    base.evaluate()
                }
                
                if (executionTime > maxExecutionTimeMs) {
                    throw AssertionError(
                        "Test '${description.methodName}' exceeded maximum execution time. " +
                        "Expected: <${maxExecutionTimeMs}ms, Actual: ${executionTime}ms"
                    )
                }
                
                println("⏱️ Test '${description.methodName}' completed in ${executionTime}ms")
            }
        }
    }
}