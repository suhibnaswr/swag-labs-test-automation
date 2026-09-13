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

## Prerequisites

Before running the project, make sure the following tools are installed:

- Java 17.
- Maven.
- Git.
- Google Chrome or Microsoft Edge.
- Allure Commandline for opening reports locally.
- Jenkins for running the CI pipeline.

Verify Java and Maven from the terminal:

```bash
java -version
mvn -version
```

## Setup

Clone the repository from GitHub:

```bash
git clone https://github.com/suhibnaswr/swag-labs-test-automation.git
```

Open the project directory:

```bash
cd swag-labs-test-automation
```

Download the project dependencies and compile the source code:

```bash
mvn clean compile
```

The default configuration is located inside:

```text
src/test/resources/config.properties
```

Update the browser or headless values in this file when required.

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

The Allure report contains:

- Test execution status.
- Test steps and descriptions.
- Failure screenshots.
- Current page URL attachments.
- Retry information.
- Failure details and stack traces.

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

## Jenkins Pipeline

The project includes a `Jenkinsfile` for continuous integration.

The Jenkins pipeline contains the following stages:

- Checkout: downloads the source code from GitHub.
- Build: compiles the Maven project.
- Test: runs the selected TestNG suite.
- Post Actions: publishes JUnit results, Allure results and screenshots.

The pipeline supports the following parameters:

- `SUITE`: selects the complete, smoke or parallel TestNG suite.
- `HEADLESS`: controls whether the browser runs without displaying its window.

Available suite values:

```text
testng.xml
testng-smoke.xml
testng-parallel.xml
```

To run the Jenkins pipeline:

1. Open the `swag-labs-test-automation` job in Jenkins.
2. Select **Build with Parameters**.
3. Select the required TestNG suite.
4. Enable or disable the `HEADLESS` parameter.
5. Select **Build**.
6. Wait for the build to finish.
7. Open **Allure Report** to review the test results.

A successful Jenkins build publishes:

- JUnit test results.
- Allure test results.
- Failure screenshots.
- A downloadable Allure report archive.

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

## Future Automation

Future improvements may include:

- Cross-browser testing with Chrome, Edge and Firefox.
- Automated accessibility testing.
- Visual regression testing.
- Performance and load testing.
- API testing for application services.
- Running the Jenkins pipeline automatically after every GitHub push.
- Executing tests inside Docker containers.
- Running tests on multiple operating systems.
- Publishing build notifications through email or Slack.
- Adding test history and trend reports.

## Author

Suhib N. Dameiri
