# Markdown Linting Conventions

This document defines the markdown linting rules and conventions for the GitHubUsers project, based on [markdownlint v0.38.0](https://github.com/DavidAnson/markdownlint/tree/v0.38.0).

## Configuration

The project uses `.markdownlint.json` for configuration. The rules are designed to ensure consistency, readability, and maintainability across all markdown documentation.

## Core Rules

### Headings (MD001-MD003, MD018-MD025, MD041-MD043)

#### MD001: Heading levels should only increment by one level at a time

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
```

#### MD003: Heading style (ATX style enforced)

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
```

#### MD041: First line in file should be a top-level heading

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
```

### Lists (MD004-MD007, MD029-MD032)

#### MD004: Unordered list style (dash/hyphen enforced)

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
<!-- Good -->
- Item 1
- Item 2
  - Nested item

<!-- Bad -->
* Item 1
+ Item 2
```

#### MD007: Unordered list indentation (2 spaces)

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
<!-- Good -->
- Item 1
- Item 2
  - Nested item

<!-- Bad -->
* Item 1
+ Item 2
<!-- Good -->
- Item 1
  - Nested with 2 spaces
    - Double nested

<!-- Bad -->
- Item 1
    - Nested with 4 spaces
```

#### MD029: Ordered list item prefix

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
<!-- Good -->
- Item 1
- Item 2
  - Nested item

<!-- Bad -->
* Item 1
+ Item 2
<!-- Good -->
- Item 1
  - Nested with 2 spaces
    - Double nested

<!-- Bad -->
- Item 1
    - Nested with 4 spaces
<!-- Good (sequential) -->
1. First item
2. Second item
3. Third item

<!-- Also Good (all ones) -->
1. First item
1. Second item
1. Third item
```

### Line Length (MD013)

- Maximum line length: 120 characters
- Exceptions: Code blocks, tables, and URLs
- Headings must respect the limit

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
<!-- Good -->
- Item 1
- Item 2
  - Nested item

<!-- Bad -->
* Item 1
+ Item 2
<!-- Good -->
- Item 1
  - Nested with 2 spaces
    - Double nested

<!-- Bad -->
- Item 1
    - Nested with 4 spaces
<!-- Good (sequential) -->
1. First item
2. Second item
3. Third item

<!-- Also Good (all ones) -->
1. First item
1. Second item
1. Third item
<!-- Good -->
This is a line that stays within the 120 character limit and is easy to read.

<!-- Bad -->
This is a very long line that exceeds the 120 character limit and becomes difficult to read in most editors and should be broken up into multiple lines.
```

### Whitespace (MD009-MD012, MD030-MD032, MD047)

#### MD012: Multiple consecutive blank lines (maximum 1)

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
<!-- Good -->
- Item 1
- Item 2
  - Nested item

<!-- Bad -->
* Item 1
+ Item 2
<!-- Good -->
- Item 1
  - Nested with 2 spaces
    - Double nested

<!-- Bad -->
- Item 1
    - Nested with 4 spaces
<!-- Good (sequential) -->
1. First item
2. Second item
3. Third item

<!-- Also Good (all ones) -->
1. First item
1. Second item
1. Third item
<!-- Good -->
This is a line that stays within the 120 character limit and is easy to read.

<!-- Bad -->
This is a very long line that exceeds the 120 character limit and becomes difficult to read in most editors and should be broken up into multiple lines.
<!-- Good -->
Paragraph 1

Paragraph 2

<!-- Bad -->
Paragraph 1


Paragraph 2
```

#### MD030: Spaces after list markers

```markdown
```xml
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
<!-- Good -->
- Item 1
- Item 2
  - Nested item

<!-- Bad -->
* Item 1
+ Item 2
<!-- Good -->
- Item 1
  - Nested with 2 spaces
    - Double nested

<!-- Bad -->
- Item 1
    - Nested with 4 spaces
<!-- Good (sequential) -->
1. First item
2. Second item
3. Third item

<!-- Also Good (all ones) -->
1. First item
1. Second item
1. Third item
<!-- Good -->
This is a line that stays within the 120 character limit and is easy to read.

<!-- Bad -->
This is a very long line that exceeds the 120 character limit and becomes difficult to read in most editors and should be broken up into multiple lines.
<!-- Good -->
Paragraph 1

Paragraph 2

<!-- Bad -->
Paragraph 1


Paragraph 2
<!-- Good -->
- Item with one space after dash
1. Ordered item with one space

<!-- Bad -->
-  Item with two spaces
1.  Ordered item with two spaces
```

### Code (MD014, MD031, MD038, MD040, MD046, MD048)

#### MD040: Fenced code blocks should have a language specified

````markdown
```kotlin
```kotlin
<!-- Good -->
# Heading 1
## Heading 2
### Heading 3

<!-- Bad -->
# Heading 1
### Heading 3 (skipped level 2)
<!-- Good -->
# Heading 1
## Heading 2

<!-- Bad -->
Heading 1
=========
<!-- Good -->
# Document Title

Content starts here...

<!-- Bad -->
Some text before heading

# Document Title
<!-- Good -->
- Item 1
- Item 2
  - Nested item

<!-- Bad -->
* Item 1
+ Item 2
<!-- Good -->
- Item 1
  - Nested with 2 spaces
    - Double nested

<!-- Bad -->
- Item 1
    - Nested with 4 spaces
<!-- Good (sequential) -->
1. First item
2. Second item
3. Third item

<!-- Also Good (all ones) -->
1. First item
1. Second item
1. Third item
<!-- Good -->
This is a line that stays within the 120 character limit and is easy to read.

<!-- Bad -->
This is a very long line that exceeds the 120 character limit and becomes difficult to read in most editors and should be broken up into multiple lines.
<!-- Good -->
Paragraph 1

Paragraph 2

<!-- Bad -->
Paragraph 1


Paragraph 2
<!-- Good -->
- Item with one space after dash
1. Ordered item with one space

<!-- Bad -->
-  Item with two spaces
1.  Ordered item with two spaces
<!-- Good -->
fun main() {
    println("Hello, World!")
}
```

<!-- Bad -->
```kotlin
fun main() {
    println("Hello, World!")
}
```
````
````markdown
```bash
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"
```

<!-- Bad (indented) -->
    echo "Hello, World!"
````
````markdown
```kotlin
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"
```

<!-- Bad -->
~~~kotlin
val example = "code"
~~~
````
```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
```

#### MD050: Strong emphasis style (asterisk for bold)

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
```

### Links and References (MD034, MD039, MD042, MD051-MD053)

#### MD042: No empty links

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
```

#### MD051: Link fragments should be valid

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
```

### Tables

Tables should be properly formatted with pipes aligned:

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
```

### HTML (MD033)

Limited HTML is allowed for specific use cases:

- `<br>` - Line breaks when necessary
- `<sup>`, `<sub>` - Superscript and subscript
- `<details>`, `<summary>` - Collapsible sections

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
```

## File Organization

### File Naming

- Use kebab-case for file names: `my-document.md`
- Use UPPERCASE for special documents: `README.md`, `CHANGELOG.md`, `LICENSE.md`
- Be descriptive but concise

### Document Structure

1. **Title**: First line must be a top-level heading
2. **Introduction**: Brief description of the document's purpose
3. **Table of Contents**: For documents longer than 3 sections
4. **Main Content**: Organized with logical heading hierarchy
5. **References**: Links and citations at the end

Example structure:

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
```

## Special Conventions for Project

### MCP Tool References

When documenting MCP tools, use consistent formatting:

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
```

### Code Examples

Always specify the language and provide context:

````markdown
```kotlin
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}
```
````
```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
```

## Inline Comments for Exceptions

Use markdownlint inline comments when exceptions are necessary:

```markdown
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
<!-- markdownlint-disable-next-line MD013 -->
This is a very long line that cannot be broken due to a specific URL or technical requirement that must remain on a single line.

