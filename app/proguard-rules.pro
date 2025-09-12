# GitHub Users App - Advanced ProGuard/R8 Rules

# =============================================================================
# CORE ANDROID COMPONENTS
# =============================================================================

# Preserve the class names for debugging purposes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep all Activities, Services, and BroadcastReceivers
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# Preserve application class and main entry points
-keep public class com.example.githubusers.UserApplication {
    public static void main(java.lang.String[]);
}

# =============================================================================
# DEPENDENCY INJECTION - HILT
# =============================================================================

# Hilt - Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }
-keep class **_HiltComponents$* { *; }
-keep class **_Impl { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Keep Hilt entry points and modules
-keep @dagger.hilt.InstallIn class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.Module class *

# =============================================================================
# DATABASE - ROOM
# =============================================================================

# Room Database - Keep entity classes and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keep class * implements androidx.room.TypeConverter
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public abstract *;
}

# =============================================================================
# NETWORKING - KTOR & OKHTTP
# =============================================================================

# Ktor - Keep serialization classes
-keep @kotlinx.serialization.Serializable class * {
    <fields>;
}
-keepclassmembers,allowobfuscation class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Ktor HttpClient
-keep class io.ktor.client.** { *; }
-keep class io.ktor.http.** { *; }
-keep class io.ktor.util.** { *; }
-keep class io.ktor.serialization.** { *; }

# OkHttp and networking
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# =============================================================================
# UI FRAMEWORK - COMPOSE
# =============================================================================

# Compose - Keep Composable functions and related classes
-keep class androidx.compose.** { *; }
-keep @androidx.compose.runtime.Composable class *
-keep @androidx.compose.runtime.Stable class *

# Keep Compose compiler generated classes
-keep class **.*ComposableSingletons { *; }
-keep class **.*LiveLiterals$* { *; }

# =============================================================================
# ARCHITECTURE PATTERNS - MVI
# =============================================================================

# MVI Pattern - Keep state and intent classes
-keep class **.*State { *; }
-keep class **.*Intent { *; }
-keep class **.*Effect { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# =============================================================================
# NAVIGATION & PAGING
# =============================================================================

# Navigation 3 - Keep navigation components
-keep class androidx.navigation3.** { *; }

# Paging 3 - Keep paging related classes
-keep class androidx.paging.** { *; }
-keep class * implements androidx.paging.PagingSource

# =============================================================================
# PERFORMANCE MONITORING
# =============================================================================

# Performance monitoring - Keep performance classes from being obfuscated
-keep class com.example.githubusers.performance.** { *; }
-keep class com.example.githubusers.core.ui.performance.** { *; }

# =============================================================================
# COROUTINES & THREADING
# =============================================================================

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# =============================================================================
# IMAGE LOADING - COIL
# =============================================================================

# Coil Image Loading
-keep class coil.** { *; }
-dontwarn coil.**

# =============================================================================
# LOGGING FRAMEWORKS
# =============================================================================

# Keep SLF4J binding
-dontwarn org.slf4j.**
-keep class org.slf4j.** { *; }

# Keep the StaticLoggerBinder class to prevent R8 from stripping it out
-keep class org.slf4j.impl.StaticLoggerBinder { *; }

# Keep any logging frameworks you are using, e.g., Logback
-keep class ch.qos.logback.** { *; }
-keep class ch.qos.logback.classic.** { *; }
-keep class ch.qos.logback.core.** { *; }
-keepclassmembers class ch.qos.logback.** { *; }

# =============================================================================
# GENERIC RULES
# =============================================================================

# Preserve all annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Generic rules for reflection and serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom exceptions
-keep public class * extends java.lang.Exception

# =============================================================================
# OPTIMIZATION & LOGGING REMOVAL
# =============================================================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class timber.log.Timber {
    public static *** tag(...);
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Optimization settings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 3
-allowaccessmodification
-repackageclasses ''

# Remove debugging information in release
-renamesourcefileattribute SourceFile
