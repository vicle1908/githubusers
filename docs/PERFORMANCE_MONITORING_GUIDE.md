# Performance Monitoring Guide

## Overview

This project uses Firebase Performance Monitoring for production performance tracking, replacing custom performance monitoring solutions with industry-standard tools.

## Architecture

### Firebase Performance Monitoring

- **Production Ready**: Industry-standard performance monitoring solution
- **Automatic Tracking**: Tracks app startup, screen rendering, network requests automatically
- **Custom Traces**: Supports custom performance traces for specific operations
- **Real-time Insights**: Provides real-time performance data in Firebase Console
- **Crash-free Users**: Tracks performance impact on user experience

### Plugin-based Configuration

Firebase Performance Monitoring is configured through our custom convention plugin:

```kotlin
// In app/build.gradle.kts
plugins {
    id("githubusers.firebase.performance")
}
```

This plugin:
- Applies Firebase Performance Monitoring and Google Services plugins
- Adds Firebase BOM and Performance Monitoring dependencies
- Configures build types with appropriate performance monitoring settings
- Provides consistent setup across all modules

## Implementation

### Application Configuration

```kotlin
@HiltAndroidApp
class UserApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Firebase Performance Monitoring
        val firebasePerformance = FirebasePerformance.getInstance()
        
        // Enable based on build type
        val isEnabled = BuildConfig.FIREBASE_PERF_ENABLED
        firebasePerformance.isPerformanceCollectionEnabled = isEnabled
    }
}
```

### Build Configuration

The plugin automatically configures build types:

```kotlin
buildTypes {
    debug {
        buildConfigField("boolean", "FIREBASE_PERF_ENABLED", "true")
    }
    release {
        buildConfigField("boolean", "FIREBASE_PERF_ENABLED", "true")
    }
    benchmark {
        buildConfigField("boolean", "FIREBASE_PERF_ENABLED", "true")
    }
}
```

## What Firebase Performance Monitoring Tracks

### Automatic Metrics

1. **App Startup Time**
   - Cold start time
   - Warm start time
   - Hot start time

2. **Screen Performance**
   - Screen rendering time
   - Screen load time
   - Frame rendering performance

3. **Network Performance**
   - HTTP request/response times
   - Network success/failure rates
   - Data transfer sizes

4. **Custom Traces**
   - Navigation performance
   - Database operations
   - Image loading
   - Custom business logic

### Custom Traces (Optional)

```kotlin
// Example: Custom trace for navigation
val trace = FirebasePerformance.getInstance().newTrace("navigation_trace")
trace.start()

// Perform navigation operation
navigateToDestination()

trace.stop()
```

## Development vs Production

### Development
- Firebase Performance Monitoring enabled in debug builds
- Local logging for development insights
- No impact on production metrics

### Production
- Firebase Performance Monitoring enabled in release builds
- Real-time performance data in Firebase Console
- Automatic crash-free user tracking
- Performance alerts and monitoring

## Migration from Custom Solutions

### Removed Components

1. **StartupPerformanceTracker.kt**
   - Replaced by Firebase Performance Monitoring automatic startup tracking
   - No custom startup timing needed

2. **NavigationPerformanceMonitor.kt**
   - Replaced by Firebase Performance Monitoring custom traces
   - More comprehensive and production-ready

### Retained Components

1. **core-ui/PerformanceMonitor.kt**
   - Kept for development-time UI performance tracking
   - Complements Firebase Performance Monitoring
   - Focuses on Compose-specific metrics

## Best Practices

### 1. Use Firebase Performance Monitoring for Production
- Industry-standard solution
- Automatic tracking of key metrics
- Real-time insights and alerts
- Crash-free user tracking

### 2. Keep Development Tools for Local Insights
- Use `core-ui/PerformanceMonitor.kt` for development
- Local logging for immediate feedback
- Compose-specific performance tracking

### 3. Custom Traces for Business Logic
- Add custom traces for critical operations
- Track navigation performance
- Monitor database operations
- Measure image loading performance

### 4. Build Type Configuration
- Enable in all build types for comprehensive monitoring
- Use build config fields for conditional enabling
- Separate development and production insights

## Firebase Console

### Accessing Performance Data

1. **Firebase Console**: https://console.firebase.google.com
2. **Select Project**: githubusers-demo
3. **Performance Tab**: View real-time performance data
4. **Custom Traces**: Monitor custom performance traces
5. **Network Requests**: Track API performance
6. **Screen Performance**: Monitor UI rendering

### Key Metrics to Monitor

- **App Startup Time**: Target < 1.5s for cold start
- **Screen Load Time**: Target < 200ms for screen rendering
- **Network Response Time**: Target < 500ms for API calls
- **Crash-free Users**: Target > 99.5% crash-free users

## Troubleshooting

### Common Issues

1. **Firebase Not Initialized**
   - Ensure `google-services.json` is present
   - Check Firebase project configuration
   - Verify Google Services plugin is applied

2. **Performance Data Not Appearing**
   - Check Firebase Performance Monitoring is enabled
   - Verify build type configuration
   - Wait for data propagation (can take up to 24 hours)

3. **Build Errors**
   - Ensure Firebase plugins are properly configured
   - Check version catalog dependencies
   - Verify plugin registration

### Debug Logging

```kotlin
// Enable debug logging for Firebase Performance
FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
Timber.tag("FirebasePerf").d("Performance monitoring enabled")
```

## Future Enhancements

### Planned Features

1. **Custom Metrics**
   - Navigation performance traces
   - Database operation monitoring
   - Image loading performance

2. **Performance Alerts**
   - Automatic alerts for performance degradation
   - Custom thresholds for key metrics
   - Integration with monitoring systems

3. **A/B Testing Integration**
   - Performance impact of feature flags
   - User experience optimization
   - Performance-based feature rollouts

## Conclusion

Firebase Performance Monitoring provides a comprehensive, production-ready solution for performance tracking. The plugin-based configuration ensures consistent setup across all modules while maintaining the flexibility to add custom traces for specific business logic.

The migration from custom performance monitoring solutions to Firebase Performance Monitoring improves maintainability, provides better insights, and follows industry best practices for Android performance monitoring.
