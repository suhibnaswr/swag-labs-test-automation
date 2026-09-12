# Test Execution Guide

This document explains how to run the Swag Labs automated tests.

## Prerequisites

Before running the tests, make sure the following tools are available:

- Java 17
- Maven
- Google Chrome or Microsoft Edge
- Internet connection

## Complete Test Suite

Run all test classes and DataProvider executions:

```bash
mvn test
```

Expected result:

```text
Tests run: 29
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## Headless Execution

Run all tests without displaying browser windows:

```bash
mvn test -Dheadless=true
```

Headless execution is suitable for CI environments such as Jenkins.

## Smoke Test Suite

Run only tests assigned to the `smoke` group:

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-smoke.xml"
```

Expected result:

```text
Tests run: 7
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## Parallel Test Suite

Run test classes in parallel using three threads:

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-parallel.xml"
```

The parallel suite uses:

```text
parallel="classes"
thread-count="3"
```

Every thread receives an independent WebDriver through
`ThreadLocal<WebDriver>`.

## Allure Report

Generate and open the Allure report:

```bash
mvn allure:serve
```

Stop the temporary Allure server using:

```text
Ctrl + C
```

## Failure Evidence

When a test fails, `TestListener` performs the following actions:

- Captures a browser screenshot.
- Saves the screenshot inside `target/screenshots`.
- Attaches the screenshot to the Allure result.
- Attaches the current page URL.
- Logs the test name and failure reason.

## TestNG Suite Files

The project contains three TestNG Suite files:

```text
testng.xml
testng-smoke.xml
testng-parallel.xml
```

- `testng.xml` runs the complete test suite.
- `testng-smoke.xml` runs only Smoke tests.
- `testng-parallel.xml` runs test classes in parallel.

## Generated Files

The following directories are generated automatically and excluded
from Git:

```text
target/
allure-results/
allure-report/
test-output/
screenshots/
logs/
```

## Current Test Results

The latest verified results are:

```text
Complete Suite: 29 tests passed
Smoke Suite: 7 tests passed
Parallel Suite: 29 tests passed
Headless Suite: 29 tests passed
```