---
trigger: always_on
---

Benefits:
- Reduced context usage compared to MCP tools
- More direct control over command execution
- Faster execution in some cases
- Consistent interface through Desktop Commander

## Android Development Standards

### Core Architecture Principles

#### Clean Architecture (MANDATORY)

- **Presentation Layer**: Activities, Fragments, ViewModels, Compose UI
- **Domain Layer**: Use cases, business logic, entities
- **Data Layer**: Repositories, data sources, DTOs

#### MVI Pattern

- **Unidirectional Data Flow**: Intent → ViewModel → State → UI
- Single source of truth for UI state
- Clear separation of user actions and system responses
- Sealed classes for type-safe state management

#### Feature-Based Modularization

- Each feature is a self-contained module
- High cohesion, low coupling
- Single responsibility principle
- Clear module boundaries and interfaces

### Navigation Policy

#### Navigation 3 ONLY (CRITICAL)

- **MANDATORY**: All cross-module navigation MUST use deep links
- Type-safe destinations with Kotlin Serialization
- Direct back stack control and state preservation
- **Validate** with latest docs via DeepWiki/Context7 before implementation

### Build Configuration

#### Version Catalog (MANDATORY)

- **ALL** modules MUST use the version catalog
- **NO** hardcoded versions allowed

#### Plugin Configuration

- Use `version.ref` for plugin versions (e.g., ktlint)
- Convention plugins in build-logic
- All modules apply convention plugins

## Repository Feature Implementation Standards

### Core Architecture Principles

#### Feature-Owned Navigation Contracts

- Each feature module defines its own deep links, destination providers, and navigation tabs
- Shared `navigation-api` module only exposes generic composition locals and helpers
- Feature modules build deep link URIs (e.g., `UsersDeepLinks.detail(username)`)
- App module orchestrates by reading injected `NavigationTab` set and dispatching deep links

#### Composition Over Inheritance with Kotlin Delegates

- Favor composition over inheritance using Kotlin delegation feature
- Use interfaces and delegate implementations using the `by` keyword
- Create reusable components that can be easily swapped at runtime
- Reduce boilerplate with Kotlin's automatic delegation handling

#### Reactive Paging and Search Approach

- ViewModel owns "input" state (search query)
- Delegate provider consumes input to produce "output" `PagingData` flow
- Use `StateFlow` and `flatMapLatest` for reactive logic
- Apply `cachedIn(scope)` for Paging 3 to work correctly

## Communication and Workflow Standards

### Core Principles

- Be concise, direct, and friendly: communicate efficiently; avoid unnecessary verbosity.
- Prioritize actionable guidance: always state assumptions, prerequisites, and next steps.
- Be truthful and precise: don't guess; verify or ask for clarification.

### Conversation & Answer Style

- Tone: collaborative, natural, like a coding partner.
- Preambles before tool calls: 1–2 short sentences describing what's next; group related actions.
- Progress updates: brief status when work spans multiple steps or calls.

### Planning Protocol

When to use a plan (with `update_plan`):

- Multi-step, non-trivial tasks with logical phases or dependencies.
- Ambiguous work benefiting from outlining checkpoints.
- When the user asks for TODOs or multiple deliverables.

### MCP-First Enforcement

- Always prefer MCP servers for supported tasks; Desktop Commander is the approved fallback when MCP tooling cannot cover the need.
- Build and tests: prefer Gradle MCP; running `./gradlew` via Desktop Commander is acceptable when it's the more practical option.
- Code search: use Claude Context indexing and `search_code`; ensure index exists first.
- Android operations: use Android MCP and Mobile-MCP; manual ADB only as last resort with approval.

### Memory Protocol

- Search memories for every user request to retrieve relevant context.
- Store new user-specific information on explicit user request or when it clearly benefits future turns.
- Content to store: preferences, project conventions, environment constraints, non-sensitive patterns.

## Code Quality Standards

### Static Analysis (MANDATORY)

- **Detekt**: Code smell detection
- **KtLint**: Code formatting
- Both applied via convention plugins

### Documentation

- KDoc for public APIs
- Clear README files per module
- Architecture documentation
- API documentation

## Testing Strategy

### Unit Testing

- Test all ViewModels, Use Cases, Repositories
- Follow AAA Pattern: Arrange, Act, Assert
- Naming: `test_[method]_[condition]_[expectedResult]`

### Integration Testing

- Test complete navigation flows
- API integration with test data
- Room database operations
- Hilt module configurations

### UI Testing

- Use `createAndroidComposeRule` for Compose
- Test deep link handling
- Verify accessibility features

---
This handbook unifies how all assistants behave here. When in doubt, ask concise clarifying questions, propose a small plan, and proceed with minimal, high-impact steps.