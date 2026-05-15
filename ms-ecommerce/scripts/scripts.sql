-- Crear base de datos (Ejecutar manualmente si no existe)
-- CREATE DATABASE factorit_db;

-- Tabla Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    dni VARCHAR(255) NOT NULL UNIQUE,
    es_vip BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabla Productos
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL
);

-- Tabla Fechas Especiales
CREATE TABLE IF NOT EXISTS fechas_especiales (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL UNIQUE
);

-- Tabla Carritos
CREATE TABLE IF NOT EXISTS carritos (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    date_created DATE NOT NULL,
    total DECIMAL(10, 2),
    monto_bruto DECIMAL(10, 2),
    cliente_id BIGINT NOT NULL REFERENCES clientes(id)
);

-- Tabla Carrito Detalles
CREATE TABLE IF NOT EXISTS carrito_detalles (
    id BIGSERIAL PRIMARY KEY,
    carrito_id BIGINT NOT NULL REFERENCES carritos(id),
    producto_id BIGINT NOT NULL REFERENCES productos(id),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL
);

-- Inserts iniciales Clientes:
INSERT INTO clientes (nombre, apellido, dni, es_vip) VALUES
('Juan', 'Pérez', '12345678', FALSE),
('María', 'Gómez', '87654321', TRUE),
('Carlos', 'López', '11223344', FALSE),
('Ana', 'Martínez', '44332211', FALSE),
('Luis', 'Fernández', '55667788', TRUE);

-- Inserts iniciales Productos:
INSERT INTO productos (nombre, precio) VALUES
('Laptop Gamer', 1200.00),
('Mouse Inalámbrico', 300),
('Teclado Mecánico', 200),
('Monitor 24"', 300.00),
('Auriculares Bluetooth', 200),
('SSD 1TB', 500.00),
('Memoria RAM 16GB', 500),
('Webcam 4K', 400),
('Micrófono USB', 400),
('Silla Gamer', 250.00);


-- Inserts iniciales Fechas especiales (promocionables):
INSERT INTO fechas_especiales (fecha) VALUES
('2026-05-01'),  -- Día del Trabajador
('2026-05-25'),  -- Revolución de Mayo (Arg)
('2026-07-09'),  -- Día de la Independencia
('2026-12-25'),  -- Navidad
('2026-01-01');  -- Año Nuevo