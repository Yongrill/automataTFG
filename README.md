# AutomataTFG

Aplicacion web para monitorizacion SCADA y analisis OEE de ordenes, bonos y maquinas de produccion. El repositorio incluye un backend Spring Boot, un frontend React y una configuracion Docker preparada para ejecutar una demo portable sin depender del ERP ni del PLC real.

## Objetivo

El proyecto centraliza datos de produccion, estados de maquina, fichajes y metricas OEE para facilitar el seguimiento operativo. En modo portable se cargan datos simulados sobre H2 para que la herramienta pueda revisarse en cualquier equipo con Docker.

## Tecnologias

- Java 11 y Spring Boot
- Spring Data JPA
- H2 en modo portable
- SQL Server para despliegues privados
- React y Vite
- Nginx como servidor del frontend
- Docker Compose

## Ejecucion con Docker

Requisitos:

- Docker Desktop o Docker Engine
- Docker Compose

Arranque:

```powershell
docker compose up -d --build
```

URLs:

- Frontend: http://localhost:8080
- Backend/API: http://localhost:8084

Parada:

```powershell
docker compose down
```

## Datos demo

El despliegue Docker usa el perfil `portable`. Este perfil no intenta conectar con SQL Server ni con PLC real; carga datos de ejemplo para poder validar la aplicacion.

Ejemplos utiles para probar el dashboard OEE:

- Bono: `BONO-2001`
- Orden: `2001`
- Maquina: `MAQ-01`

## Desarrollo local

Backend:

```powershell
mvn test
mvn spring-boot:run -Dspring-boot.run.profiles=portable
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

Nota: el frontend actual requiere una version reciente de Node compatible con Vite. En Docker se usa `node:22-alpine`.

## Perfiles de ejecucion

- `portable`: usa H2 y datos simulados. Es el perfil recomendado para revision, demo y entrega.
- `sqlserver`: usa variables de entorno para conectar con la base de datos real. No se deben subir credenciales al repositorio.

Variables principales para despliegue privado:

```env
SPRING_PROFILES_ACTIVE=sqlserver
SPRING_DATASOURCE_URL=jdbc:sqlserver://server:1433;databaseName=database;encrypt=false;trustServerCertificate=true;
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
```

## Estructura

```text
.
|-- Dockerfile              # Imagen del backend
|-- docker-compose.yml      # Backend + frontend
|-- pom.xml                 # Proyecto Maven
|-- frontend/               # Aplicacion React
`-- src/                    # Codigo Java y recursos Spring
```

## Estado del repositorio

El repositorio no incluye artefactos generados, dependencias locales, bases de datos, logs ni credenciales. Para reproducir la demo basta con clonar el proyecto y ejecutar Docker Compose.
