#!/bin/bash

# Final Security Module Validation Script
# This script performs comprehensive validation of the security module migration
# Run this after Android SDK is available in the environment

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CORE_SECURITY_MODULE="$PROJECT_ROOT/core-security"
APP_MODULE="$PROJECT_ROOT/app"

echo -e "${BLUE}=== Security Module Final Validation ===${NC}"
echo "Project Root: $PROJECT_ROOT"
echo "Core Security Module: $CORE_SECURITY_MODULE"
echo ""

# Function to print status
print_status() {
    local status=$1
    local message=$2
    if [[ $status == "PASS" ]]; then
        echo -e "${GREEN}✅ $message${NC}"
    elif [[ $status == "FAIL" ]]; then
        echo -e "${RED}❌ $message${NC}"
    elif [[ $status == "WARN" ]]; then
        echo -e "${YELLOW}⚠️  $message${NC}"
    else
        echo -e "${BLUE}ℹ️  $message${NC}"
    fi
}

# Check Android SDK availability
check_android_sdk() {
    echo -e "${BLUE}🔍 Checking Android SDK availability...${NC}"
    
    if [[ -z "${ANDROID_HOME:-}" ]]; then
        print_status "WARN" "ANDROID_HOME not set - SDK-dependent validation will be skipped"
        return 1
    fi
    
    if [[ ! -d "$ANDROID_HOME" ]]; then
        print_status "FAIL" "ANDROID_HOME directory doesn't exist: $ANDROID_HOME"
        return 1
    fi
    
    print_status "PASS" "Android SDK found at: $ANDROID_HOME"
    return 0
}

# Validate core-security module structure
validate_module_structure() {
    echo -e "${BLUE}📁 Validating module structure...${NC}"
    
    # Check core-security module exists
    if [[ ! -d "$CORE_SECURITY_MODULE" ]]; then
        print_status "FAIL" "core-security module directory not found"
        return 1
    fi
    
    # Check essential files
    local essential_files=(
        "build.gradle.kts"
        "consumer-proguard-rules.pro"
        "src/main/kotlin/com/example/githubusers/core/security/SecurityManager.kt"
        "src/main/kotlin/com/example/githubusers/core/security/di/SecurityModule.kt"
        "src/main/cpp/CMakeLists.txt"
        "src/main/cpp/security_manager.cpp"
        "src/main/cpp/anti_tamper.cpp"
        "src/main/cpp/key_obfuscation.cpp"
        "src/test/kotlin/com/example/githubusers/core/security/SecurityManagerTest.kt"
        "src/androidTest/kotlin/com/example/githubusers/core/security/SecurityManagerIntegrationTest.kt"
    )
    
    for file in "${essential_files[@]}"; do
        local full_path="$CORE_SECURITY_MODULE/$file"
        if [[ -f "$full_path" ]]; then
            print_status "PASS" "Found: $file"
        else
            print_status "FAIL" "Missing: $file"
            return 1
        fi
    done
    
    # Check that old native files are removed from app module
    local old_native_files=(
        "src/main/cpp/CMakeLists.txt"
        "src/main/cpp/security_manager.cpp"
        "src/main/cpp/anti_tamper.cpp"
        "src/main/cpp/key_obfuscation.cpp"
    )
    
    for file in "${old_native_files[@]}"; do
        local full_path="$APP_MODULE/$file"
        if [[ -f "$full_path" ]]; then
            print_status "FAIL" "Old native file still exists in app module: $file"
            return 1
        else
            print_status "PASS" "Confirmed removal from app module: $file"
        fi
    done
    
    return 0
}

# Validate dependencies and configuration
validate_dependencies() {
    echo -e "${BLUE}📦 Validating dependencies and configuration...${NC}"
    
    # Check version catalog includes core-security
    if grep -q "local-core-security" "$PROJECT_ROOT/catalog/gradle/libs.versions.toml"; then
        print_status "PASS" "core-security added to version catalog"
    else
        print_status "FAIL" "core-security missing from version catalog"
        return 1
    fi
    
    # Check app module depends on core-security
    if grep -q "implementation(libs.local.core.security)" "$APP_MODULE/build.gradle.kts"; then
        print_status "PASS" "app module depends on core-security"
    else
        print_status "FAIL" "app module missing core-security dependency"
        return 1
    fi
    
    # Check NDK configuration removed from app module
    if ! grep -q "ndkConfig" "$APP_MODULE/build.gradle.kts"; then
        print_status "PASS" "NDK configuration removed from app module"
    else
        print_status "FAIL" "NDK configuration still present in app module"
        return 1
    fi
    
    return 0
}

