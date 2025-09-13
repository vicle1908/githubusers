# Development Environment Setup

## Required Environment Variables

### MCP Server API Keys

Set these environment variables in your shell profile:

```bash
# Core MCP Services
export OPENAI_API_KEY="your-openai-key"
export GEMINI_API_KEY="your-gemini-key"
export XAI_API_KEY="your-xai-key"
export ANTHROPIC_API_KEY="your-anthropic-key"

# Search and Documentation
export TAVILY_API_KEY="your-tavily-key"
export BRAVE_API_KEY="your-brave-key"
export EXA_API_KEY="your-exa-key"
export CONTEXT7_API_KEY="your-context7-key"

# Knowledge and Memory
export MILVUS_TOKEN="your-milvus-token"
export OPENMEMORY_API_KEY="your-openmemory-key"
export BYTEROVER_TOKEN="your-byterover-token"  # Token for Byterover MCP shared memory layer

# GitHub Integration
export GITHUB_PERSONAL_ACCESS_TOKEN="your-github-token"

# Android Development
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools"
```

## MCP Server Configuration

### Required MCP Servers

1. **Gradle MCP Server** - Build operations (MANDATORY)
2. **Android MCP Server** - Device operations
3. **Mobile-MCP Server** - UI automation
4. **Claude Context** - Code search
5. **ByteRover MCP** - Knowledge management and shared long-term memory for AI agents
6. **OpenMemory** - Persistent storage
7. **DeepWiki** - Repository documentation
8. **Context7/DocFork** - Library documentation
9. **Tavily/Brave** - Web search
10. **Zen MCP** - Multi-AI consultation

### Byterover MCP Integration

- **Setup**: Configure Byterover for persistent memory management across AI assistants.
- **Environment**: Export `BYTEROVER_TOKEN` as shown above.
- **Workflow**: Use Byterover for storing/retrieving knowledge between sessions. For details, see [byterover-rules.md](./byterover-rules.md) for memory management workflows.
- **Benefits**: Ensures consistent decision-making and shared memory across Claude, Zen, and other assistants.

## Local Setup

1. Export all required environment variables, including Byterover token
2. Install MCP servers via npm/pip as needed
3. Configure assistant settings to use environment variables
4. Never commit actual keys to version control

## CI/CD Setup

Add secrets to your CI/CD platform:

- GitHub Actions: Repository Settings → Secrets
- GitLab CI: Project Settings → CI/CD → Variables
- Jenkins: Credentials → Global credentials

Include Byterover token in CI/CD secrets for continuous integration with shared memory.

## Security Best Practices

1. Use environment variables, never hardcode secrets
2. Rotate keys regularly
3. Use different keys for development and production
4. Add `.env` files to `.gitignore`
5. Use secret management tools in production
6. Protect Byterover token as it handles shared AI memory across assistants
