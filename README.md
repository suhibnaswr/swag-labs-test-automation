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
- Log AssertJ
- Log4 Log4j2
- Apache Commons CSV CSV

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