## elevator-api
Elevator Service Api to manage elevator requests.

## Version: V1.0

## Description

It manages a public and freight elevator separately, processes the requests and validates the access to restricted floors.
It's also responsible for calculating the elevator trajectory.

## Technical notes
- The project runs on Java 17
- Uses maven for project management
- Uses Spring and Springboot
- Uses PostgreSQL as database 
- It's an Api Rest microservice, so in order to test the program you will have to use http requests
- Uses Junit and Mockito for the unit tests

## How to run locally
- Have docker running (it can be docker desktop or any version you want)
- Go to project's root path and execute the following docker command:
```docker-compose up``` if it's the first time you run the image.
- If you have run it previously you simply can execute: 
  ```docker-compose start```
- The previous step will generate a PostgreSLQ db docker image, which the program will connect automatically.
- Once the database image is running you can simply run the project main class in you ide of preference.
- At the first startup the liquibase script will generate the needed tables and populate them will initial data.
- With the program running you can use the provided postman collection to test the different endpoints.
- Note: The local profile will run as default and uses port 8080 to run the service.

## DB connection
- The docker image will generate the db automatically
- Since it's a demo project the db connection details and credentials are placed in the application-local.yml
- The project uses liquibase to automatically generate the tables and populate them with some initial data for testing purposes.