-- ============================================================
-- FitSystem - instalación completa de la base de datos
-- MySQL 8.0 o superior
--
-- Ejecute este archivo con un usuario administrador de MySQL
-- (por ejemplo, root). Incluye los usuarios, membresías y pagos
-- de prueba existentes en la base de datos del proyecto.
--
-- Usuario MySQL usado por application.properties:
--   usuario: admin
--   contraseña: fitsystem
--
-- ============================================================

CREATE DATABASE IF NOT EXISTS proyecto_final
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'admin'@'localhost'
    IDENTIFIED BY 'fitsystem';

ALTER USER 'admin'@'localhost'
    IDENTIFIED BY 'fitsystem';

GRANT ALL PRIVILEGES ON proyecto_final.*
    TO 'admin'@'localhost';

FLUSH PRIVILEGES;

USE proyecto_final;

SET NAMES utf8mb4;

-- ============================================================
-- TABLAS PRINCIPALES
-- ============================================================

CREATE TABLE IF NOT EXISTS rol (
    id_rol INT NOT NULL AUTO_INCREMENT,
    rol VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uk_rol_nombre UNIQUE (rol)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(512) NOT NULL,
    telefono VARCHAR(25),
    rol ENUM('ADMINISTRADOR', 'ENTRENADOR', 'CLIENTE'),
    activo TINYINT(1) NOT NULL DEFAULT 1,
    ruta_imagen VARCHAR(1024),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuario_username UNIQUE (username),
    CONSTRAINT uk_usuario_correo UNIQUE (correo)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS usuario_rol (
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario_rol PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuario_rol_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT fk_usuario_rol_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol (id_rol)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ruta (
    id_ruta INT NOT NULL AUTO_INCREMENT,
    ruta VARCHAR(255) NOT NULL,
    id_rol INT,
    requiere_rol TINYINT(1) NOT NULL DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_ruta PRIMARY KEY (id_ruta),
    CONSTRAINT uk_ruta_ruta UNIQUE (ruta),
    CONSTRAINT fk_ruta_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol (id_rol),
    CONSTRAINT chk_ruta_rol
        CHECK (id_rol IS NOT NULL OR requiere_rol = 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS home (
    id BIGINT NOT NULL AUTO_INCREMENT,
    imagen_url VARCHAR(500) NOT NULL,
    CONSTRAINT pk_home PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS plan_membresia (
    id_plan INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(1000),
    precio DECIMAL(10, 2) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT pk_plan_membresia PRIMARY KEY (id_plan),
    CONSTRAINT uk_plan_membresia_nombre UNIQUE (nombre)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS membresia (
    id_membresia INT NOT NULL AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_plan INT,
    plan VARCHAR(50) NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    fecha_pago DATE,
    fecha_inicio DATE,
    fecha_vencimiento DATE,
    estado ENUM('ACTIVA', 'PENDIENTE', 'VENCIDA') NOT NULL,
    CONSTRAINT pk_membresia PRIMARY KEY (id_membresia),
    CONSTRAINT fk_membresia_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT fk_membresia_plan
        FOREIGN KEY (id_plan)
        REFERENCES plan_membresia (id_plan)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS pago (
    id_pago INT NOT NULL AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_plan INT NOT NULL,
    fecha_pago DATE NOT NULL,
    fecha_proximo_pago DATE NOT NULL,
    monto DECIMAL(10, 2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    CONSTRAINT pk_pago PRIMARY KEY (id_pago),
    CONSTRAINT fk_pago_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT fk_pago_plan
        FOREIGN KEY (id_plan)
        REFERENCES plan_membresia (id_plan),
    INDEX idx_pago_fecha (fecha_pago),
    INDEX idx_pago_usuario (id_usuario)
) ENGINE = InnoDB;

-- ============================================================
-- DATOS NECESARIOS PARA EL FUNCIONAMIENTO
-- ============================================================

INSERT INTO rol (id_rol, rol)
VALUES
    (1, 'ADMINISTRADOR'),
    (2, 'ENTRENADOR'),
    (3, 'CLIENTE')
ON DUPLICATE KEY UPDATE
    rol = VALUES(rol);

INSERT INTO ruta (id_ruta, ruta, requiere_rol, id_rol)
VALUES
    (1,  '/admin/**',      1, 1),
    (2,  '/entrenador/**', 1, 2),
    (3,  '/cliente/**',    1, 3),
    (4,  '/',              0, NULL),
    (5,  '/home',          0, NULL),
    (6,  '/acceso',        0, NULL),
    (7,  '/login',         0, NULL),
    (8,  '/registro',      0, NULL),
    (9,  '/registro/**',   0, NULL),
    (10, '/error',         0, NULL),
    (11, '/css/**',        0, NULL),
    (12, '/js/**',         0, NULL),
    (13, '/img/**',        0, NULL),
    (14, '/webjars/**',    0, NULL),
    (15, '/favicon.ico',   0, NULL),
    (16, '/403',           0, NULL),
    (17, '/usuario/**',    1, 1)
ON DUPLICATE KEY UPDATE
    ruta = VALUES(ruta),
    requiere_rol = VALUES(requiere_rol),
    id_rol = VALUES(id_rol);

INSERT INTO plan_membresia
    (id_plan, nombre, descripcion, precio, activo)
VALUES
    (1, 'Plan Básico',
        'El más económico de FitSystem', 12000.00, 1),
    (2, 'Plan Fit',
        'Para usuarios constantes', 18000.00, 1),
    (3, 'Plan Completo',
        'El más completo de FitSystem', 28000.00, 1)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    descripcion = VALUES(descripcion),
    precio = VALUES(precio),
    activo = VALUES(activo);

INSERT INTO home (id, imagen_url)
VALUES (
    1,
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQiDmsisrTYfA_ot_y4jBirYO4z8c8i2YYb-zLAnNb_nQ&s=10'
)
ON DUPLICATE KEY UPDATE
    imagen_url = VALUES(imagen_url);

-- Las contraseñas se conservan cifradas con BCrypt.
INSERT INTO usuario (
    id_usuario,
    nombre,
    apellidos,
    correo,
    username,
    password,
    telefono,
    rol,
    activo,
    ruta_imagen
)
VALUES
    (1, 'Ana', 'Mora Solis', 'ana@fitsystem.com', 'amora',
        '$2a$10$LS4PKIJpaD0cUaIO2a/BjOLrgC2wS9QgsHBrqh8GES4S4kBYUboiG',
        '88880001', 'ADMINISTRADOR', 1, NULL),
    (2, 'Carlos', 'Ruiz Perez', 'carlos@fitsystem.com', 'cruiz',
        '$2a$10$3EFX2OIOrr3w8b7SaW8BWuyeABqiIaSf1j5PZrycjFBcWvw7QC6R2',
        '88880002', 'ENTRENADOR', 1, NULL),
    (3, 'Sebastian', 'Fung', 'sebastian@fitsystem.com', 'sebastian',
        '$2a$10$e3kxxcOwPsZXqAmelYWHFuYKGZkwk4gt8dH6yZshuwR90FlNHaZxG',
        '88880003', 'CLIENTE', 1, NULL),
    (4, 'pruebaBC', 'prueba1', 'prueba@gmail.com', 'pruebaBC',
        '$2a$10$/HC6Oe209kwf7HYXKF0YhuR.Q6JF1cEU1mRcdRYReRhctMX7CqowK',
        '888', NULL, 1, NULL),
    (5, 'prueba2Cliente', 'prueba', 'prueba2@gmail.com',
        'prueba2Cliente',
        '$2a$10$bO.XksNRebKZZrNrMAy1.ujeldhDdEEw346u9vV8Il1hnB8XcNTzq',
        '1111', NULL, 1, NULL),
    (7, 'prueba3', 'prueba3', 'prueba3@gmail.com',
        'prueba3Entrenador',
        '$2a$10$.Jw8S9Eb2TJ7IAm8.C4K7esW9IU5tHJePPTBeLtQ53A1EBaqF8Ntm',
        '222', NULL, 1, NULL)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    apellidos = VALUES(apellidos),
    correo = VALUES(correo),
    username = VALUES(username),
    password = VALUES(password),
    telefono = VALUES(telefono),
    rol = VALUES(rol),
    activo = VALUES(activo),
    ruta_imagen = VALUES(ruta_imagen);

INSERT INTO usuario_rol (id_usuario, id_rol)
VALUES
    (1, 1),
    (2, 3),
    (3, 3),
    (4, 3),
    (5, 3),
    (7, 2)
ON DUPLICATE KEY UPDATE
    id_rol = VALUES(id_rol);

INSERT INTO membresia (
    id_membresia,
    id_usuario,
    id_plan,
    plan,
    monto,
    fecha_pago,
    fecha_inicio,
    fecha_vencimiento,
    estado
)
VALUES
    (1, 3, 2, 'Plan Premium', 18000.00,
        '2026-08-02', '2026-08-02', '2026-09-02', 'ACTIVA'),
    (2, 2, 3, 'Plan Completo', 250000.00,
        '2026-08-10', '2026-08-10', '2026-09-10', 'ACTIVA')
ON DUPLICATE KEY UPDATE
    id_usuario = VALUES(id_usuario),
    id_plan = VALUES(id_plan),
    plan = VALUES(plan),
    monto = VALUES(monto),
    fecha_pago = VALUES(fecha_pago),
    fecha_inicio = VALUES(fecha_inicio),
    fecha_vencimiento = VALUES(fecha_vencimiento),
    estado = VALUES(estado);

INSERT INTO pago (
    id_pago,
    id_usuario,
    id_plan,
    fecha_pago,
    fecha_proximo_pago,
    monto,
    metodo_pago
)
VALUES
    (1, 3, 2, '2026-08-02', '2026-09-02',
        18000.00, 'NO_REGISTRADO'),
    (2, 5, 3, '2026-08-09', '2026-09-09',
        25000.00, 'TRANSFERENCIA')
ON DUPLICATE KEY UPDATE
    id_usuario = VALUES(id_usuario),
    id_plan = VALUES(id_plan),
    fecha_pago = VALUES(fecha_pago),
    fecha_proximo_pago = VALUES(fecha_proximo_pago),
    monto = VALUES(monto),
    metodo_pago = VALUES(metodo_pago);

-- ============================================================
-- COMPROBACIÓN FINAL
-- ============================================================

SELECT 'Base de datos FitSystem instalada correctamente.' AS resultado;
SELECT id_rol, rol FROM rol ORDER BY id_rol;
SELECT id_plan, nombre, precio, activo
FROM plan_membresia
ORDER BY id_plan;
SELECT COUNT(*) AS usuarios_importados FROM usuario;
SELECT COUNT(*) AS membresias_importadas FROM membresia;
SELECT COUNT(*) AS pagos_importados FROM pago;
