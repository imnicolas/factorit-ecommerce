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

| Pieza | Dónde | Notas |
|-------|--------|--------|
| **Base de datos** | Supabase (PostgreSQL) | Creá el proyecto, copiá host, puerto, usuario, contraseña y nombre de DB. En la API definí `DB_SSL_MODE=require` (valor por defecto del repo). Ejecutá el SQL inicial (`ms-ecommerce/scripts/scripts.sql`) en el SQL editor de Supabase si la base está vacía. |
| **Backend (JAR)** | Render, Railway, Fly.io, etc. | Imagen Docker o build Maven + `java -jar`. Definí las mismas variables que en `application.yaml`: `PORT` (si el proveedor lo inyecta, usalo), `DB_*`, `DB_SSL_MODE`, `CORS_ALLOWED_ORIGINS` con la URL **exacta** del front en producción (ej. `https://tu-proyecto.vercel.app`). |
| **Frontend** | Vercel, Netlify, Cloudflare Pages | Build de Angular en modo producción: `environment.prod.ts` debe tener `apiUrl` apuntando a la URL pública de la API (HTTPS). |

Otras opciones válidas: **todo en un VPS** (Docker Compose igual que en local, con dominio y TLS con Caddy/Traefik), o **Google Cloud Run / AWS ECS** si preferís contenedores gestionados.

## Documentación adicional

En la raíz del repo hay una guía más detallada para desarrollo sin Docker en `CLAUDE.md` (comandos Maven, `ng serve`, variables de entorno).
