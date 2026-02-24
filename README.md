# Property View API

Spring Boot REST API for viewing and searching hotel properties.

## Tech stack

- Java 21
- Spring Boot (Web, Data JPA, Validation)
- H2 database (in‑memory)
- Liquibase (schema migrations)
- MapStruct (DTO mapping)
- springdoc‑openapi (Swagger UI)

## Build & run

From the project root:

```bash
mvn spring-boot:run
```

The application will start on:

- **Port**: `8092`
- **Base path**: `/property-view`

## Main endpoints

All endpoints are rooted at `/property-view` (already configured as servlet context path).

- **GET `/property-view/hotels`** – list of hotels (short info)
- **GET `/property-view/hotels/{id}`** – full hotel details
- **GET `/property-view/search`** – search by `name`, `brand`, `city`, `country`, `amenities`
  - Example: `/property-view/search?city=Paris&amenities=pool&amenities=wifi`
- **POST `/property-view/hotels`** – create hotel (JSON body with DTO)
- **POST `/property-view/hotels/{id}/amenities`** – add amenities to hotel
- **GET `/property-view/histogram/{param}`** – histogram by `brand`, `city`, `country`, `amenities`

## Swagger / OpenAPI

After starting the app:

- Swagger UI: `http://localhost:8092/property-view/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8092/property-view/v3/api-docs`

## Database

- In‑memory H2 database.
- Schema managed by Liquibase changelog under `src/main/resources/db/changelog`.
- H2 console: `http://localhost:8092/property-view/h2-console`

You can switch to another relational DB by updating only the Spring `datasource` properties in `application.yml`; no H2‑specific code is used in repositories or services.

