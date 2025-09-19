#include <string>
#include <fstream>
#include <android/log.h>
#include <unistd.h>
#include <sys/stat.h>
#include <cstring>
#include <dirent.h>

#ifndef LOG_TAG
#define LOG_TAG "AntiTamper"
#endif

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace AntiTamper {

    // Common root detection paths
    const char* COMMON_ROOT_PATHS[] = {
        "/system/app/Superuser.apk",
        "/system/app/su",
        "/system/bin/su", 
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/sbin/su",
        "/su/bin/su",
        "/system/app/SuperSU.apk",
        "/system/app/Kinguser.apk",
        "/system/app/KingoUser.apk"
    };

    // Common root management apps
    const char* ROOT_APPS[] = {
        "com.noshufou.android.su",
        "com.noshufou.android.su.elite", 
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "com.yellowes.su",
        "com.topjohnwu.magisk",
        "com.kingroot.kinguser",
        "com.kingo.root",
        "com.smedialink.oneclickroot",
        "com.zhiqupk.root.global",
        "com.alephzain.framaroot"
    };

    // Debugging detection paths
    const char* DEBUG_PATHS[] = {
        "/proc/self/status",
        "/proc/self/stat",
        "/proc/self/task"
    };

    bool checkFileExists(const char* path) {
        struct stat buffer;
        return (stat(path, &buffer) == 0);
    }

    bool checkRootBinaries() {
        for (const char* path : COMMON_ROOT_PATHS) {
            if (checkFileExists(path)) {
                LOGE("Root binary detected: %s", path);
                return true;
            }
        }
        return false;
    }

    bool checkRootProperties() {
        // Check build properties that may indicate root
        std::ifstream propFile("/system/build.prop");
        if (propFile.is_open()) {
            std::string line;
            while (std::getline(propFile, line)) {
                if (line.find("ro.build.tags=test-keys") != std::string::npos ||
                    line.find("ro.debuggable=1") != std::string::npos) {
                    LOGE("Suspicious build property detected: %s", line.c_str());
                    propFile.close();
                    return true;
                }
            }
            propFile.close();
        }
        return false;
    }

    bool checkSuCommand() {
        // Try to execute su command
        FILE* pipe = popen("which su", "r");
        if (pipe) {
            char buffer[256];
            if (fgets(buffer, sizeof(buffer), pipe) != nullptr) {
                pclose(pipe);
                LOGE("su command found: %s", buffer);
                return true;
            }
            pclose(pipe);
        }
        return false;
    }

    bool checkInstalledPackages() {
        // Check for installed root management packages
        DIR* dir = opendir("/data/data/");
        if (dir) {
            struct dirent* entry;
            while ((entry = readdir(dir)) != nullptr) {
                if (entry->d_type == DT_DIR) {
                    for (const char* rootApp : ROOT_APPS) {
                        if (strstr(entry->d_name, rootApp) != nullptr) {
                            LOGE("Root app package detected: %s", entry->d_name);
                            closedir(dir);
                            return true;
                        }
                    }
                }
            }
            closedir(dir);
        }
        return false;
    }

    bool checkForDebugging() {
        // Check if process is being traced/debugged
        std::ifstream statusFile("/proc/self/status");
        if (statusFile.is_open()) {
            std::string line;
            while (std::getline(statusFile, line)) {
                if (line.find("TracerPid:") != std::string::npos) {
                    // Extract the tracer PID
                    size_t pos = line.find("TracerPid:");
                    if (pos != std::string::npos) {
                        std::string pidStr = line.substr(pos + 10);
                        int tracerPid = std::stoi(pidStr);
                        if (tracerPid != 0) {
                            LOGE("Debugger detected - TracerPid: %d", tracerPid);
                            statusFile.close();
                            return true;
                        }
                    }
                }
            }
            statusFile.close();
        }
        
        // Additional check for debug environment
        if (getppid() == 1) {
            // Normal case - parent should be zygote or init
            return false;
        }
        
        return false;
    }

    bool checkEmulatorEnvironment() {
        // Check for common emulator characteristics
        std::ifstream cpuInfo("/proc/cpuinfo");
        if (cpuInfo.is_open()) {
            std::string line;
            while (std::getline(cpuInfo, line)) {
                if (line.find("goldfish") != std::string::npos ||
                    line.find("ranchu") != std::string::npos ||
                    line.find("vbox") != std::string::npos) {
                    LOGE("Emulator environment detected: %s", line.c_str());
                    cpuInfo.close();
                    return true;
                }
            }
            cpuInfo.close();
        }
        
        // Check system properties for emulator signs
        if (checkFileExists("/system/lib/libc_malloc_debug_qemu.so") ||
            checkFileExists("/dev/socket/qemud") ||
            checkFileExists("/system/bin/qemu-props")) {
            LOGE("Emulator files detected");
            return true;
        }
        
        return false;
    }

} // namespace AntiTamper

// External interface functions
bool isDeviceCompromised() {
    bool compromised = false;
    
    // Check for root access
    if (AntiTamper::checkRootBinaries()) {
        LOGE("Device compromised: Root binaries detected");
        compromised = true;
    }
    
    if (AntiTamper::checkRootProperties()) {
        LOGE("Device compromised: Suspicious build properties");
        compromised = true;
    }
    
    if (AntiTamper::checkSuCommand()) {
        LOGE("Device compromised: su command available");
        compromised = true;
    }
    
    if (AntiTamper::checkInstalledPackages()) {
        LOGE("Device compromised: Root management apps installed");
        compromised = true;
    }
    
    // Check for emulator (optional - may be legitimate)
    if (AntiTamper::checkEmulatorEnvironment()) {
        LOGD("Running on emulator - flagging as potentially compromised");
        compromised = true;
    }
    
    return compromised;
}

bool isDebuggingDetected() {
    return AntiTamper::checkForDebugging();
}