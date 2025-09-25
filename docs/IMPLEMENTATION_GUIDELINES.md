# Implementation Guidelines and Best Practices

This document provides an overview of the implementation guidelines and best practices used in this project, with references to detailed documentation.

## Overview

The project follows a set of standardized patterns and practices for implementing features, particularly focused on:
1. Clean Architecture with feature modules
2. Composition over inheritance using Kotlin delegates
3. Feature-owned navigation contracts
4. Reactive paging and search implementations
5. Consistent testing strategies

## Core Architecture Documents

- [Repository Implementation Plan](REPOSITORY_IMPLEMENTATION.md) - Comprehensive plan for implementing the repository feature
- [Android Development Standards](../.lingma/rules/android.md) - Core Android development standards for the project
- [Unified Agent Operating Handbook](../.lingma/rules/AGENTS.md) - Guidelines for AI assistants working on this project

## Implementation Patterns

### Paging and Search

The project implements a reusable paging and search system using Kotlin delegates and reactive programming patterns.

**Key Components:**
- `PagingSourceProvider` - Interface for components that transform queries into paged data streams
- `DefaultPagingSourceProvider` - Default implementation with reactive logic
- `PagingSearchController` - Controller that handles search UI state and user interactions

**Documentation:**
- [Paging and Search Implementation Patterns](implementation-patterns/PAGING_AND_SEARCH.md) - Detailed implementation guide
- [Repository Implementation Plan](REPOSITORY_IMPLEMENTATION.md) - Real-world usage in the repository feature

### Navigation

The project uses a "Navigation 3" approach with feature-owned navigation contracts.

**Key Principles:**
- Each feature owns its navigation contracts
- Shared navigation API provides generic composition locals
- Hilt multibindings for automatic registration
- Deep link handling at the feature level

**Documentation:**
- [Navigation Patterns](implementation-patterns/NAVIGATION.md) - Detailed implementation guide
- [Repository Implementation Plan](REPOSITORY_IMPLEMENTATION.md) - Real-world usage in the repository feature

## Feature Module Structure

Each feature module follows a standardized structure:
1. Data layer (DTOs, DAOs, Repositories)
2. Domain layer (Use cases, Models)
3. Presentation layer (ViewModels, UI states)
4. UI layer (Composables, Navigation)

## Testing Standards

### Unit Testing
- Test ViewModels and Use Cases in isolation
- Mock dependencies using standard mocking frameworks
- Follow AAA pattern (Arrange, Act, Assert)
- Name tests descriptively: `test_[method]_[condition]_[expectedResult]`

### Integration Testing
- Test complete data flows through layers
- Use in-memory databases for data layer testing
- Test navigation integration between features
- Validate deep link handling

### UI Testing
- Use `createAndroidComposeRule` for Compose UI testing
- Test user interactions and state changes
- Verify error states and loading indicators
- Test across different device configurations

## Code Quality Standards

### Static Analysis
- Detekt for code smell detection
- KtLint for code formatting
- Both applied via convention plugins

### Documentation
- KDoc for public APIs
- Clear README files per module
- Architecture documentation
- API documentation

## MCP Integration

### Build Operations
- Prefer Gradle MCP Server for builds and tests
- Use Desktop Commander as fallback when needed

### Device Operations
- Use Android MCP for ADB operations
- Use Mobile-MCP for UI automation
- Manual ADB only with explicit approval

### Research and Documentation
- Use DeepWiki for Android best practices
- Use Context7/DocFork for library documentation
- Validate Navigation 3 implementation with latest docs