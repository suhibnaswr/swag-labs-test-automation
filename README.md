# Swag Labs Test Automation Framework

A complete UI test automation framework for the
[SauceDemo](https://www.saucedemo.com/) website.

The project is built with Java, Selenium WebDriver, TestNG, Maven,
Allure Report and the Page Object Model design pattern.

## Technologies

- Java 17
- Selenium WebDriver 4.49.0
- TestNG
- Maven
- Allure Report
- AssertJ
- Log4j2
- Apache Commons CSV

## Project Design

The framework follows the Page Object Model pattern.

Each page class contains:

- Element locators.
- Page actions.
- Explicit waits.
- Navigation methods.
- Page-specific validations.

Test classes contain only test scenarios and assertions.

## Project Structure

```text
src
├── main
│   └── java
│       ├── config
│       ├── exceptions
│       ├── model
│       ├── pages
│       └── utils
└── test
    ├── java
    │   ├── core
    │   ├── data
    │   ├── listeners
    │   └── tests
    └── resources
        ├── config.properties
        └── testdata
```

## Main Components

### DriverFactory

Creates and manages WebDriver instances.

It uses `ThreadLocal<WebDriver>` to provide an independent driver
for every test thread and support parallel execution safely.

### BasePage

Contains shared browser operations such as:

- Clicking elements.
- Typing text.
- Reading text.
- Waiting for elements.
- Scrolling to elements.
- Reading the shopping cart badge.

### Page Objects

The project contains Page Objects for:

- Login page.
- Products page.
- Product details page.
- Shopping cart page.
- Checkout information page.
- Checkout overview page.
- Checkout completion page.

### TestListener

The TestNG listener provides:

- Test execution logging.
- Screenshots when tests fail.
- Screenshot attachments in Allure.
- Current page URL attachments.
- Test status tracking.

### RetryAnalyzer

Retries failed tests according to the configured retry limit.

### Data-Driven Testing

The project uses TestNG DataProviders and CSV test data.

Customer information and product selections are read from:

```text
src/test/resources/testdata/customers.csv
```

### CartSummary

Calculates the expected:

- Item total.
- Tax.
- Final total.

The calculations use `BigDecimal` to maintain financial precision.

## Test Coverage

The automated scenarios cover:

- Successful login.
- Rejected and invalid users.
- Required login fields.
- Products and product details.
- Product sorting.
- Adding and removing products.
- Shopping cart contents and quantities.
- Checkout validation.
- Data-driven customer purchases.
- Item total, tax and final total.
- Successful order completion.
- Cancelled checkout flows.

The framework currently produces 29 successful test executions,
including DataProvider executions.

## TestNG Suites

### Complete Suite

```text
testng.xml
```

Runs the complete test suite.

### Smoke Suite

```text
testng-smoke.xml
```

Runs only tests assigned to the `smoke` group.

### Parallel Suite

```text
testng-parallel.xml
```

Runs test classes in parallel using three threads.

## Running the Tests

Run the complete suite:

```bash
mvn test
```

Run in headless mode:

```bash
mvn test -Dheadless=true
```

Run the Smoke Suite:

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-smoke.xml"
```

Run the Parallel Suite:

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-parallel.xml"
```

## Allure Report

Generate and open the Allure report:

```bash
mvn allure:serve
```

Allure results are generated inside:

```text
target/allure-results
```

Screenshots for failed tests are saved inside:

```text
target/screenshots
```

## Supported Browsers

The browser is selected through `config.properties`.

Supported browser values:

```properties
browser=chrome
```

```properties
browser=edge
```

Headless execution can be enabled using:

```properties
headless=true
```

or through Maven:

```bash
mvn test -Dheadless=true
```

## Git Workflow

The project uses feature branches.

Each project part is developed on a separate branch and then merged
into the `main` branch.

Example:

```bash
git switch -c feature/project-documentation
git add .
git commit -m "docs: add project documentation"
git switch main
git merge --no-ff feature/project-documentation
```

## Author

Suhib N. Dameiri