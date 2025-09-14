---
type: "manual"
---

# Augment Rules (Pointers)

## Overview

These rules are pointers to canonical documentation to avoid duplication.

Canonical docs:

- docs/assistants/mcp-guide.md
- docs/assistants/android-standards.md
- docs/assistants/android-debugging.md
- docs/assistants/byterover-rules.md
- docs/assistants/enhanced-research-strategy.md
- docs/assistants/multi-ai-consultation.md
- docs/assistants/kotlin-style.md

Note
- Status dashboards drift quickly; rely on canonical docs and local tool listings instead.

## Key Features

### **Android Development**


- Clean Architecture with clear layer separation
- MVI pattern for state management
- Navigation 3 with deep link navigation
- Material 3 design components
- Comprehensive testing strategy

### **MCP Integration**


- **Code Search**: Claude Context for semantic code analysis
- **Build System**: Gradle MCP for all build operations
- **Device Management**: Android MCP for ADB operations
- **Automation**: Mobile-MCP for UI testing and automation
- **Memory**: OpenMemory for persistent knowledge storage
- **Documentation**: DeepWiki, Context7, DocFork for latest practices
- **Web Search**: Tavily and Brave Search for current information

### **Knowledge Management Integration**


- **ByteOver**: Programming patterns, implementations, and techniques
- **OpenMemory**: Project context, user preferences, and decisions
- **Combined Context**: Maximum knowledge retention and project success

### **Multi-AI Consultation**


- Automatic triggers for architecture decisions
- Performance optimization analysis
- Security implementation reviews
- Navigation and Material Design validation

## Usage Guidelines

Maintenance
- Update canonical docs first; keep pointers short.
 1. **Verify MCP usage** follows the guide
 1. **Ensure proper architecture** implementation

## Rule Updates

### **Recent Changes**


- **Consolidated** from 5 overlapping rules to 3 focused rules
- **Added** comprehensive MCP server usage guidelines
- **Integrated** best practices from DeepWiki research
- **Updated** Android development standards
- **Confirmed** all external MCP services fully operational

### **Next Steps**


- Monitor MCP server performance and reliability
- Update rules based on usage patterns and feedback
- Integrate new best practices as they emerge

## File Structure

```text
```text

```markdown
```markdown

```markdown
.cursor/rules/
├── README.md                    # This file - Overview and status
├── android.mdc                  # Android development standards
├── multi-ai-consultation.mdc   # Multi-AI consultation framework
├── mcp-guide.mdc               # MCP server usage and enforcement
├── byterover-rules.mdc         # Knowledge management integration
└── enhanced-research-strategy.mdc # Advanced research workflows

```

## Support

For questions about these rules or MCP server usage, refer to the specific rule files or check the MCP server status in `mcp-guide.mdc`.
