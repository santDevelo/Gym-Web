//CREATE DATABASE proyecto_final;
//USE proyecto_final;
//
//CREATE TABLE home (
//    id BIGINT AUTO_INCREMENT PRIMARY KEY,
//    imagen_url VARCHAR(500) NOT NULL
//);
//
//INSERT INTO home (imagen_url) VALUES (
//   'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQiDmsisrTYfA_ot_y4jBirYO4z8c8i2YYb-zLAnNb_nQ&s=10'
//);
//CREATE USER 'admin'@'localhost' IDENTIFIED BY 'fitsystem';
//GRANT ALL PRIVILEGES ON proyecto_final.* TO 'admin'@'localhost';
//-- Aplicar cambios
//FLUSH PRIVILEGES;
//
//SELECT * FROM home;
//
//USE proyecto_final;
//
//SELECT id_usuario, username, password, rol
//FROM usuario;
//
//SHOW DATABASES;
//USE proyecto_final;
//
//SHOW TABLES;
//
//USE proyecto_final;
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
//INSERT INTO usuario
//(nombre, apellidos, correo, username, password, telefono, rol, activo, ruta_imagen)
//VALUES
//('Ana','Mora Solis','ana@fitsystem.com','amora','1234','88880001','ADMINISTRADOR',true,NULL),
//('Carlos','Ruiz Perez','carlos@fitsystem.com','cruiz','1234','88880002','ENTRENADOR',true,NULL),
//('Sebastian','Fung','sebastian@fitsystem.com','sebastian','1234','88880003','CLIENTE',true,NULL);
//
//DESCRIBE usuario;
//
//
//SELECT id_usuario, nombre, username, password, rol
//FROM usuario;