#pragma once

// Anti-tamper and security verification functions

// Check if device is compromised (root access, debug environment, etc.)
bool isDeviceCompromised();

// Detect if debugging/tracing is active
bool isDebuggingDetected();
