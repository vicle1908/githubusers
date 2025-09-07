---
type: "always_apply"
---

# Architecture Rule for Augment

## Overview

This rule defines the architectural principles and patterns for all development within the Augment system, ensuring consistency, maintainability, and scalability across all components.

## Core Architecture Principles

### Clean Architecture


- **Separation of Concerns**: Clear boundaries between layers
  - Presentation Layer: UI components and user interaction
  - Domain Layer: Business logic and use cases
  - Data Layer: Data sources and repositories
- **Dependency Rule**: Inner layers should not depend on outer layers
- **Framework Independence**: Business logic should not depend on frameworks

### Feature-Based Modules


- **Modular Structure**: Each feature is a self-contained module
- **Single Responsibility**: Each module has one clear purpose
- **Loose Coupling**: Modules interact through well-defined interfaces
- **High Cohesion**: Related functionality grouped together

### MVI (Model-View-Intent) Pattern


- **Unidirectional Data Flow**: Intent → Model → View
- **State Management**: Immutable state with predictable updates
- **Side Effect Handling**: Explicit handling of side effects
- **Testability**: Easy to test due to clear separation of concerns

## Multi-AI Integration Architecture

### AI Model Orchestration


- **Model Discovery**: Auto-discovery of all available AI models
- **Health Monitoring**: Continuous monitoring of model availability
- **Load Balancing**: Distribute queries across available models
- **Fallback Mechanisms**: Graceful degradation when models are unavailable

### Consultation Framework


- **Parallel Processing**: Simultaneous consultation of multiple models
- **Response Synthesis**: Aggregation and analysis of multiple perspectives
- **Consensus Building**: Identification of common recommendations
- **Divergence Analysis**: Highlighting conflicting opinions for deeper review

### Quality Assurance


- **Minimum Model Requirement**: At least 2 models must respond successfully
- **Consensus Threshold**: 70% agreement for major recommendations
- **Response Validation**: Filtering of low-quality responses
- **Error Handling**: Robust error handling and recovery mechanisms

## Development Standards

### Code Organization


- **Package Structure**: Feature-oriented package organization
- **Naming Conventions**: Consistent naming across all components
- **Documentation**: Comprehensive documentation for public APIs
- **Testing**: Unit tests, integration tests, and end-to-end tests

### Technology Stack


- **Primary Language**: Kotlin for all components
- **UI Framework**: Jetpack Compose for Android, appropriate frameworks for other platforms
- **Networking**: Ktor client for HTTP requests
- **Dependency Injection**: Hilt for Android, appropriate DI frameworks for other platforms
- **Persistence**: Room for Android, appropriate databases for other platforms
- **State Management**: MVI pattern with StateFlow/SharedFlow

### Testing Strategy


- **Unit Testing**: Test individual components and functions
- **Integration Testing**: Test interactions between components
- **UI Testing**: Test user interfaces and user flows
- **AI Model Testing**: Test AI model integration and response handling

## Multi-AI Consultation Integration

### Automatic Triggers

The system automatically activates multi-AI consultation for:

- **Architecture Decisions**: Choosing between patterns, libraries, or frameworks
- **Navigation Implementation**: Complex navigation flows or deep linking
- **Performance Issues**: App startup, memory usage, battery consumption
- **Security Implementation**: Authentication, data protection, API security
- **Material Design**: Complex UI patterns or accessibility requirements
- **Testing Strategy**: Comprehensive test coverage and automation

### Manual Triggers

Users can explicitly request multi-AI consultation with:

- "Multi-AI review of [component]"
- "Analyze [feature] with all models"
- "Get consensus on [decision]"
- "Comprehensive assessment of [implementation]"

## Best Practices

### ✅ DO


- Follow Clean Architecture principles strictly
- Implement MVI pattern for state management
- Use Navigation 3 with deep link navigation
- Follow Material 3 design guidelines
- Write comprehensive tests for all layers
- Use proper error handling and user feedback
- Implement accessibility features
- Follow Kotlin best practices and idioms

### ❌ DON'T


- Mix business logic with UI code
- Use deprecated APIs or patterns
- Ignore accessibility requirements
- Skip error handling and edge cases
- Write untested code
- Use hardcoded values or magic numbers
- Ignore performance implications
- Violate SOLID principles

## Continuous Improvement

### Code Review Checklist


- [ ] Follows Clean Architecture principles
- [ ] Implements MVI pattern correctly
- [ ] Uses proper navigation patterns
- [ ] Follows Material 3 guidelines
- [ ] Includes proper error handling
- [ ] Has comprehensive test coverage
- [ ] Follows accessibility guidelines
- [ ] Uses proper Kotlin idioms

### Performance Monitoring


- Monitor app startup time
- Track memory usage and leaks
- Measure UI frame rates
- Analyze network call efficiency
- Monitor battery consumption

This architecture rule ensures the highest quality development by combining modern architectural patterns with comprehensive multi-AI validation for critical decisions.