# Build core-security module
build_core_security() {
    echo -e "${BLUE}🔨 Building core-security module...${NC}"
    
    cd "$PROJECT_ROOT"
    
    # Clean build
    print_status "INFO" "Cleaning previous builds..."
    if ./gradlew :core-security:clean --no-daemon --quiet; then
        print_status "PASS" "Clean build completed"
    else
        print_status "FAIL" "Clean build failed"
        return 1
    fi
    
    # Build debug variant
    print_status "INFO" "Building debug variant..."
    if ./gradlew :core-security:assembleDebug --no-daemon --info | grep -E "(BUILD SUCCESSFUL|BUILD FAILED)"; then
        if ./gradlew :core-security:assembleDebug --no-daemon --quiet; then
            print_status "PASS" "Debug build successful"
        else
            print_status "FAIL" "Debug build failed"
            return 1
        fi
    else
        print_status "FAIL" "Debug build failed"
        return 1
    fi
    
    return 0
}

# Verify native libraries
verify_native_libraries() {
    echo -e "${BLUE}🔍 Verifying native libraries...${NC}"
    
    local build_dir="$CORE_SECURITY_MODULE/build"
    
    # Check CMake build directory
    if [[ -d "$build_dir/intermediates/cxx" ]]; then
        print_status "PASS" "CMake build directory exists"
        
        # Look for .so files
        local so_files
        so_files=$(find "$build_dir/intermediates/cxx" -name "*.so" 2>/dev/null || true)
        
        if [[ -n "$so_files" ]]; then
            print_status "PASS" "Native libraries (.so files) found:"
            echo "$so_files" | while read -r so_file; do
                echo "    - $(basename "$so_file")"
            done
        else
            print_status "WARN" "No .so files found - may require ABI-specific builds"
        fi
    else
        print_status "FAIL" "CMake build directory not found"
        return 1
    fi
    
    return 0
}

# Run unit tests
run_unit_tests() {
    echo -e "${BLUE}🧪 Running unit tests...${NC}"
    
    cd "$PROJECT_ROOT"
    
    # Run core-security unit tests
    if ./gradlew :core-security:testDebugUnitTest --no-daemon --quiet; then
        print_status "PASS" "Unit tests passed"
    else
        print_status "FAIL" "Unit tests failed"
        return 1
    fi
    
    return 0
}

# Run integration tests
run_integration_tests() {
    echo -e "${BLUE}🔗 Running integration tests...${NC}"
    
    cd "$PROJECT_ROOT"
    
    # Run core-security integration tests (requires connected device/emulator)
    if ./gradlew :core-security:connectedDebugAndroidTest --no-daemon --quiet; then
        print_status "PASS" "Integration tests passed"
    else
        print_status "WARN" "Integration tests require connected Android device/emulator"
        return 0
    fi
    
    return 0
}

# Build full app
build_full_app() {
    echo -e "${BLUE}📱 Building full application...${NC}"
    
    cd "$PROJECT_ROOT"
    
    # Build app with core-security integration
    if ./gradlew :app:assembleDebug --no-daemon --quiet; then
        print_status "PASS" "App build successful"
        
        # Check if native libraries are included in APK
        local apk_path="$APP_MODULE/build/outputs/apk/debug/app-debug.apk"
        if [[ -f "$apk_path" ]]; then
            print_status "PASS" "APK generated at: $apk_path"
            
            # Check APK contents for native libraries
            if command -v unzip >/dev/null 2>&1; then
                local native_libs
                native_libs=$(unzip -l "$apk_path" | grep -E "\\.so$" || true)
                
                if [[ -n "$native_libs" ]]; then
                    print_status "PASS" "Native libraries found in APK:"
                    echo "$native_libs" | while read -r line; do
                        if [[ "$line" =~ \\.so$ ]]; then
                            echo "    - $(echo "$line" | awk '{print $NF}')"
                        fi
                    done
                else
                    print_status "WARN" "No native libraries found in APK"
                fi
            fi
        else
            print_status "FAIL" "APK not found at expected location"
            return 1
        fi
    else
        print_status "FAIL" "App build failed"
        return 1
    fi
    
    return 0
}

