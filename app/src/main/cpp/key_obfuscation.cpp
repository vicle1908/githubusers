#include <string>
#include <algorithm>
#include <random>
#include <chrono>
#include <android/log.h>

#ifndef LOG_TAG
#define LOG_TAG "KeyObfuscation"
#endif

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

namespace KeyObfuscation {

    // Multiple obfuscated seed components
    // In production, these should be generated uniquely per build
    constexpr uint8_t SEED_PART_A[] = {
        0x53 ^ 0xA7, 0x65 ^ 0xA7, 0x63 ^ 0xA7, 0x75 ^ 0xA7,
        0x72 ^ 0xA7, 0x69 ^ 0xA7, 0x74 ^ 0xA7, 0x79 ^ 0xA7
    };

    constexpr uint8_t SEED_PART_B[] = {
        0x4B ^ 0x5F, 0x65 ^ 0x5F, 0x79 ^ 0x5F, 0x53 ^ 0x5F,
        0x65 ^ 0x5F, 0x65 ^ 0x5F, 0x64 ^ 0x5F, 0x32 ^ 0x5F
    };

    constexpr uint8_t SEED_PART_C[] = {
        0x30 ^ 0x92, 0x32 ^ 0x92, 0x34 ^ 0x92, 0x5F ^ 0x92,
        0x48 ^ 0x92, 0x61 ^ 0x92, 0x73 ^ 0x92, 0x68 ^ 0x92
    };

    // Dynamic obfuscation using multiple techniques
    std::string applyDynamicObfuscation(const std::string& input) {
        std::string result = input;
        
        // Simple Caesar cipher with time-based shift
        auto now = std::chrono::high_resolution_clock::now();
        auto timeValue = std::chrono::duration_cast<std::chrono::milliseconds>(
            now.time_since_epoch()).count();
        
        uint8_t shift = static_cast<uint8_t>(timeValue % 256);
        
        for (char& c : result) {
            c ^= shift;
        }
        
        // Reverse the string as additional obfuscation
        std::reverse(result.begin(), result.end());
        
        return result;
    }

    // Deobfuscate a seed component
    std::string deobfuscateSeedPart(const uint8_t* obfuscated, size_t length, uint8_t key) {
        std::string result;
        result.reserve(length);
        
        for (size_t i = 0; i < length; ++i) {
            result += static_cast<char>(obfuscated[i] ^ key);
        }
        
        return result;
    }

    // Mix multiple seed parts with additional entropy
    std::string combineSeedParts() {
        std::string combined;
        
        // Deobfuscate each part
        std::string partA = deobfuscateSeedPart(SEED_PART_A, sizeof(SEED_PART_A), 0xA7);
        std::string partB = deobfuscateSeedPart(SEED_PART_B, sizeof(SEED_PART_B), 0x5F);
        std::string partC = deobfuscateSeedPart(SEED_PART_C, sizeof(SEED_PART_C), 0x92);
        
        // Interleave the parts for better distribution
        size_t maxLen = std::max({partA.length(), partB.length(), partC.length()});
        
        for (size_t i = 0; i < maxLen; ++i) {
            if (i < partA.length()) combined += partA[i];
            if (i < partB.length()) combined += partB[i];
            if (i < partC.length()) combined += partC[i];
        }
        
        return combined;
    }

} // namespace KeyObfuscation

// External interface for security manager
std::string getObfuscatedKeySeed() {
    std::string seed = KeyObfuscation::combineSeedParts();

    if (seed.empty()) {
        LOGD("Warning: Empty key seed generated");
        return "";
    }

    // FIX: Return the combined seed directly without dynamic (time-based) obfuscation.
    // This makes the seed deterministic.
    LOGD("Deterministic key seed generated successfully");
    return seed;
}