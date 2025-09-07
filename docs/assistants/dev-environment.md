# Development Environment Setup

## Required Environment Variables

### MCP Server API Keys

Set these environment variables in your shell profile:

```bash
```bash

```markdown
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

# GitHub Integration
export GITHUB_PERSONAL_ACCESS_TOKEN="your-github-token"

# Android Development
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools"
```

## MCP Server Configuration

### Required MCP Servers

1. **Gradle MCP Server** - Build operations (MANDATORY)
1. **Android MCP Server** - Device operations
1. **Mobile-MCP Server** - UI automation
1. **Claude Context** - Code search
1. **ByteRover MCP** - Knowledge management
1. **OpenMemory** - Persistent storage
1. **DeepWiki** - Repository documentation
1. **Context7/DocFork** - Library documentation
1. **Tavily/Brave** - Web search
1. **Zen MCP** - Multi-AI consultation

## Local Setup

1. Export all required environment variables
1. Install MCP servers via npm/pip as needed
1. Configure assistant settings to use environment variables
1. Never commit actual keys to version control

## CI/CD Setup

Add secrets to your CI/CD platform:

- GitHub Actions: Repository Settings → Secrets
- GitLab CI: Project Settings → CI/CD → Variables
- Jenkins: Credentials → Global credentials

## Security Best Practices

1. Use environment variables, never hardcode secrets
1. Rotate keys regularly
1. Use different keys for development and production
1. Add `.env` files to `.gitignore`
1. Use secret management tools in production
