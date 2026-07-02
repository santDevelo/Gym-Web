# FitSystem — Avance 2

Sistema Web Orientado a la Gestion de Gimnasios (ver `../Avance1 DesarrolloAplicacionesWeb.md`). Implementacion funcional del 50% para el Avance 2 del curso SC-403 Desarrollo de Aplicaciones Web y Patrones.

## Stack

Spring Boot + Thymeleaf + Bootstrap 5 + JPA/Hibernate + MySQL + Firebase Storage (foto de perfil de Usuario), siguiendo exactamente los patrones de las semanas 1-8 del curso (mismo patron de 4 capas Entidad -> Repository -> Service -> Controller, y el mismo patron de subida de imagen de la semana 4, que el proyecto `tienda` de referencia). Sin Spring Security (no cubierto en el material del curso hasta la fecha).

## Requisitos de ejecucion

1. **Java 17+** y **Maven**.
2. **MySQL** corriendo en `localhost:3306`.
3. Crear la base de datos ejecutando el script `sql/fitsystem-schema.sql` (crea la BD `fitsystem`, el usuario `usuario_fitsystem` y datos de ejemplo). **Cambia la contraseña de ejemplo del script** (`CAMBIAR_esta_clave123`) antes de usarlo.
4. Definir la variable de entorno `DB_PASSWORD` con la misma contraseña que pusiste en el script (nunca se guarda en texto plano en `application.properties`):
   - PowerShell: `$env:DB_PASSWORD = "tu_clave"`
   - bash: `export DB_PASSWORD=tu_clave`
5. **Credencial de Firebase**: el archivo `src/main/resources/firebase/fitsystem-c876c-firebase-adminsdk-fbsvc-4091d316a3.json` está excluido de git (`.gitignore`) porque es una credencial real. Cada integrante que clone el repo debe copiar ese archivo manualmente en esa ruta (pídelo aparte, no por chat/GitHub) antes de correr la app.
6. Ejecutar `mvn spring-boot:run` (o `mvn spring-boot:run -DskipTests`). La app queda en `http://localhost:8082`.

## Estado del avance (historias de usuario del Avance 1)

13 de 20 historias implementadas (65%), priorizando todas las de prioridad Alta:

| # | Historia | Estado |
|---|---|---|
| 1 | Admin registra usuarios | Implementado (`/usuario`) |
| 2 | Admin asigna roles | Implementado (campo `rol` en Usuario) |
| 3 | Admin gestiona empleados | **Omitido**: el Avance 1 no define campos propios para "empleado" |
| 4 | Admin registra clientes | Implementado (Usuario con rol CLIENTE) |
| 5 | Admin crea/administra membresias | Implementado (`/membresia`) |
| 6 | Ver estado de membresias | Implementado (`/suscripcion`, estado calculado ACTIVA/VENCIDA/PENDIENTE) |
| 7 | Consultar fechas de pago | Implementado (`/pago`, incluye consulta derivada/JPQL/SQL nativa por rango de fechas, semana 8) |
| 8 | Registrar pagos manualmente | Implementado (extiende automaticamente la suscripcion) |
| 9-10 | Reportes de ingresos/asistencia | Omitido (prioridad Baja) |
| 11 | Entrenador ve sus clientes | Implementado (`/perfil/{idUsuario}/clientes`) |
| 12 | Entrenador asigna rutinas | Implementado (`/rutina`) |
| 13 | Entrenador actualiza rutinas | Implementado |
| 14-15 | Historial de asistencia / agenda de citas del entrenador | Omitido (prioridad Baja, sin datos suficientes en el Avance 1) |
| 16 | Cliente se registra | Implementado (`/registro`) — **parcial**: guarda el usuario, pero no hay "acceder con sus credenciales" real (ver nota abajo) |
| 17 | Cliente consulta estado de membresia | Implementado (`/perfil/{idUsuario}/membresia`) |
| 18 | Cliente reserva citas | Omitido (prioridad Baja) |
| 19 | Cliente ve sus rutinas | Implementado (`/perfil/{idUsuario}/rutinas`) |
| 20 | Cliente ve historial de asistencia | Omitido (prioridad Baja, no hay entidad de asistencia) |

### Nota sobre login/autenticacion

El material del curso (semanas 1-8) no cubre Spring Security ni manejo de sesiones. Las vistas "de cliente" o "de entrenador" (historias 11, 17, 19) se resuelven navegando a `/perfil/{idUsuario}/...`, replicando el patron ya usado en `tienda` (`IndexController`, `/consultas/{idCategoria}`) en vez de inventar un sistema de autenticacion no visto en clase. No hay contraseñas cifradas ni control de acceso por rol.

### Pendiente (fuera del alcance de este avance)

- **Git/GitHub colaborativo**: el repositorio (https://github.com/neytan182006/Proyectofit) ya tiene el primer commit en `main`, pero el criterio de "Participacion en GitHub" (30% de la rubrica) requiere que los 4 integrantes trabajen con ramas y pull requests reales, lo cual queda pendiente de que el equipo lo configure a partir de aqui.

## Estructura del proyecto

Igual a `tienda`: `domain/` (entidades JPA), `repository/` (Spring Data JPA), `service/` (logica de negocio, `@Transactional`), `controller/` (Spring MVC), `templates/` (Thymeleaf + Bootstrap, un `fragmentos.html` por entidad con los fragments `agregar`/`listado`/`editar`/`confirmarEliminar`).
