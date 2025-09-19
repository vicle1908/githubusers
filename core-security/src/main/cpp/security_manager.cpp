#include <jni.h>
#include <android/log.h>
#include <string>
#include "anti_tamper.h"
#include "key_obfuscation.h"

#ifndef LOG_TAG
#define LOG_TAG "SecurityCore"
#endif

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// JNI method implementations
static jint nativeVerifyIntegrity(JNIEnv* env, jobject /*thiz*/) {
    LOGI("Starting integrity verification");
    
    // Check for device compromise (root, debugging, etc.)
    if (isDeviceCompromised()) {
        LOGE("Integrity check failed: device compromised");
        return -1001; // Device compromised
    }
    
    if (isDebuggingDetected()) {
        LOGE("Integrity check failed: debugging detected");
        return -1002; // Debugging detected
    }
    
    LOGI("Integrity verification passed");
    return 0; // Success
}

static jstring nativeGetObfuscatedKey(JNIEnv* env, jobject /*thiz*/, jint keyId) {
    LOGI("Getting obfuscated key for ID: %d", keyId);
    
    // Get base seed from existing key obfuscation system
    std::string keySeed = getObfuscatedKeySeed();
    if (keySeed.empty()) {
        LOGE("Failed to get key seed");
        return nullptr;
    }
    
    // Simple key derivation based on keyId (can be enhanced)
    std::string derivedKey = keySeed;
    for (int i = 0; i < keyId; ++i) {
        // Simple hash-like derivation
        for (char& c : derivedKey) {
            c ^= (keyId + i) & 0xFF;
        }
    }
    
    if (derivedKey.empty()) {
        LOGE("Key derivation failed for ID: %d", keyId);
        return nullptr;
    }
    
    LOGI("Successfully derived key for ID: %d", keyId);
    return env->NewStringUTF(derivedKey.c_str());
}

// JNI method registration table
static JNINativeMethod methods[] = {
    {"nativeVerifyIntegrity", "()I", (void*)nativeVerifyIntegrity},
    {"nativeGetObfuscatedKey", "(I)Ljava/lang/String;", (void*)nativeGetObfuscatedKey}
};

// JNI_OnLoad implementation with RegisterNatives
extern "C" jint JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGE("Failed to get JNI environment");
        return JNI_ERR;
    }
    
    // Find the SecurityManager class (adjust package if needed)
    jclass securityClass = env->FindClass("com/example/githubusers/core/security/SecurityManager");
    if (!securityClass) {
        LOGE("Failed to find SecurityManager class");
        return JNI_ERR;
    }
    
    // Register native methods
    if (env->RegisterNatives(securityClass, methods, sizeof(methods)/sizeof(methods[0])) < 0) {
        LOGE("Failed to register native methods");
        return JNI_ERR;
    }
    
    LOGI("JNI_OnLoad: security_native loaded successfully with RegisterNatives");
    return JNI_VERSION_1_6;
}