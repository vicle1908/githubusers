#ifndef KEY_OBFUSCATION_H
#define KEY_OBFUSCATION_H

#include \u003cvector\u003e
#include \u003ccstdint\u003e

// Apply key obfuscation
std::vector\u003cuint8_t\u003e obfuscateKey(const uint8_t* key, size_t length);

// Reverse key obfuscation
std::vector\u003cuint8_t\u003e deobfuscateKey(const uint8_t* obfuscatedKey, size_t length);

#endif // KEY_OBFUSCATION_H
