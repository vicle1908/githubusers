# 🔐 Secure MCP Environment Setup Guide

## ⚠️ SECURITY NOTICE
**NEVER expose API keys in configuration files or documentation. Always use environment variables for sensitive credentials.**

## 🛠️ Secure MCP Configuration Setup

### 1. Environment Variables Setup

Create a `.env` file or set these environment variables in your system:

```bash
# Required API Keys (replace with your actual keys)
export CONTEXT7_API_KEY="your_context7_api_key_here"
export OPENAI_API_KEY="your_openai_api_key_here"
export GEMINI_API_KEY="your_gemini_api_key_here"
export XAI_API_KEY="your_xai_api_key_here"
export MILVUS_ADDRESS="your_milvus_address_here"
export MILVUS_TOKEN="your_milvus_token_here"
export BRAVE_API_KEY="your_brave_api_key_here"
export OPENMEMORY_API_KEY="your_openmemory_api_key_here"
export TAVILY_API_KEY="your_tavily_api_key_here"
export EXA_API_KEY="your_exa_api_key_here"
```

### 2. Secure MCP Configuration

The `mcp-config-working.json` file now uses environment variable placeholders:

```json
{
  "mcpServers": {
    "context7": {
      "type": "http", 
      "url": "https://mcp.context7.com/mcp",
      "headers": {
        "CONTEXT7_API_KEY": "${CONTEXT7_API_KEY}"
      }
    }
    // ... other servers use ${VARIABLE_NAME} format
  }
}
```

### 3. Local Development Setup

#### Option A: Using .env file
```bash
# Create .env file (DO NOT commit this file)
echo "CONTEXT7_API_KEY=your_key_here" >> .env
echo "OPENAI_API_KEY=your_key_here" >> .env
# ... add all other keys

# Load environment variables
source .env
```

#### Option B: Shell export (temporary)
```bash
# Export for current session only
export CONTEXT7_API_KEY="your_key_here"
export OPENAI_API_KEY="your_key_here"
# ... export all other keys
```

#### Option C: Add to ~/.bashrc (persistent)
```bash
# Add to ~/.bashrc for persistent setup
echo 'export CONTEXT7_API_KEY="your_key_here"' >> ~/.bashrc
echo 'export OPENAI_API_KEY="your_key_here"' >> ~/.bashrc
# ... add all other keys
source ~/.bashrc
```

### 4. API Key Acquisition

#### Where to get API keys:
- **Context7**: Sign up at https://context7.com
- **OpenAI**: Get from https://platform.openai.com/api-keys  
- **Gemini**: Get from https://aistudio.google.com/app/apikey
- **X.AI**: Get from https://x.ai/api
- **Brave Search**: Get from https://brave.com/search/api/
- **OpenMemory**: Get from https://openmemory.ai
- **Tavily**: Get from https://tavily.com
- **Exa**: Get from https://exa.ai

### 5. Security Best Practices

#### ✅ DO:
- Use environment variables for all API keys
- Add `.env` to `.gitignore`
- Use different API keys for different environments (dev/staging/prod)
- Regularly rotate API keys
- Use minimal permissions/scopes for each API key
- Monitor API key usage

#### ❌ DON'T:
- Put API keys directly in code or config files
- Commit API keys to version control
- Share API keys in documentation or screenshots
- Use production API keys in development
- Leave unused API keys active

### 6. Team Development

#### For Teams:
```bash
# Create example environment file (safe to commit)
cp .env .env.example

# Remove actual values, keep structure
# .env.example content:
CONTEXT7_API_KEY=your_context7_api_key_here
OPENAI_API_KEY=your_openai_api_key_here
# ... etc
```

#### .gitignore entries:
```gitignore
# Environment files
.env
.env.local
*.env

# API Keys and secrets
**/secrets/
**/credentials/
api-keys.txt
```

## 🔧 Testing Secure Setup

### Verify Environment Variables
```bash
# Check if variables are set (should show "SET" not the actual key)
echo "OPENAI_API_KEY: $([ -n "$OPENAI_API_KEY" ] && echo "SET" || echo "NOT SET")"
echo "GEMINI_API_KEY: $([ -n "$GEMINI_API_KEY" ] && echo "SET" || echo "NOT SET")"
# ... check all keys
```

### Test MCP Server
```bash
# Test Zen MCP with secure config
uvx --from git+https://github.com/BeehiveInnovations/zen-mcp-server.git zen-mcp-server --help
```

## 🚨 If API Keys Were Exposed

If API keys were accidentally exposed:

1. **Immediately revoke/rotate** all exposed keys
2. **Generate new API keys** from each service
3. **Update environment variables** with new keys
4. **Check git history** and remove any commits with exposed keys
5. **Monitor accounts** for any unauthorized usage

## 🛡️ Production Deployment

### Docker Setup:
```dockerfile
# Use environment variables in Docker
ENV CONTEXT7_API_KEY=${CONTEXT7_API_KEY}
ENV OPENAI_API_KEY=${OPENAI_API_KEY}
# ... other keys
```

### CI/CD Pipeline:
```yaml
# Use secrets in CI/CD (GitHub Actions example)
env:
  CONTEXT7_API_KEY: ${{ secrets.CONTEXT7_API_KEY }}
  OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
  # ... other keys
```

## 📋 Security Checklist

- [ ] All API keys moved to environment variables
- [ ] `.env` file added to `.gitignore`
- [ ] No hardcoded keys in any files
- [ ] Team members have individual API keys
- [ ] API key usage monitoring enabled
- [ ] Regular key rotation schedule established
- [ ] Production keys separate from development keys

---

**Remember: Security is not optional. Always protect your API keys and sensitive credentials.**