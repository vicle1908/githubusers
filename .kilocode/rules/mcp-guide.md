---
alwaysApply: true
---

# MCP Server Usage Guide and Enforcement

## Overview

This rule enforces the proper use of Model Context Protocol (MCP) servers for different development tasks. It ensures consistent, efficient, and secure usage of MCP servers for codebase operations, build management, Android development, and external service integration.

## 🚀 **Current MCP Server Status Summary**

### ✅ **FULLY OPERATIONAL** (10/10)

- **Claude Context MCP Server**: Code search and indexing (307 files, 693 chunks)
- **Gradle MCP Server**: Build operations and project management
- **OpenMemory MCP Server**: Persistent memory storage and retrieval (1130+ memories)
- **Android MCP Server**: ADB operations and device management
- **Mobile-MCP Server**: Android automation and testing
- **DeepWiki MCP Server**: GitHub repository documentation and best practices
- **Context7 MCP Server**: Library documentation and API information
- **DocFork MCP Server**: Latest library documentation and code examples
- **Tavily MCP Server**: Web search and content extraction
- **Brave Search MCP Server**: Web search and content discovery

### 📊 **Total MCP Servers**: 10/10 **FULLY OPERATIONAL**

## MCP Server Usage Enforcement

### 🧠 **KNOWLEDGE MANAGEMENT INTEGRATION**

**MANDATORY**: Before using any MCP server, integrate knowledge from both systems:

1. **ByteOver**: Retrieve relevant programming patterns and techniques
2. **OpenMemory**: Search for project-specific context and decisions
3. **Combined Context**: Use both knowledge sources for optimal MCP server usage

### 📋 **Consolidated Usage Rules Table**

| Task Type | MCP Server | Primary Tools | When to Use |
|-----------|------------|---------------|-------------|
| **Code Search & Analysis** | Claude Context | `mcp_claude-context_search_code`, `mcp_claude-context_index_codebase` | Finding functions, classes, patterns, codebase indexing |
| **Build & Gradle Tasks** | Gradle MCP Server | `mcp_gradle-mcp-server_execute_gradle_task`, `mcp_gradle-mcp-server_run_gradle_tests` | Building, testing, project management, dependency handling |
| **Android Device Operations** | Android MCP Server | `mcp_android_get_packages`, `mcp_android_execute_adb_shell_command` | Device management, ADB operations, app installation |
| **Android Automation** | Mobile-MCP Server | `mcp_mobile-mcp_mobile_launch_app`, `mcp_mobile-mcp_mobile_click_on_screen_at_coordinates` | UI testing, device automation, test orchestration |
| **External Documentation** | DeepWiki, Context7, DocFork | `mcp_deepwiki_ask_question`, `mcp_context7_get-library-docs` | Library docs, API information, best practices |
| **Web Search** | Tavily, Brave Search | `mcp_tavily_tavily_search`, `mcp_brave-search_brave_web_search` | Current information, web content, research |
| **Persistent Memory** | OpenMemory | `mcp_openmemory_add-memory`, `mcp_openmemory_search-memories` | Information storage, context retention, pattern learning |

### 🗂️ **Codebase Operations - Claude Context MCP Server** ✅ **WORKING**

