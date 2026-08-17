
# FitSystem — Sistema Web de Gestión de Gimnasios

Sistema web transaccional para la administración integral de un gimnasio: usuarios,
membresías, pagos, rutinas de entrenamiento y control de asistencia, con acceso
diferenciado por rol (Administrador, Entrenador, Cliente) e interfaz disponible en
español, inglés y francés.

Proyecto final — SC-403 Desarrollo de Aplicaciones Web y Patrones, Universidad
Fidélitas, Sede San Pedro.

## Integrantes del equipo

- Berrocal Siles Santiago Caleb
- Bourrouet Obregon Neytan Andry
- Fung Ramirez Sebastian
- Solis Mendez Gabriel Gerardo

## Stack tecnológico

- **Backend:** Java 17, Spring Boot 4.1, Spring MVC, Spring Security, Spring Data JPA
  (Hibernate)
- **Vistas:** Thymeleaf + `thymeleaf-extras-springsecurity6`
- **Base de datos:** MySQL 8
- **Interfaz:** Bootstrap 5.3.7 + Font Awesome 7 (vía WebJars)
- **Almacenamiento de imágenes:** Firebase Storage (Google Cloud Storage)
- **Internacionalización:** `spring.messages` + `messages_es/en/fr.properties`
- **Build:** Maven

## Módulos principales

| Módulo | Descripción |
|---|---|
| **Portada pública** | Landing con los 3 planes de membresía (Básico, Fit, Completo). |
| **Autenticación** | Login único con `Spring Security`, registro público de clientes, cierre de sesión, control de acceso dinámico por rol (tabla `ruta`), selector de idioma es/en/fr persistente en sesión. |
| **Dashboard** | Un panel único (`/dashboard`) cuyo contenido cambia según el rol autenticado — sin controladores duplicados por rol. |
| **Administración de usuarios** | Alta, edición, activar/desactivar usuarios (admin, entrenador o cliente), con foto de perfil vía Firebase Storage. |
| **Membresías** | Catálogo de planes editable y asignación de plan/vigencia/estado por cliente; el cliente puede cancelar la propia. |
| **Pagos** | Historial manual de pagos; registrar un pago actualiza automáticamente la vigencia de la membresía (módulo transaccional). |
| **Rutinas** | El entrenador crea rutinas y administra ejercicios; el cliente consulta y también puede agregar ejercicios a su rutina activa. |
| **Asistencia** | El cliente registra su propia entrada/salida; se usa para los indicadores de los tres roles. |

## Instalación y ejecución local

### Requisitos

- JDK 17 o superior
- Maven 3.9+
- MySQL 8 (servidor corriendo en `localhost:3306`, o accesible por red)

### 1. Clonar el repositorio

```bash
git clone https://github.com/santDevelo/Gym-Web.git
cd Gym-Web
```

### 2. Crear y poblar la base de datos

Ejecuta uno de los dos scripts equivalentes de la carpeta `sql/` con un cliente MySQL
(Workbench, `mysql` CLI, etc.):

- `sql/base_datos_fitsystem.sql` — crea también un usuario MySQL local `admin`/`fitsystem`.
- `sql/replicar_base_datos_fitsystem.sql` — no crea usuarios (para MySQL administrado /
  Aiven); usa las credenciales que ya tengas configuradas.

Ambos crean la base `proyecto_final`, sus 11 tablas y los datos base necesarios
(roles, rutas protegidas, planes de membresía y usuarios de prueba).

```bash
mysql -u root -p < sql/replicar_base_datos_fitsystem.sql
```

### 3. Configurar la conexión

La app lee la configuración de `src/main/resources/application.properties`, con
valores por defecto sobreescribibles por variables de entorno:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/proyecto_final}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:<tu-contraseña>}
server.port=${PORT:96}
```

Para producción (por ejemplo Render + una base MySQL administrada), define
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `PORT` como variables de entorno en vez de
tocar el archivo.

### 4. Ejecutar

```bash
mvn spring-boot:run
```

La aplicación queda disponible en **http://localhost:96** (o el puerto indicado por
la variable `PORT`).

## Rutas principales

| Ruta | Acceso |
|---|---|
| `/`, `/home` | Pública |
| `/login`, `/registro`, `/acceso` | Pública |
| `/dashboard` | Cualquier rol autenticado (contenido según el rol) |
| `/usuario/**`, `/admin/**` | ADMINISTRADOR |
| `/entrenador/**` | ENTRENADOR |
| `/cliente/**` | CLIENTE |
| `?lang=es` \| `?lang=en` \| `?lang=fr` | Cambia el idioma en cualquier ruta |

## Usuarios de prueba

Todas las cuentas de prueba creadas por el script SQL usan la misma contraseña:
**`1234`**.

| Usuario | Rol | Contraseña |
|---|---|---|
| `amora` | ADMINISTRADOR | `1234` |
| `cruiz` | ENTRENADOR | `1234` |
| `sebastian` | CLIENTE | `1234` |

## Control de versiones por ramas

- **main** — versión estable del sistema.
- **develop** — rama de integración de trabajo en curso.
- **feature-auth** — autenticación, roles y control de acceso.
- **feature-clientes** — gestión de clientes.
- **feature-membresias** — planes y membresías.

## Documentación y anexos

- **Artículo científico (IEEE):** `FitSystem_Articulo_Cientifico_EA25.docx`
- **Prototipo en Figma:** https://www.figma.com/design/5frjIYrVXWQgQzg436DiJO/Desarrollo-web
- **Video de resumen de la propuesta:** https://youtu.be/JPHgVUjIohM
- **Aplicación desplegada:** https://gym-web-mlet.onrender.com 

## Estado del proyecto

Funcional: autenticación con roles y control dinámico de acceso, internacionalización
(es/en/fr), dashboard único por rol, gestión de usuarios/clientes/empleados,
membresías, historial de pagos, rutinas de entrenamiento y control de asistencia.
Fuera de alcance en esta versión: reportes gerenciales y reservación de citas con
entrenadores (retirados explícitamente durante el desarrollo).
