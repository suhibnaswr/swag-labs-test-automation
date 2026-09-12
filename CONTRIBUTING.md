# Contributing Guide

This document describes the workflow used when contributing to the
Swag Labs Test Automation Framework.

## Branching Strategy

Do not make project changes directly on the `main` branch.

Create a separate feature branch for every project part.

Branch name examples:

- `feature/project-documentation`
- `feature/test-execution-docs`
- `feature/code-quality`
- `feature/jenkins-pipeline`

## Creating a Feature Branch

Start from the latest version of `main`.

Use `git switch main` to return to the main branch.

Use `git switch -c feature/branch-name` to create a new branch.

## Commit Messages

Commit messages must be short, clear and readable.

Recommended prefixes:

- `feat:` for a new feature.
- `fix:` for a bug fix.
- `test:` for test changes.
- `docs:` for documentation.
- `build:` for Maven or build changes.
- `ci:` for Jenkins and continuous integration changes.
- `chore:` for project maintenance.
- `merge:` for merge commits.

## Testing Before a Merge

Run the complete suite before merging a branch.

Use `mvn test` to run all automated tests.

The expected result is:

- 29 tests executed.
- 0 failures.
- 0 errors.
- 0 skipped tests.
- Maven reports `BUILD SUCCESS`.

## Merging a Feature Branch

Return to `main` and merge with the `--no-ff` option.

This preserves the branch history and creates a visible merge commit.

## Generated Files

Do not commit generated files or local IDE settings.

The `.gitignore` file excludes:

- Maven `target` output.
- Allure generated results.
- Screenshots and logs.
- IntelliJ IDEA metadata.
- Temporary operating-system files.

## Pull Request Checklist

Before completing a change, verify that:

- The project builds successfully.
- All tests pass.
- No generated files are included.
- No credentials or passwords are committed.
- The commit message clearly describes the change.
- Documentation is updated when necessary.