<!-- markdownlint-disable MD033 -->
<custom-html-element>
  When HTML is absolutely necessary
</custom-html-element>
<!-- markdownlint-enable MD033 -->
```

## Automation and CI/CD

### Pre-commit Hook

Add to `.git/hooks/pre-commit`:

```bash
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
<!-- markdownlint-disable-next-line MD013 -->
This is a very long line that cannot be broken due to a specific URL or technical requirement that must remain on a single line.

<!-- markdownlint-disable MD033 -->
<custom-html-element>
  When HTML is absolutely necessary
</custom-html-element>
<!-- markdownlint-enable MD033 -->
#!/bin/bash
# Check markdown files
if command -v markdownlint &> /dev/null; then
    markdownlint '**/*.md' --ignore node_modules
fi
```

### GitHub Actions

```yaml
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
<!-- markdownlint-disable-next-line MD013 -->
This is a very long line that cannot be broken due to a specific URL or technical requirement that must remain on a single line.

<!-- markdownlint-disable MD033 -->
<custom-html-element>
  When HTML is absolutely necessary
</custom-html-element>
<!-- markdownlint-enable MD033 -->
#!/bin/bash
# Check markdown files
if command -v markdownlint &> /dev/null; then
    markdownlint '**/*.md' --ignore node_modules
