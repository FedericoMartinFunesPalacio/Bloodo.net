# Full Stack Template: Angular + Spring Boot + ApiGateway

## Descripción
Este repositorio funciona como template full stack que incluye:
- Frontend: Proyecto en Angular 19 (standalone components, Angular Material).
- Backend: Proyecto en Java 17 con Spring Boot.
- ApiGateway: Proyecto en Java Spring Boot + Spring Cloud Gateway para enrutar y centralizar las APIs.
El objetivo es proveer una base lista para clonar y extender, con configuración mínima y dependencias esenciales ya integradas.

## Frontend (Angular)
- Framework: Angular 19
- Librerías: Angular Material, RxJS/Signals
- Estructura: Standalone components, control flow moderno (@if, @for)
- Configuración lista para integración con el ApiGateway.

## Backend (Spring Boot)
Dependencias principales
- spring-boot-starter-web → API REST.
- spring-boot-starter-data-jpa → Persistencia con JPA/Hibernate.
- spring-boot-starter-validation → Validaciones con Jakarta Validation.
- springdoc-openapi-starter-webmvc-ui (2.1.0) → Documentación automática con Swagger UI.
- h2 (runtime) → Base de datos en memoria para desarrollo.
- lombok → Reducción de boilerplate en entidades y DTOs.
- jakarta.validation-api → API de validación.
Plugins configurados
- maven-compiler-plugin → Procesadores de anotaciones (spring-boot-configuration-processor, lombok).
- spring-boot-maven-plugin → Empaquetado y ejecución del proyecto.
- jacoco-maven-plugin (0.8.12) → Instrumentación y reportes de cobertura de tests.

## ApiGateway (Spring Boot + Spring Cloud)
Dependencias principales
- spring-boot-starter-actuator → Monitoreo y métricas.
- spring-boot-starter-web → Soporte básico de web.
- spring-cloud-starter-gateway-server-webmvc → Gateway para enrutar peticiones.
- spring-boot-devtools → Hot reload en desarrollo.
- spring-boot-starter-test → Testing.
Dependency Management
- spring-cloud-dependencies (2025.0.0) → Importado vía BOM para asegurar compatibilidad de versiones.
Plugins configurados
- spring-boot-maven-plugin → Empaquetado y ejecución del ApiGateway.

## Docker
Cada módulo (frontend, backend, gateway) incluye su propio Dockerfile para empaquetar la aplicación en contenedores.
Ejemplo de build manual
### Backend:
####   cd backend/app-service
####   docker build -t app-service:latest .

### Frontend:
####   cd frontend/angular-app
####   docker build -t angular-app:latest .

### ApiGateway:
####   cd api-gateway
####   docker build -t api-gateway:latest .

## Docker Compose
El proyecto incluye un archivo docker-compose.yml que orquesta los tres servicios:
version: "3.9"
services:
  backend:
    build: ./backend/app-service
    container_name: app-service
    ports:
      - "8080:8080"

  frontend:
    build: ./frontend/angular-app
    container_name: angular-app
    ports:
      - "4200:4200"

  gateway:
    build: ./api-gateway
    container_name: api-gateway
    ports:
      - "8081:8081"
    depends_on:
      - backend
      - frontend
Levantar todo el stack:
  docker-compose up --build

Esto levantará:
- Backend en http://localhost:8080
- Frontend en http://localhost:4200
- ApiGateway en http://localhost:8081

## Licencia
Este template está disponible bajo licencia abierta para que puedas adaptarlo y extenderlo libremente.
