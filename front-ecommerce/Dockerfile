# Etapa 1: Build de Angular
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# Build con configuración development para que use environment.ts (apiUrl=http://localhost:8080).
# Production apunta a Render y solo aplica para el deploy en la nube.
RUN npm run build -- --configuration development

# Etapa 2: Servir estáticos con nginx
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/front-carrito-factorit /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
