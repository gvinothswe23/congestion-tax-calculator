# Congestion Tax Calculator

Welcome to the Congestion Tax Calculator assignment.

## Project Overview

This project is a congestion tax calculator for vehicles driving in and out of Gothenburg. It calculates the total tax a vehicle incurs based on timestamps, tax rules, and exemptions specific to the year 2013. The application is designed to be adaptable to other cities with different tax rules, externalized through a configuration file.

## Features

Calculates congestion tax for vehicles based on specific time intervals.
Supports tax exemptions for specific types of vehicles (e.g., emergency vehicles, buses).
Limits the total daily tax to 60 SEK per vehicle.
Excludes weekends, public holidays, and the month of July from tax calculations.
Provides an HTTP REST API to interact with the service.
Externalizes city-specific rules to a configuration file for easy extension to other cities.

## Technologies Used

Language: Java 17
Framework: Spring Boot
Build Tool: Maven
Testing: JUnit 5
Configuration: JSON (for city-specific rules)
API Documentation: Swagger

# Getting Started

## Prerequisites

JDK 17
Maven 3.6+ installed on your machine
A supported IDE (e.g., IntelliJ IDEA, Eclipse) or a text editor

## Installing and Running Locally

1. Build the project:
 
   ```mvn clean install```
   
2. Run the application:

   ```mvn spring-boot:run```
   
   The application will be running on ```http://localhost:8080```
   
3. Run the tests:

   ```mvn test```
   
## Usage

## REST API Endpoints

Calculate Tax:

Endpoint: POST /calculate

### Payload:
```json
{
    "city": "gothenburg",
    "vehicleType": "car",
    "timestamps": [ 
		"2013-01-14 21:00:00", 
		"2013-01-15 21:00:00", 
		"2013-02-07 06:23:27", 
		"2013-02-07 15:27:00", 
		"2013-02-08 06:27:00", 
		"2013-02-08 06:20:27", 
		"2013-02-08 14:35:00", 
		"2013-02-08 15:29:00", 
		"2013-02-08 15:47:00", 
		"2013-02-08 16:01:00", 
		"2013-02-08 16:48:00", 
		"2013-02-08 17:49:00", 
		"2013-02-08 18:29:00", 
		"2013-02-08 18:35:00", 
		"2013-03-26 14:25:00", 
		"2013-03-28 14:07:27" 
	]
}
```
### Response:
```json
{
    "taxAmount": 60.0,
    "message": null
}
```

### Swagger Documentation
If Swagger is enabled, you can access the Swagger UI at ```http://localhost:8080/swagger-ui.html``` to explore the API.

## Testing
Unit tests are provided to verify the correctness of the tax calculation logic, including:

1. Tax exemption handling for specific vehicles.
2. Time-based rate application.
3. Maximum daily tax limit enforcement.

Tests can be found under ```src/test/java/com/volvo/congestion_tax```.

## How to Run Tests

### Run the tests with Maven:
```mvn test```
