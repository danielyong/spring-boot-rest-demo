## Sample Spring Boot Project 

A very simple and crude CRUD with Spring Boot, with with different implementation of selecting and updating data.
Some place uses CRUD repository while others uses raw SQL with JDBC templates
Basic authentication check when accessing certain endpoints

Includes a sample weather data Kafka producer and consumer
Every 3 seconds, a data is randomly generated and produced to a kafka topic
this produced data will be consumed by this same application because I cannot be bothered to write another service
consumed data will also be persisted in the same PostgreSQL instance.

You can head to http://localhost:8081/web/weather to view the data.

UI is practically non existent as I am not really a front end developer, I build backend stuffs

## Setting up your project

Run docker compose build, or setup to a PostgreSQL instance manually

### License
This project is licensed under [GLWTPL](./LICENSE)