fi
name: Markdown Lint
on: [push, pull_request]
jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: DavidAnson/markdownlint-cli2-action@v9
        with:
          globs: '**/*.md'
```

## Common Issues and Fixes

### Issue: Trailing spaces (MD009)

```bash
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
<!-- markdownlint-disable-next-line MD013 -->
This is a very long line that cannot be broken due to a specific URL or technical requirement that must remain on a single line.

<!-- markdownlint-disable MD033 -->
<custom-html-element>
  When HTML is absolutely necessary
</custom-html-element>
<!-- markdownlint-enable MD033 -->
#!/bin/bash
# Check markdown files
if command -v markdownlint &> /dev/null; then
    markdownlint '**/*.md' --ignore node_modules
fi
name: Markdown Lint
on: [push, pull_request]
jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: DavidAnson/markdownlint-cli2-action@v9
        with:
          globs: '**/*.md'
# Find files with trailing spaces
grep -l " $" *.md

# Fix trailing spaces
sed -i '' 's/[ \t]*$//' *.md
```

### Issue: Inconsistent heading styles

```bash
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
<!-- markdownlint-disable-next-line MD013 -->
This is a very long line that cannot be broken due to a specific URL or technical requirement that must remain on a single line.

<!-- markdownlint-disable MD033 -->
<custom-html-element>
  When HTML is absolutely necessary
</custom-html-element>
<!-- markdownlint-enable MD033 -->
#!/bin/bash
# Check markdown files
if command -v markdownlint &> /dev/null; then
    markdownlint '**/*.md' --ignore node_modules
fi
name: Markdown Lint
on: [push, pull_request]
jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: DavidAnson/markdownlint-cli2-action@v9
        with:
          globs: '**/*.md'
# Find files with trailing spaces
grep -l " $" *.md

# Fix trailing spaces
sed -i '' 's/[ \t]*$//' *.md
# Find Setext-style headings
grep -n "^==*$\|^--*$" *.md
```

### Issue: Missing language in code blocks

```bash
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
<!-- markdownlint-disable-next-line MD013 -->
This is a very long line that cannot be broken due to a specific URL or technical requirement that must remain on a single line.

<!-- markdownlint-disable MD033 -->
<custom-html-element>
  When HTML is absolutely necessary
</custom-html-element>
<!-- markdownlint-enable MD033 -->
#!/bin/bash
# Check markdown files
if command -v markdownlint &> /dev/null; then
    markdownlint '**/*.md' --ignore node_modules
fi
name: Markdown Lint
on: [push, pull_request]
jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: DavidAnson/markdownlint-cli2-action@v9
        with:
          globs: '**/*.md'
# Find files with trailing spaces
grep -l " $" *.md

