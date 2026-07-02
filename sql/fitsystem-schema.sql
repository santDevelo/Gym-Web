/*
  Script de creacion de base de datos para FitSystem
  Este script crea el esquema, tablas, usuario de aplicacion, y
  carga datos de ejemplo minimos para probar el Avance 2.
*/
-- Seccion de administracion (ejecutar una vez en un entorno de desarrollo)
drop database if exists fitsystem;
drop user if exists usuario_fitsystem;

-- Creacion del esquema
CREATE database fitsystem
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creacion del usuario de aplicacion.
-- IMPORTANTE: 'CAMBIAR_esta_clave123' es un valor de ejemplo, no una credencial real.
-- Cambiala aqui y define la misma clave en la variable de entorno DB_PASSWORD antes de ejecutar la app
-- (ver application.properties: spring.datasource.password=${DB_PASSWORD}).
create user 'usuario_fitsystem'@'%' identified by 'CAMBIAR_esta_clave123';

-- Asignacion de permisos (solo sobre la BD de la aplicacion, no privilegios globales)
grant select, insert, update, delete on fitsystem.* to 'usuario_fitsystem'@'%';
flush privileges;

use fitsystem;

-- --- Seccion de Creacion de Tablas ---

-- Tabla de usuarios (administradores, entrenadores y clientes)
create table usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  correo VARCHAR(100) NOT NULL,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(100) NOT NULL,
  telefono VARCHAR(20),
  rol ENUM('ADMINISTRADOR', 'ENTRENADOR', 'CLIENTE') NOT NULL,
  activo boolean,
  ruta_imagen varchar(1024),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  UNIQUE (correo),
  UNIQUE (username),
  index ndx_rol (rol))
  ENGINE = InnoDB;

-- Tabla de planes de membresia (catalogo)
create table membresia (
  id_membresia INT NOT NULL AUTO_INCREMENT,
  nombre_plan VARCHAR(50) NOT NULL,
  descripcion TEXT,
  precio decimal(12,2) CHECK (precio > 0),
  duracion_dias INT NOT NULL CHECK (duracion_dias > 0),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_membresia),
  UNIQUE (nombre_plan))
  ENGINE = InnoDB;

-- Tabla de suscripciones: relacion cliente <-> plan de membresia
create table cliente_membresia (
  id_cliente_membresia INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_membresia INT NOT NULL,
  fecha_inicio DATE NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_cliente_membresia),
  index ndx_usuario (id_usuario),
  index ndx_membresia (id_membresia),
  foreign key fk_clienteMembresia_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_clienteMembresia_membresia (id_membresia) references membresia(id_membresia))
  ENGINE = InnoDB;

-- Tabla de pagos (registra abonos y extiende la suscripcion asociada)
create table pago (
  id_pago INT NOT NULL AUTO_INCREMENT,
  id_cliente_membresia INT NOT NULL,
  monto decimal(12,2) CHECK (monto > 0),
  fecha_pago DATE NOT NULL,
  metodo_pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA') NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pago),
  index ndx_cliente_membresia (id_cliente_membresia),
  index ndx_fecha_pago (fecha_pago),
  foreign key fk_pago_clienteMembresia (id_cliente_membresia) references cliente_membresia(id_cliente_membresia))
  ENGINE = InnoDB;

-- Tabla de rutinas asignadas por un entrenador a un cliente
create table rutina (
  id_rutina INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  descripcion TEXT,
  id_entrenador INT NOT NULL,
  id_cliente INT NOT NULL,
  fecha_asignacion DATE NOT NULL,
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rutina),
  index ndx_entrenador (id_entrenador),
  index ndx_cliente (id_cliente),
  foreign key fk_rutina_entrenador (id_entrenador) references usuario(id_usuario),
  foreign key fk_rutina_cliente (id_cliente) references usuario(id_usuario))
  ENGINE = InnoDB;

-- --- Seccion de Insercion de Datos de ejemplo ---

-- Usuarios: 1 administrador, 1 entrenador, 2 clientes
-- Contrasenas de ejemplo en texto plano (el material de las semanas 1-8 no cubre hashing/Spring Security).
INSERT INTO usuario (nombre, apellidos, correo, username, password, telefono, rol, activo) VALUES
('Ana', 'Mora Solis', 'ana.mora@fitsystem.com', 'amora', 'clave123', '8888-0001', 'ADMINISTRADOR', true),
('Carlos', 'Ruiz Perez', 'carlos.ruiz@fitsystem.com', 'cruiz', 'clave123', '8888-0002', 'ENTRENADOR', true),
('Sebastian', 'Rojas Vega', 'sebastian.rojas@fitsystem.com', 'srojas', 'clave123', '8888-0003', 'CLIENTE', true),
('Maria', 'Chaves Leon', 'maria.chaves@fitsystem.com', 'mchaves', 'clave123', '8888-0004', 'CLIENTE', true);

-- Planes de membresia (nombres tomados del prototipo del Avance 1)
INSERT INTO membresia (nombre_plan, descripcion, precio, duracion_dias, activo) VALUES
('Plan Basico', 'Entrena en una sede, acceso a maquinas basicas, uso de zona premium (spa/recuperacion)', 8000, 30, true),
('Plan Completo', 'Entrena en todas las sedes, acceso ilimitado a todas las areas, clases grupales ilimitadas, rutinas personalizadas', 25000, 30, true);

-- Suscripcion de ejemplo (Sebastian con el Plan Completo)
INSERT INTO cliente_membresia (id_usuario, id_membresia, fecha_inicio, fecha_vencimiento) VALUES
(3, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY));

-- Pago de ejemplo asociado a esa suscripcion
INSERT INTO pago (id_cliente_membresia, monto, fecha_pago, metodo_pago) VALUES
(1, 25000, CURDATE(), 'TARJETA');

-- Rutina de ejemplo asignada por el entrenador Carlos al cliente Sebastian
INSERT INTO rutina (nombre, descripcion, id_entrenador, id_cliente, fecha_asignacion, activo) VALUES
('Hipertrofia avanzada', 'Rutina de 5 dias enfocada en fuerza maxima e hipertrofia', 2, 3, CURDATE(), true);
