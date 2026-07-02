# Copilot Instructions for study

These instructions guide code generation and edits in this repository.

## Non-negotiable rules

1. Don't assume. Don't hide confusion. Surface tradeoffs.
2. Minimum code that solves the problem. Nothing speculative.
3. Touch only what you must. Clean up only your own mess.
4. Define success criteria. Loop until verified.

## Project overview

- Language: Java
- Build tool: Maven (`pom.xml`)
- Doc root: `src/docs`
- Main source root: `src/main/java/org/callatis/study/...`
- Test source root: `src/test/java/...`
- This repository contains algorithm/data-structure solutions and focused unit tests.

## Compile and build

- Compile main and test sources without running tests: `mvn -DskipTests compile`
- Build the JAR artifact without running tests: `mvn -DskipTests package`
- Build the JAR artifact and run tests: `mvn package`
- Clean previous outputs, then build and test: `mvn clean package`
- For quick validation of compile-only changes, prefer `mvn -DskipTests compile` first, then run targeted tests.

## Problem workflow (docs -> code)

- Problems are typically authored as Markdown at: `src/docs/<packageName>/<problem>.md`
- When implementing a new problem, generate:
	- `src/main/java/<packageName>/<problem>.java` (implementation stub first - do NOT fill in the implementation unless explicitly directed so)
	- `src/test/java/<packageName>/<problem>Test.java` (tests for all examples in the Markdown)
- Treat examples in the Markdown problem statement as required test cases.
- If the Markdown includes edge cases/constraints, add tests for those too when they affect behavior.
- Keep package declarations and directory layout consistent between docs, implementation, and tests.

## Code style and scope

- Prefer small, targeted changes over broad refactors.
- Preserve existing public method signatures unless a task explicitly requires API changes.
- Keep implementations straightforward and readable; avoid speculative abstractions.
- Follow existing package conventions under `org.callatis.study`.

## Java and dependency constraints

- Write Java code compatible with Java 8 syntax/features unless instructed otherwise.
- Use existing test stack (JUnit 4 style annotations and assertions).
- Do not add new dependencies unless clearly needed for the requested task.

## Testing conventions

- Add or update tests only for behavior changed by the task.
- Keep test names behavior-focused (`test...` style is acceptable in this repo).
- Use deterministic inputs/outputs; avoid flaky or timing-based tests.
- For problem-driven tasks from `src/docs`, ensure tests cover all examples in the corresponding Markdown file.
- Run targeted tests first, then broader suites only when needed.

## Verification checklist before finishing

- The change compiles in Maven for affected modules/files.
- Relevant tests pass (at minimum, tests covering changed behavior).
- No unrelated files are reformatted or modified.
- The final explanation states what changed, why, and how it was validated.

## Useful commands

- Compile only (skip tests): `mvn -DskipTests compile`
- Build only (skip tests): `mvn -DskipTests package`
- Clean + build + test: `mvn clean package`
- Run all tests: `mvn test`
- Run one test class: `mvn -Dtest=TwoSumTest test`
- Run multiple test classes: `mvn -Dtest=TwoSumTest,FloydWarshallTest test`