**Status**: See [Current MCP Server Status Summary](#-current-mcp-server-status-summary) above for operational details.

#### **When to Use Claude Context**

- Finding specific functions, classes, or implementations
- Understanding code architecture and patterns
- Locating code related to specific features or bugs
- Analyzing code dependencies and relationships

#### **Code Search Usage Examples**

```text
// ✅ CORRECT: Use claude-context for code search
"Search for Navigation3Controller implementation in the codebase"

// ❌ INCORRECT: Manual file searching
"Look through files manually to find navigation code"
```

### 🏗️ **Build System Operations - Gradle MCP Server** ✅ **WORKING**

**Status**: See [Current MCP Server Status Summary](#-current-mcp-server-status-summary) above for operational details.

#### **Gradle Usage Examples**

```text
// ✅ CORRECT: Use gradle-mcp-server for build tasks
"Build the app module using gradle-mcp-server"

// ❌ INCORRECT: Manual command execution
"Run ./gradlew build manually in terminal"
```

### 📱 **Android Development - Android MCP Server** ✅ **WORKING**

**Status**: See [Current MCP Server Status Summary](#-current-mcp-server-status-summary) above for operational details.

#### **Android Best Practices (Shared)**

**Device Configuration**:

```yaml
# config.yaml - Optional but recommended for multiple devices
device:
  name: "emulator-5554"  # Device identifier from 'adb devices'
```

**Performance Optimization**:

- Screenshots automatically compressed to 30% to prevent "maximum call stack exceeded" errors
- UI layout analysis uses `uiautomator dump` for efficiency
- Package management optimized with `pm list packages` commands

**Error Handling**:

- Automatic device availability checks
- ADB installation verification
- Graceful fallbacks for device selection

#### **Android Usage Examples**

```text
// ✅ CORRECT: Use Android MCP server for device operations
"Get list of installed packages on the device"

// ✅ CORRECT: Use Android MCP server for ADB commands
"Execute adb shell pm list packages -3 to see third-party apps"

// ✅ CORRECT: Use Android MCP server for UI analysis
"Get current UI layout information for accessibility testing"
```

### 🤖 **Android Automation - Mobile-MCP Server** ✅ **WORKING**

**Status**: See [Current MCP Server Status Summary](#-current-mcp-server-status-summary) above for operational details.

#### **Mobile-MCP Specific Best Practices**

**Device Setup**:

```bash
# Start Android emulator
emulator -avd Medium_Phone

# Use default device (recommended for single device)
mobile_use_default_device
```

**Core Interaction Patterns**:

- **Screen Interaction**: `getScreenSize()`, `tap(x, y)`, `swipe(direction)`
- **Input Methods**: `sendKeys(text)`, `pressButton(button)`
- **App Management**: `listApps()`, `launchApp(packageName)`, `terminateApp(packageName)`
- **Device State**: `setOrientation()`, `getOrientation()`

**Performance Optimization**:

- Native accessibility trees for fast interactions
- Screenshot-based fallback for coordinate-based taps
- Deterministic tool application to reduce ambiguity

**Error Handling**:

- `ActionableError` for user-friendly error messages
- Automatic device selection guidance
- Platform tool verification

#### **Android Automation Usage Examples**

```text
// ✅ CORRECT: Use Mobile-MCP server for automation
"Run automated UI tests using Mobile-MCP server"

// ✅ CORRECT: Use Mobile-MCP server for device control
"Launch the GitHub users app and navigate to search"

// ✅ CORRECT: Use Mobile-MCP server for testing
"Automate user search flow and verify results"
```

### 🧠 **Persistent Memory - OpenMemory MCP Server** ✅ **WORKING**

#### **CURRENT STATUS** for memory operations

- **Memory Storage**: Use `mcp_openmemory_add-memory` for storing information ✅ **WORKING**
- **Memory Retrieval**: Use `mcp_openmemory_search-memories` for finding stored information ✅ **WORKING**
- **Memory Management**: Use `mcp_openmemory_list-memories` for listing all memories ✅ **WORKING**

#### **When to Use OpenMemory**

- Storing important project information and patterns
- Remembering user preferences and context
- Maintaining conversation context across sessions
- Storing learned patterns and solutions

#### **Memory Usage Examples**

```text
// ✅ CORRECT: Use OpenMemory for persistent storage
"Store this navigation pattern in memory for future reference"

// ✅ CORRECT: Use OpenMemory for memory retrieval
"Search for previously stored navigation patterns"
```

**Status**: See [Current MCP Server Status Summary](#-current-mcp-server-status-summary) above for operational details.

### 🔍 **External Documentation & Search Services** ✅ **ALL WORKING**

#### **MANDATORY USAGE** for external information

- **Library Documentation**: Use `mcp_context7_resolve-library-id` and `mcp_context7_get-library-docs` for official docs
- **Package Information**: Use `mcp_context7_resolve-library-id` for library discovery
- **GitHub Documentation**: Use `mcp_deepwiki_ask_question` for repository docs and best practices
- **Web Search**: Use `mcp_tavily_tavily_search` and `mcp_brave-search_brave_web_search` for current information
- **Content Extraction**: Use `mcp_tavily_tavily_extract` for web page content

#### **🚀 ENHANCED RESEARCH STRATEGY**

For comprehensive research workflows, see `.cursor/rules/enhanced-research-strategy.mdc` which includes:

- **Advanced Search**: Exa neural/semantic search and deep research capabilities
- **Multi-AI Consensus**: Zen MCP orchestration for expert validation
- **Sequential Analysis**: Step-by-step problem decomposition and planning
- **Research Automation**: Intelligent workflow orchestration across all MCP servers

**Status**: See [Current MCP Server Status Summary](#-current-mcp-server-status-summary) above for operational details.

#### **Best Practices for External Services**

1. **DeepWiki Usage**:

```text
// ✅ CORRECT: Use DeepWiki for repository-specific information
"Research best practices for android-mcp-server configuration"
"Get implementation details for mobile-mcp server"
```

2. **Context7 Usage**:

```text
// ✅ CORRECT: Use Context7 for library discovery
"Find Android navigation component libraries"
"Get Material Design component documentation"
```

3. **DocFork Usage**:

```text
// ✅ CORRECT: Use DocFork for detailed documentation
"Get Jetpack Navigation 3 implementation examples"
"Find Android architecture pattern documentation"
```

4. **Tavily Usage**:

```text
// ✅ CORRECT: Use Tavily for current information
"Search for latest Android development best practices"
"Find current MCP server configuration guides"
```

5. **Brave Search Usage**:

```text
// ✅ CORRECT: Use Brave Search for web content
"Search for Android MCP server tutorials"
"Find mobile automation best practices"
```

#### **When to Use External Services**

- Finding latest library documentation
- Searching for current best practices
- Getting up-to-date information
- Researching solutions and approaches
- Discovering new libraries and tools

#### **Usage Examples**

```text
// ✅ CORRECT: Use external search for current information
"Search for latest Android Navigation 3 best practices using Tavily"

// ✅ CORRECT: Use DeepWiki for repository-specific information
"Research best practices for android-mcp-server configuration"

// ✅ CORRECT: Use Context7 for library discovery
"Find Material Design component libraries for Android"

// ❌ INCORRECT: Relying on outdated knowledge
"Use information from training data without verification"
```

## MCP Server Selection Guidelines

### **Primary Selection Criteria**

1. **Codebase Operations** → Claude Context MCP Server ✅ **WORKING**
2. **Build & Gradle Tasks** → Gradle MCP Server ✅ **WORKING**
3. **Android Device Operations** → Android MCP Server ✅ **WORKING**
4. **Android Automation** → Mobile-MCP Server ✅ **WORKING**
5. **External Documentation** → DeepWiki, Context7, DocFork ✅ **ALL WORKING**
6. **Web Search** → Tavily, Brave Search ✅ **ALL WORKING**
7. **Persistent Memory** → OpenMemory MCP Server ✅ **WORKING**

### **Secondary Selection Criteria**

- **Performance**: Use the most efficient server for the task
- **Reliability**: Prefer stable, well-maintained servers
- **Security**: Use servers with appropriate access controls
- **Integration**: Choose servers that integrate well with the workflow

## Usage Enforcement Rules

### **Rule 1: Codebase Operations**

- **ALWAYS** use Claude Context MCP server for code search and analysis
- **NEVER** manually browse files or use basic search tools
- **ENSURE** codebase is properly indexed before searching

### **Rule 2: Build Operations**

- **ALWAYS** use Gradle MCP server for build tasks
- **AVOID** manual command execution unless MCP server unavailable
- **VERIFY** project structure before executing tasks

### **Rule 3: Android Operations**

- **ALWAYS** use appropriate Android MCP servers for device operations
- **AVOID** manual ADB commands unless MCP server unavailable
- **ENSURE** proper device connection before operations

**Exception Handling**: If MCP server is unavailable after status/config verification, manual execution is allowed with explicit user approval.

### **Rule 4: External Information**

- **ALWAYS** use external search for current information
- **NEVER** rely solely on training data for time-sensitive information
- **VERIFY** information from multiple sources when possible
- **USE** appropriate external service for each information type

### **Rule 5: Memory Management**

- **ALWAYS** use OpenMemory for persistent information storage
- **NEVER** rely on conversation memory for important information
- **REGULARLY** review and clean up stored memories

## Error Handling & Fallbacks

### **Primary Server Unavailable**

1. **Immediate**: Check server status and connectivity
2. **Fallback**: Use alternative server if available
3. **Manual**: Only as last resort with user approval

**Fallback Priority**:

- **Code Search**: Claude Context → Manual file browsing (if no alternative)
- **Build Tasks**: Gradle MCP → Manual `./gradlew` execution (with approval)
- **Android Ops**: Android MCP → Manual ADB commands (with approval)
- **External Info**: DeepWiki → Context7 → DocFork → Tavily → Brave Search

### **Server Configuration Issues**

1. **Diagnose**: Check MCP server configuration
2. **Verify**: Ensure proper API keys and settings
3. **Reconfigure**: Update configuration as needed

### **Performance Issues**

1. **Monitor**: Track server response times
2. **Optimize**: Use caching and efficient queries
3. **Scale**: Consider multiple server instances

## Best Practices

### **Efficiency**

- **Batch Operations**: Group related operations when possible
- **Caching**: Cache frequently accessed information
- **Parallel Execution**: Use multiple servers simultaneously when appropriate

### **Security**

- **Access Control**: Limit server access to necessary operations
- **API Keys**: Secure storage and rotation of API keys
- **Audit Logging**: Track server usage and operations

### **Reliability**

- **Health Checks**: Regular server status monitoring
- **Backup Servers**: Maintain alternative server options
- **Error Recovery**: Implement graceful degradation

## Configuration Requirements

### **Required Environment Variables**

**⚠️ SECURITY WARNING**: Never commit API keys to version control. Use environment variables or secure secret management.

**Placement Options**:

- **`.env` file** (recommended for local development)
- **Shell profile** (`.zshrc`, `.bashrc`) for persistent access
- **Cursor settings** for IDE integration
- **CI/CD secrets** for automated environments

```bash
# Claude Context
OPENAI_API_KEY=your_openai_key
MILVUS_TOKEN=your_milvus_token

# Gradle MCP Server
GRADLE_HOME=/path/to/gradle

# Android MCP Server
ANDROID_HOME=/path/to/android/sdk

# External Services
TAVILY_API_KEY=your_tavily_key
CONTEXT7_API_KEY=your_context7_key
BRAVE_API_KEY=your_brave_key

# OpenMemory
OPENMEMORY_API_KEY=your_openmemory_key
```

### **Server Configuration Files**

```jsonc
// .claude/mcp.json
{
  "mcpServers": {
    "claude-context": {
      "command": "npx",
      "args": ["@anthropic-ai/claude-context"],
      "env": {
        "OPENAI_API_KEY": "${OPENAI_API_KEY}",
        "MILVUS_TOKEN": "${MILVUS_TOKEN}"
      }
    },
    "gradle-mcp-server": {
      "command": "java",
      "args": ["-jar", "gradle-mcp-server.jar"]
    },
    "android": {
      "command": "uv",
      "args": ["--directory", "path/to/android-mcp-server", "run", "server.py"]
    },
    "mobile-mcp": {
      "command": "npx",
      "args": ["@mobilenext/mobile-mcp@latest"]
    },
    "deepwiki": {
      "type": "sse",
      "url": "https://mcp.deepwiki.com/sse"
    },
    "context7": {
      "command": "npx",
      "args": ["-y", "@upstash/context7-mcp"]
    },
    "docfork": {
      "command": "npx",
      "args": ["-y", "--node-options=--experimental-vm-modules", "docfork@latest"]
    },
    "tavily": {
      "command": "npx",
      "args": ["mcp-remote@latest", "https://mcp.tavily.com/mcp/?tavilyApiKey=${TAVILY_API_KEY}"]
    },
    "brave-search": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-brave-search"],
      "env": {
        "BRAVE_API_KEY": "${BRAVE_API_KEY}"
      }
    }
  }
}
```

## Monitoring & Analytics

### **Usage Tracking**

- **Server Utilization**: Monitor server usage patterns
- **Performance Metrics**: Track response times and success rates
- **Error Rates**: Monitor and analyze error patterns
- **User Satisfaction**: Collect feedback on server performance

### **Optimization Opportunities**

- **Server Scaling**: Identify need for additional server instances
- **Caching Strategies**: Optimize frequently accessed data
- **Query Optimization**: Improve search and retrieval efficiency
- **Integration Improvements**: Enhance server interoperability

## Troubleshooting Guide

**🔍 Quick Reference**: For detailed steps, see [Error Handling & Fallbacks](#error-handling--fallbacks) section above.

### **Common Issues Checklist**

- [ ] **Server Connection Failed**: Network, config, authentication
- [ ] **Authentication Errors**: API keys, token expiration, permissions
- [ ] **Performance Issues**: Resources, rate limiting, query optimization
- [ ] **Data Inconsistencies**: Sources, synchronization, validation

**Immediate Actions**:

1. Check server status using appropriate MCP tools
2. Verify configuration and environment variables
3. Test with simple operations before complex tasks
4. Use fallback servers when available

## Conclusion

This MCP guide ensures consistent, efficient, and secure usage of MCP servers across all development tasks. By following these guidelines, developers can:

- **Improve Efficiency**: Use the right tool for each task
- **Enhance Reliability**: Leverage specialized server capabilities
- **Maintain Security**: Follow proper access control practices
- **Ensure Consistency**: Standardize server usage patterns
- **Optimize Performance**: Use servers effectively and efficiently

**Remember**: Always use the appropriate MCP server for each task type, and never fall back to manual operations unless absolutely necessary. All 10 MCP servers are now fully operational and ready for production use.
