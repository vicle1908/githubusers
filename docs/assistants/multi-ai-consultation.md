---
description: Framework for consulting multiple AI models to build comprehensive consensus
alwaysApply: true
globs:
  - "**/*"
tags:
  - consultation
  - consensus
  - multi-ai
  - decision-making
---

# Multi-AI Consultation Rules

## Core Consultation Enforcement

**MANDATORY**: Use multi-AI consultation for critical development decisions, architectural reviews, and complex problem-solving.

## Integration with Research Workflow

Multi-AI consultation integrates with the 10-step research workflow:

1. Complete research using MCP servers (see `enhanced-research-strategy.md`)
2. Validate via `mcp_zen_consensus` and deepen via `mcp_zen_thinkdeep`
3. Store findings via `mcp_router__add-memory` when future recall is required and record key decisions with the user’s consent

## Trigger Conditions

Automatically activate multi-AI consultation when:

- **Architecture Reviews**: Evaluating system architecture, design patterns, or major refactoring
- **Critical Decisions**: Technology stack choices, migration strategies, or breaking changes
- **Complex Problem Solving**: Multi-faceted issues requiring deep analysis
- **Code Reviews**: Major feature implementations or security-critical code
- **Planning**: Project planning, risk assessment, or timeline estimation
- **Performance Issues**: App startup, memory usage, battery consumption optimization
- **Security Implementation**: Authentication, data protection, API security decisions

## MCP Tool Integration

### Primary Consultation Tools

| Tool | Purpose | When to Use |
|------|---------|-------------|
| `mcp_zen_consensus` | Multi-model validation | After research completion |
| `mcp_zen_thinkdeep` | Complex problem analysis | Multi-stage investigation |
| `mcp_zen_planner` | Action plan creation | Implementation planning |

### Integration with MCP Servers

- **Before Consultation**: Review existing knowledge with `mcp_router__search-memories`
- **During Consultation**: Include context from `mcp_openmemory_search-memories`
- **After Consultation**: Store insights using `mcp_router__add-memory` when long-term recall is needed

## Consultation Process Rules

### Phase 1: Pre-Consultation Setup

1. **Knowledge Retrieval**: Use `mcp_router__search-memories` for relevant context
2. **Context Gathering**: Use `mcp_openmemory_search-memories` for project context
3. **Research Completion**: Ensure 10-step research workflow is completed

### Phase 2: Multi-AI Analysis

1. **Consensus Building**: Use `mcp_zen_consensus` for multi-model validation
2. **Deep Analysis**: Use `mcp_zen_thinkdeep` for complex problem investigation
3. **Planning**: Use `mcp_zen_planner` for implementation planning

### Phase 3: Knowledge Integration

1. **Store Insights**: Use `mcp_router__add-memory` for persistent notes
2. **Update Memory**: Use `mcp_openmemory_add-memory` for project decisions
3. **Create Plans**: Use the planning tool (`update_plan`) and document summaries in OpenMemory only when requested

## Usage Enforcement Rules

### Rule 1: Research Integration

- Complete the research workflow before consultation (see `enhanced-research-strategy.md`)
- Ensure knowledge retrieval and context gathering are completed

### Rule 2: Tool Selection

- **ALWAYS** use `mcp_zen_consensus` for multi-model validation
- **ALWAYS** use `mcp_zen_thinkdeep` for complex analysis
- **ALWAYS** use `mcp_zen_planner` for implementation planning

### Rule 3: Knowledge Management

- **ALWAYS** store consultation insights in OpenMemory when they will be needed later
- **ALWAYS** update OpenMemory with project decisions
- **ALWAYS** create implementation plans for complex decisions

## Android-Specific Triggers

Multi-AI consultation automatically activates for:

- **Architecture Decisions**: Choosing between patterns, libraries, or frameworks
- **Navigation Implementation**: Complex navigation flows or deep linking
- **Performance Issues**: App startup, memory usage, battery consumption
- **Security Implementation**: Authentication, data protection, API security
- **Material Design**: Complex UI patterns or accessibility requirements
- **Testing Strategy**: Comprehensive test coverage and automation

## Quality Assurance

- **Completeness**: 100% research workflow completion before consultation
- **Accuracy**: 90%+ consensus on major recommendations
- **Integration**: Proper knowledge storage and memory updates
- **Planning**: Implementation plans created for complex decisions

## Failure Prevention

**Never proceed with consultation when:**

- Research workflow is incomplete
- Knowledge retrieval fails
- Context gathering is insufficient
- MCP tools are unavailable

**Consultation must include:**

- Complete research workflow (steps 1-8)
- Multi-AI validation (steps 9-10)
- Knowledge storage and memory updates
- Implementation planning for complex decisions
