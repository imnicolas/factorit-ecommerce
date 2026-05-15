# Cómo levantar el proyecto en local (Windows)

Este proyecto es una aplicación Frontend desarrollada en Angular.

## Requisitos previos

- **Node.js**: Asegúrate de tener instalado Node.js (se recomienda una versión LTS compatible con Angular 14).
- **Angular CLI**: Puedes instalarlo globalmente con `npm install -g @angular/cli`, aunque también puedes usar los comandos locales de `npm`.

## Pasos para la ejecución

### 1. Instalar dependencias

Abre una terminal en la raíz del proyecto `front-carrito-factorit` y ejecuta:

```cmd
npm install
```

### 2. Configurar la URL del Backend (Opcional)

El proyecto está configurado para apuntar al backend en `http://localhost:8080` por defecto en el archivo `src/environments/environment.ts`. Si tu backend corre en otro puerto, modifícalo allí.

### 3. Ejecutar la Aplicación

Para levantar el servidor de desarrollo, ejecuta:

```cmd
npm start
```
O también:
```cmd
ng serve
```

La aplicación se levantará en `http://localhost:4200`. Ábrela en tu navegador para interactuar con ella.