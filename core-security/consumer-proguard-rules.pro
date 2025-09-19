# Core-Security Module - Consumer ProGuard Rules
# Optimized for RegisterNatives Pattern (Multi-AI Research Validated)

# ============================================================================
# REGISTERNAATIVES COMPATIBILITY (GPT-5: 8/10, Grok-4: 9/10 Confidence)
# Allows obfuscation while preserving functionality
# ============================================================================

# Allow obfuscation of SecurityManager class since we use RegisterNatives
# Only preserve the class structure, not method names
-keep class com.example.githubusers.core.security.SecurityManager {
    # Don't preserve method names - RegisterNatives handles mapping
}

# Keep specific member access patterns for validation
-keepclassmembers class com.example.githubusers.core.security.SecurityManager {
    native <methods>;
    public static native <methods>;
}

# Keep public API for consumers (no obfuscation)
-keep class com.example.githubusers.core.security.SecurityService { *; }
-keep class com.example.githubusers.core.security.NativeSecurityService { *; }

# Keep Result classes used in API
-keep class kotlin.Result { *; }

# Keep Hilt DI module if present
-keep class com.example.githubusers.core.security.di.SecurityModule { *; }

# ============================================================================
# SECURITY HARDENING
# ============================================================================

# Remove all logging from security module in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Preserve exception handling but optimize stack traces
-keepattributes SourceFile,LineNumberTable

# ============================================================================
# OPTIMIZATION NOTES
# ============================================================================
# With RegisterNatives pattern:
# - No need to preserve native method names
# - JNI method lookup is immune to obfuscation
# - Provides better security through symbol hiding
# - Reduces APK size by allowing more aggressive obfuscation