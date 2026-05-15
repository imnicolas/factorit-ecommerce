# Ecommerce (Angular + Spring Boot + PostgreSQL)

Monorepo con backend (`ms-ecommerce/`), frontend (`front-ecommerce/`) y `docker-compose.yaml` para levantar todo en local.

## Requisitos

- [Docker](https://docs.docker.com/get-docker/) y Docker Compose v2

## Levantar en local con Docker Compose

Desde la raíz del repositorio:

```bash
docker compose up --build
```

Cuando termine de arrancar:

| Servicio   | URL |
|------------|-----|
| Frontend   | http://localhost:4200 |
| API (REST) | http://localhost:8080 |
| Swagger    | http://localhost:8080/swagger-ui.html |
| PostgreSQL | puerto **5433** en el host (usuario/clave/db por defecto: `postgres`) |

La primera vez, Postgres ejecuta el seed en `ms-ecommerce/scripts/scripts.sql` al crear el volumen.

### Reinicio “de cero” (borra datos locales)

```bash
docker compose down -v
docker compose up --build
```

## Despliegue en la nube (orientación)

Stack (**Render API + Vercel front + Supabase DB**)