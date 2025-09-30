# OrangeHRM-Automation
# OrangeHRM Automation Framework

## Overview
This is a Selenium + Cucumber + TestNG + Extent Reports automation framework for OrangeHRM.

## Folder Structure
- `src/main/java` - Base classes, Page Object Model, Utilities
- `src/test/java` - Step definitions, Hooks, Test runners
- `src/test/resources` - Feature files, Extent report properties
- `Reports/` - HTML and PDF reports
- `Screenshots/` - Screenshots of tests
- `drivers/` - WebDriver binaries

## How to Run
1. Clone the repository:
git clone https://github.com/
<username>/OrangeHRM-Automation.git

2. Navigate to the project folder.
3. Run tests using TestNG or Maven:
   
mvn clean test

5. Test reports will be available in `Reports/` and screenshots in `Screenshots/`.

## Dependencies
- Selenium WebDriver
- TestNG
- Cucumber
- Extent Reports
- Java 8+
