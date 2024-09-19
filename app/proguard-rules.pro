# Preserve the class names for debugging purposes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Prevent obfuscation of the following classes
#-keep class com.example.githubusers.** { *; }

# Preserve all annotations
-keepattributes *Annotation*

# Optimize code
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Keep SLF4J binding
-dontwarn org.slf4j.**
-keep class org.slf4j.** { *; }

# Keep the StaticLoggerBinder class to prevent R8 from stripping it out
-keep class org.slf4j.impl.StaticLoggerBinder { *; }

# Keep any logging frameworks you are using, e.g., Logback
# This assumes you are using Logback. If using a different implementation, adjust accordingly.
-keep class ch.qos.logback.** { *; }

# Keep Logback's configuration classes if applicable
-keep class ch.qos.logback.classic.** { *; }
-keep class ch.qos.logback.core.** { *; }

# Keep Logback initialization methods
-keepclassmembers class ch.qos.logback.** { *; }
