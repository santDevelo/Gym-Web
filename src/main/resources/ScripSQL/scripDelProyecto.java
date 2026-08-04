//-- ============================================
//-- Creación de la base de datos
//-- ============================================
//CREATE DATABASE proyecto_final;
//USE proyecto_final;
//
//-- ============================================
//-- Creación de tablas
//-- ============================================
//CREATE TABLE home (
//    id BIGINT AUTO_INCREMENT PRIMARY KEY,
//    imagen_url VARCHAR(500) NOT NULL
//);
//
//CREATE TABLE usuario (
//    id_usuario INT NOT NULL AUTO_INCREMENT,
//    nombre VARCHAR(50) NOT NULL,
//    apellidos VARCHAR(80) NOT NULL,
//    correo VARCHAR(100) NOT NULL,
//    username VARCHAR(30) NOT NULL,
//    password VARCHAR(100) NOT NULL,
//    telefono VARCHAR(20),
//    rol ENUM('ADMINISTRADOR','ENTRENADOR','CLIENTE') NOT NULL,
//    activo BOOLEAN,
//    ruta_imagen VARCHAR(1024),
//    PRIMARY KEY (id_usuario)
//);
//
//CREATE TABLE membresia (
//    id_membresia INT NOT NULL AUTO_INCREMENT,
//    id_usuario INT NOT NULL,
//    plan VARCHAR(50) NOT NULL,
//    monto DECIMAL(10,2) NOT NULL,
//    fecha_pago DATE,
//    estado ENUM('ACTIVA','PENDIENTE','VENCIDA') NOT NULL,
//    PRIMARY KEY (id_membresia),
//    CONSTRAINT fk_membresia_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
//);
//
//-- ============================================
//-- Datos iniciales
//-- ============================================
//INSERT INTO home (imagen_url) VALUES (
//    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQiDmsisrTYfA_ot_y4jBirYO4z8c8i2YYb-zLAnNb_nQ&s=10'
//);
//
//INSERT INTO usuario
//    (nombre, apellidos, correo, username, password, telefono, rol, activo, ruta_imagen)
//VALUES
//    ('Ana', 'Mora Solis', 'ana@fitsystem.com', 'amora', '1234', '88880001', 'ADMINISTRADOR', true, NULL),
//    ('Carlos', 'Ruiz Perez', 'carlos@fitsystem.com', 'cruiz', '1234', '88880002', 'ENTRENADOR', true, NULL),
//    ('Sebastian', 'Fung', 'sebastian@fitsystem.com', 'sebastian', '1234', '88880003', 'CLIENTE', true, NULL);
//
//INSERT INTO membresia (id_usuario, plan, monto, fecha_pago, estado)
//VALUES (3, 'Plan Premium', 18000.00, CURDATE(), 'ACTIVA');
//
//-- ============================================
//-- Usuario de la aplicación y permisos
//-- ============================================
//CREATE USER 'admin'@'localhost' IDENTIFIED BY 'fitsystem';
//GRANT ALL PRIVILEGES ON proyecto_final.* TO 'admin'@'localhost';
//FLUSH PRIVILEGES;
//
//-- ============================================
//-- Consultas de verificación
//-- ============================================
//SHOW DATABASES;
//SHOW TABLES;
//DESCRIBE usuario;
//DESCRIBE membresia;
//
//SELECT * FROM home;
//
//SELECT id_usuario, nombre, username, password, rol
//FROM usuario;
//
//SELECT m.id_membresia, u.nombre, m.plan, m.monto, m.fecha_pago, m.estado
//FROM membresia m
//JOIN usuario u ON u.id_usuario = m.id_usuario;
//
//
//
//USE proyecto_final;
//
//SELECT username, COUNT(*) AS cantidad
//FROM usuario
//GROUP BY username
//HAVING COUNT(*) > 1;
//
//SELECT correo, COUNT(*) AS cantidad
//FROM usuario
//WHERE correo IS NOT NULL
//GROUP BY correo
//HAVING COUNT(*) > 1;
//
//ALTER TABLE usuario
//MODIFY COLUMN password VARCHAR(512) NOT NULL;
//
//ALTER TABLE usuario
//MODIFY COLUMN password VARCHAR(512) NOT NULL;
//
//ALTER TABLE usuario
//ADD COLUMN fecha_creacion
//    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//ADD COLUMN fecha_modificacion
//    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//    ON UPDATE CURRENT_TIMESTAMP;
//    
//    ALTER TABLE usuario
//ADD CONSTRAINT uk_usuario_username
//    UNIQUE (username),
//ADD CONSTRAINT uk_usuario_correo
//    UNIQUE (correo);
//    
//    CREATE TABLE rol (
//    id_rol INT NOT NULL AUTO_INCREMENT,
//    rol VARCHAR(20) UNIQUE,
//    fecha_creacion
//        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//    fecha_modificacion
//        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//        ON UPDATE CURRENT_TIMESTAMP,
//
//    PRIMARY KEY (id_rol)
//) ENGINE = InnoDB;
//
//INSERT INTO rol (rol)
//VALUES
//('ADMINISTRADOR'),
//('ENTRENADOR'),
//('CLIENTE');
//
//CREATE TABLE usuario_rol (
//    id_usuario INT NOT NULL,
//    id_rol INT NOT NULL,
//    fecha_creacion
//        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//    fecha_modificacion
//        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//        ON UPDATE CURRENT_TIMESTAMP,
//
//    PRIMARY KEY (id_usuario, id_rol),
//
//    CONSTRAINT fk_usuarioRol_usuario
//        FOREIGN KEY (id_usuario)
//        REFERENCES usuario (id_usuario),
//
//    CONSTRAINT fk_usuarioRol_rol
//        FOREIGN KEY (id_rol)
//        REFERENCES rol (id_rol)
//) ENGINE = InnoDB;
//
//INSERT INTO usuario_rol (
//    id_usuario,
//    id_rol
//)
//SELECT
//    u.id_usuario,
//    r.id_rol
//FROM usuario u
//INNER JOIN rol r
//    ON r.rol = u.rol;
//    
//    SELECT
//    u.id_usuario,
//    u.username,
//    r.rol
//FROM usuario u
//INNER JOIN usuario_rol ur
//    ON u.id_usuario = ur.id_usuario
//INNER JOIN rol r
//    ON ur.id_rol = r.id_rol
//ORDER BY u.id_usuario;
//
//
//ALTER TABLE usuario
//MODIFY COLUMN rol ENUM(
//    'ADMINISTRADOR',
//    'ENTRENADOR',
//    'CLIENTE'
//) NULL;
//
//CREATE TABLE ruta (
//    id_ruta INT NOT NULL AUTO_INCREMENT,
//    ruta VARCHAR(255) NOT NULL,
//    id_rol INT NULL,
//    requiere_rol BOOLEAN NOT NULL DEFAULT TRUE,
//    fecha_creacion
//        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//    fecha_modificacion
//        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//        ON UPDATE CURRENT_TIMESTAMP,
//
//    PRIMARY KEY (id_ruta),
//
//    CONSTRAINT chk_ruta_rol
//        CHECK (
//            id_rol IS NOT NULL
//            OR requiere_rol = FALSE
//        ),
//
//    CONSTRAINT fk_ruta_rol
//        FOREIGN KEY (id_rol)
//        REFERENCES rol (id_rol)
//) ENGINE = InnoDB;
//
//
//INSERT INTO ruta (
//    ruta,
//    id_rol
//)
//VALUES
//(
//    '/admin/**',
//    (
//        SELECT id_rol
//        FROM rol
//        WHERE rol = 'ADMINISTRADOR'
//    )
//),
//(
//    '/entrenador/**',
//    (
//        SELECT id_rol
//        FROM rol
//        WHERE rol = 'ENTRENADOR'
//    )
//),
//(
//    '/cliente/**',
//    (
//        SELECT id_rol
//        FROM rol
//        WHERE rol = 'CLIENTE'
//    )
//);
//
//
//INSERT INTO ruta (
//    ruta,
//    requiere_rol
//)
//VALUES
//('/', FALSE),
//('/home', FALSE),
//('/acceso', FALSE),
//('/login', FALSE),
//('/registro', FALSE),
//('/registro/**', FALSE),
//('/error', FALSE),
//('/css/**', FALSE),
//('/js/**', FALSE),
//('/img/**', FALSE),
//('/webjars/**', FALSE),
//('/favicon.ico', FALSE);
//
//SELECT
//    ru.id_ruta,
//    ru.ruta,
//    ru.requiere_rol,
//    r.rol
//FROM ruta ru
//LEFT JOIN rol r
//    ON ru.id_rol = r.id_rol
//ORDER BY
//    ru.requiere_rol,
//    ru.id_ruta;
//    
//#usar despues de migrar las clases de java
//    ALTER TABLE usuario DROP COLUMN rol;
//    
//    
//    INSERT INTO ruta (
//    ruta,
//    requiere_rol
//)
//SELECT
//    '/403',
//    FALSE
//WHERE NOT EXISTS (
//    SELECT 1
//    FROM ruta
//    WHERE ruta = '/403'
//);
//
//SELECT
//    username,
//    LEFT(password, 7) AS inicio_password,
//    LENGTH(password) AS longitud
//FROM usuario;
//
//SELECT *
//FROM rol;
//
//
//INSERT INTO ruta (
//    ruta,
//    id_rol,
//    requiere_rol
//)
//SELECT
//    '/usuario/**',
//    id_rol,
//    TRUE
//FROM rol
//WHERE rol = 'ADMINISTRADOR'
//  AND NOT EXISTS (
//      SELECT 1
//      FROM ruta
//      WHERE ruta = '/usuario/**'
//  );
//  
//  SELECT
//    ruta.ruta,
//    rol.rol,
//    ruta.requiere_rol
//FROM ruta
//LEFT JOIN rol
//    ON ruta.id_rol = rol.id_rol
//WHERE ruta.ruta = '/usuario/**';