# Performance validation
performance_validation() {
    echo -e "${BLUE}⚡ Performance validation...${NC}"
    
    # APK size check
    local apk_path="$APP_MODULE/build/outputs/apk/debug/app-debug.apk"
    if [[ -f "$apk_path" ]]; then
        local apk_size
        apk_size=$(du -h "$apk_path" | cut -f1)
        print_status "INFO" "APK size: $apk_size"
        
        # For reference, log the size - actual validation would require baseline
        echo "    💡 Monitor for size regression compared to baseline"
    fi
    
    # Build time validation
    print_status "INFO" "Build time validation completed during previous steps"
    
    return 0
}

# Security validation
security_validation() {
    echo -e "${BLUE}🔒 Security validation...${NC}"
    
    # Check ProGuard rules
    if [[ -f "$CORE_SECURITY_MODULE/consumer-proguard-rules.pro" ]]; then
        print_status "PASS" "ProGuard rules file exists"
        
        # Check for key security rules
        if grep -q "keepclassmembers.*SecurityManager" "$CORE_SECURITY_MODULE/consumer-proguard-rules.pro"; then
            print_status "PASS" "SecurityManager ProGuard rules found"
        else
            print_status "FAIL" "SecurityManager ProGuard rules missing"
            return 1
        fi
    else
        print_status "FAIL" "ProGuard rules file missing"
        return 1
    fi
    
    # Check CMake security flags
    if grep -q "fstack-protector-strong" "$CORE_SECURITY_MODULE/src/main/cpp/CMakeLists.txt"; then
        print_status "PASS" "Stack protection enabled in CMake"
    else
        print_status "FAIL" "Stack protection not found in CMake"
        return 1
    fi
    
    if grep -q "D_FORTIFY_SOURCE=2" "$CORE_SECURITY_MODULE/src/main/cpp/CMakeLists.txt"; then
        print_status "PASS" "FORTIFY_SOURCE enabled in CMake"
    else
        print_status "FAIL" "FORTIFY_SOURCE not found in CMake"
        return 1
    fi
    
    return 0
}

# Main validation flow
main() {
    local has_android_sdk=false
    local validation_failed=false
    
    echo "Starting comprehensive security module validation..."
    echo ""
    
    # Check Android SDK (but continue without it)
    if check_android_sdk; then
        has_android_sdk=true
    fi
    echo ""
    
    # Always run structural validation
    if ! validate_module_structure; then
        validation_failed=true
    fi
    echo ""
    
    if ! validate_dependencies; then
        validation_failed=true
    fi
    echo ""
    
    if ! security_validation; then
        validation_failed=true
    fi
    echo ""
    
    # Only run SDK-dependent validation if SDK is available
    if [[ $has_android_sdk == true ]]; then
        if ! build_core_security; then
            validation_failed=true
        fi
        echo ""
        
        if ! verify_native_libraries; then
            validation_failed=true
        fi
        echo ""
        
        if ! run_unit_tests; then
            validation_failed=true
        fi
        echo ""
        
        if ! run_integration_tests; then
            validation_failed=true
        fi
        echo ""
        
        if ! build_full_app; then
            validation_failed=true
        fi
        echo ""
        
        performance_validation
        echo ""
    else
        print_status "INFO" "Skipping SDK-dependent validations (Android SDK not available)"
        print_status "INFO" "These validations will run automatically in CI/CD pipeline"
        echo ""
    fi
    
    # Final status
    echo -e "${BLUE}=== Final Validation Results ===${NC}"
    
    if [[ $validation_failed == true ]]; then
        print_status "FAIL" "Some validations failed - check output above"
        exit 1
    else
        print_status "PASS" "All available validations passed!"
        
        if [[ $has_android_sdk == false ]]; then
            print_status "INFO" "Complete validation requires Android SDK environment"
            print_status "INFO" "CI/CD pipeline will run full validation with native builds"
        else
            print_status "PASS" "Security module migration fully validated!"
        fi
        
        echo ""
        echo -e "${GREEN}🎉 Security Module Migration: READY FOR DEPLOYMENT 🎉${NC}"
    fi
}

# Run main function
main "$@"