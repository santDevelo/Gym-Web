# Gym-Web

Sistema de Gestión de Gimnasio

**Descripción:** Sistema web diseñado para la gestión de gimnasios, permitiendo administrar usuarios, clientes y membresías, con el objetivo de optimizar los procesos administrativos y mejorar la organización del servicio. Esto mediante el desarrollo de una plataforma que facilite la gestión de clientes y el control de membresías dentro del gimnasio, mediante un sistema organizado y basado en roles.

## Integrantes del equipo

- BERROCAL SILES SANTIAGO CALEB
- BOURROUET OBREGON NEYTAN ANDRY
- FUNG RAMIREZ SEBASTIAN
- SOLIS MENDEZ GABRIEL GERARDO

## Módulos principales

- **Home**: página pública con la imagen del gimnasio y los planes de membresía disponibles.
- **Autenticación de usuarios**: login independiente por rol (cliente, entrenador, administrador) con sesión real (`HttpSession`), registro público de clientes y cierre de sesión (`/logout`).
- **Dashboard por rol**: cada rol tiene su propia ruta protegida por sesión (`/admin`, `/cliente`, `/entrenador`); si no hay sesión activa o el rol no coincide, redirige de vuelta al login correspondiente.
- **Panel de administrador**: tarjetas de resumen, gráfica de ingresos y de asistencias.

## Cómo ejecutarlo

1. Java 17+ y Maven.
2. MySQL corriendo en `localhost:3306`. Crear la base de datos `proyecto_final` (usuario `admin`/`fitsystem`) y la tabla `usuario` (ver `src/main/resources/ScripSQL`).
3. Ejecutar `mvn spring-boot:run`. La app queda en `http://localhost:96`.
4. Rutas: `/` (home), `/login/cliente`, `/login/entrenador`, `/login/admin`, `/registro`, `/logout`.

## Control de versiones por ramas

- **main**: versión estable del sistema (esta es la versión documentada aquí).
- **develop**: rama de desarrollo.
- **feature-auth**: desarrollo de autenticación (registro, login y roles).
- **feature-clientes**: gestión de clientes y su información.
- **feature-membresias**: gestión de planes de membresía.

## Estado del proyecto

En desarrollo. Home, autenticación por rol con sesión real, y dashboard de administrador (resumen, ingresos, asistencias) ya implementados en `main`. Pendiente: completar el contenido de los dashboards de cliente y entrenador, y la gestión de membresías/pagos/rutinas.
