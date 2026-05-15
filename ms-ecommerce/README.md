# Cómo levantar el proyecto en local (Windows)

Este proyecto es una aplicación Spring Boot que utiliza PostgreSQL como base de datos.

## Requisitos previos

- **Java 17**: Asegúrate de tener instalado el JDK 17.
- **PostgreSQL**: Debe estar instalado y corriendo.
- **Maven**: El proyecto incluye el wrapper `mvnw`, por lo que no es estrictamente necesario tenerlo instalado globalmente.

## Pasos para la ejecución

> Si preferís levantar todo (DB + back + front) en un solo paso, corré `docker compose up --build` desde la raíz del repo.

### 1. Configurar la Base de Datos

1. Abre tu cliente de PostgreSQL (pgAdmin, psql, etc.).
2. Crea una base de datos llamada `postgres` (o el nombre que prefieras).
   ```sql
   CREATE DATABASE postgres;
   ```
3. Conéctate a la base de datos y ejecuta el script de creación de tablas e inserts que se encuentra en `scripts/scripts.sql`.

### 2. Configurar Variables de Entorno (Opcional)

El proyecto utiliza variables de entorno con valores por defecto para conectarse a la base de datos. Si tu configuración local coincide con los valores por defecto, no necesitas configurar nada:
- `DB_HOST`: `localhost`
- `DB_PORT`: `5432`
- `DB_NAME`: `postgres`
- `DB_USER`: `postgres`
- `DB_PASSWORD`: `postgres`

> [!IMPORTANT]
> Por defecto, el proyecto intenta usar conexión SSL (`sslmode=require`). Si tu base de datos local no tiene SSL habilitado (caso común en instalaciones por defecto), debes setear la variable de entorno:
> `DB_SSL_MODE=disable`
>
> En Windows (CMD), podés hacerlo con:
> ```cmd
> set DB_SSL_MODE=disable
> ```
> O en PowerShell:
> ```powershell
> $env:DB_SSL_MODE="disable"
> ```

### 3. Ejecutar la Aplicación

Abre una terminal en la raíz del proyecto `ms-ecommerce` y ejecuta el siguiente comando:

Usando el Maven Wrapper incluido (CMD):
```cmd
mvnw.cmd spring-boot:run
```

O si usas PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación se levantará en `http://localhost:8080`.

## Documentación API (Swagger / OpenAPI)

Una vez levantada la aplicación, podés acceder a la documentación de Swagger en:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Postman

Colección lista para importar en `ms-ecommerce/postman/ms-ecommerce.postman_collection.json`.

Incluye todas las operaciones REST agrupadas en tres carpetas:
- **Carritos**: buscar paginado, crear, consultar, eliminar, agregar/quitar productos, finalizar compra.
- **Clientes**: búsqueda paginada con autocomplete, VIPs del mes, nuevos VIP del mes, perdieron VIP en el mes.
- **Productos**: listar catálogo.

### Cómo usarla

1. Postman → **Import** → arrastrar el archivo JSON.
2. La colección viene con variable `baseUrl = http://localhost:8080`. Cambiala si el back corre en otra URL.
3. Cada request tiene placeholders sensatos (anio/mes actuales, IDs de ejemplo). Ajustá según tus seeds.

## Servicios SOAP

Además de la API REST, el backend expone tres operaciones SOAP de consulta. El WSDL se publica automáticamente al levantar la app.

- **WSDL**: `http://localhost:8080/ws/ecommerce.wsdl`
- **Endpoint**: `http://localhost:8080/ws`
- **Namespace**: `http://ecommerce.factorit.com/soap`

### Operaciones disponibles

| Operación | Input | Descripción |
|---|---|---|
| `GetProductosRequest` | — | Lista todos los productos del catálogo |
| `GetCarritoByIdRequest` | `id` (long) | Detalle de un carrito (cliente, totales, tipo, estado) |
| `GetClientesVipRequest` | `anio` (int), `mes` (int 1-12) | Clientes VIP del mes/año consultado |

### Probar con SOAP UI

1. Abrir SOAP UI → **File → New SOAP Project**.
2. Nombre: `ms-ecommerce` y en **Initial WSDL**: `http://localhost:8080/ws/ecommerce.wsdl`.
3. Aceptar. SOAP UI carga las operaciones automáticamente.
4. Hacer clic en cualquier operación → doble clic en "Request 1" → completar los campos y enviar con ▶.

Ejemplo de request para `GetCarritoByIdRequest`:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://ecommerce.factorit.com/soap">
   <soapenv:Header/>
   <soapenv:Body>
      <soap:GetCarritoByIdRequest>
         <soap:id>1</soap:id>
      </soap:GetCarritoByIdRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Decisión de scope

Se expusieron operaciones de **consulta** únicamente. El PDF pide "exponer servicios SOAP" pero no especifica cuáles, y como las mutaciones (crear carrito, agregar producto, finalizar compra) ya están cubiertas por REST con su dominio rico (DTOs validados, manejo de excepciones, transacciones), agregar SOAP escritura sería duplicación. Las consultas SOAP son suficientes para demostrar la capacidad y son el caso de uso típico de SOAP en integraciones legacy.