# Fix trailing spaces
sed -i '' 's/[ \t]*$//' *.md
# Find Setext-style headings
grep -n "^==*$\|^--*$" *.md
# Find code blocks without language
grep -n "^```$" *.md
```

## VS Code Integration

Install the markdownlint extension and add to `.vscode/settings.json`:

```json
```kotlin
fun main() {
    println("Hello, World!")
}

#### MD046: Code block style (fenced preferred)

<!-- Good -->
echo "Hello, World!"

#### MD048: Code fence style (backticks)

<!-- Good -->
val example = "code"

### Emphasis (MD036-MD037, MD049-MD050)

#### MD049: Emphasis style (underscore for italic)

<!-- Good -->
_italic text_

<!-- Bad -->
*italic text*
<!-- Good -->
**bold text**

<!-- Bad -->
__bold text__
<!-- Good -->
[Link text](https://example.com)
[Reference link][ref]

[ref]: https://example.com

<!-- Bad -->
[Empty link]()
[No reference][]
<!-- Good -->
[Jump to section](#valid-section)

<!-- Bad -->
[Jump to section](#non-existent-section)
<!-- Good -->
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Data 1   | Data 2   | Data 3   |
| Data 4   | Data 5   | Data 6   |

<!-- Bad -->
|Column 1|Column 2|Column 3|
|---|---|---|
|Data 1|Data 2|Data 3|
<!-- Good -->
Line 1<br>
Line 2

<details>
<summary>Click to expand</summary>
Hidden content
</details>

<!-- Bad -->
<div>Avoid unnecessary HTML</div>
<span style="color: red">No inline styles</span>
# Document Title

Brief introduction explaining the purpose of this document.

## Table of Contents

- [Section 1](#section-1)
- [Section 2](#section-2)
- [Section 3](#section-3)

## Section 1

Content for section 1...

## Section 2

Content for section 2...

## Section 3

Content for section 3...

## References

- [Link 1](https://example.com)
- [Link 2](https://example.com)
- Tool name: `mcp_server_name_tool_name`
- Example: `mcp_gradle-mcp-server_execute_gradle_task`
- Always use backticks for tool names
// Example: Navigation setup in Android
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen() }
    }
}

### Android/Kotlin Specific

- Use `kotlin` for Kotlin code blocks
- Use `xml` for Android XML layouts
- Use `gradle` or `kotlin` for Gradle build scripts
- Use `properties` for properties files

### Tables for Comparisons

Use tables for comparing options or listing configurations:

| Feature | Option A | Option B |
|---------|----------|----------|
| Performance | Fast | Moderate |
| Complexity | Low | High |
| Flexibility | Limited | Extensive |
<!-- markdownlint-disable-next-line MD013 -->
This is a very long line that cannot be broken due to a specific URL or technical requirement that must remain on a single line.

<!-- markdownlint-disable MD033 -->
<custom-html-element>
  When HTML is absolutely necessary
</custom-html-element>
<!-- markdownlint-enable MD033 -->
#!/bin/bash
# Check markdown files
if command -v markdownlint &> /dev/null; then
    markdownlint '**/*.md' --ignore node_modules
fi
name: Markdown Lint
on: [push, pull_request]
jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: DavidAnson/markdownlint-cli2-action@v9
        with:
          globs: '**/*.md'
# Find files with trailing spaces
grep -l " $" *.md

# Fix trailing spaces
sed -i '' 's/[ \t]*$//' *.md
# Find Setext-style headings
grep -n "^==*$\|^--*$" *.md
# Find code blocks without language
grep -n "^```$" *.md
{
  "markdownlint.config": {
    "extends": ".markdownlint.json"
  },
  "editor.formatOnSave": true,
  "[markdown]": {
    "editor.defaultFormatter": "DavidAnson.vscode-markdownlint"
  }
}
```

## References

- [markdownlint Rules](https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md)
- [markdownlint CLI](https://github.com/igorshubovych/markdownlint-cli)
- [CommonMark Spec](https://spec.commonmark.org/)
- [GitHub Flavored Markdown](https://github.github.com/gfm/)

---

_Last Updated: 2025-01-07_
_Version: 1.0.0_
