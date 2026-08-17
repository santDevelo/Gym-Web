# FitSystem — Sistema web de gestión de gimnasios

FitSystem es una aplicación web transaccional para administrar usuarios, membresías,
pagos, rutinas de entrenamiento y asistencias de un gimnasio. Utiliza un único
dashboard cuyo contenido cambia según el rol autenticado: administrador, entrenador
o cliente. La interfaz está disponible en español, inglés y francés.

Proyecto final del curso SC-403 Desarrollo de Aplicaciones Web y Patrones,
Universidad Fidélitas, sede San Pedro.

## Integrantes

- Berrocal Siles Santiago Caleb
- Bourrouet Obregón Neytan Andry
- Fung Ramírez Sebastián
- Solís Méndez Gabriel Gerardo

## Tecnologías

- Java 25
- Spring Boot 4.1
- Spring MVC, Spring Security y Spring Data JPA
- Thymeleaf
- MySQL 8
- Bootstrap 5.3.7 y Font Awesome 7 mediante WebJars
- Firebase Storage para imágenes
- Maven
- Docker

## Módulos

| Módulo | Función |
|---|---|
| Portada pública | Presenta FitSystem y los planes Básico, Fit y Completo. |
| Autenticación | Inicio de sesión, registro de clientes, cierre de sesión y permisos por rol. |
| Dashboard | Panel único que muestra información y opciones según el rol autenticado. |
| Usuarios | Permite al administrador crear, modificar, activar y desactivar usuarios. |
| Membresías | Administra los planes y la membresía asignada a cada cliente. |
| Pagos | Registra manualmente el historial de pagos y actualiza la vigencia de la membresía. |
| Rutinas | Permite administrar rutinas y varios ejercicios por día. |
| Asistencias | Registra las entradas y salidas de los clientes. |
| Idiomas | Permite cambiar la interfaz entre español, inglés y francés. |

## Requisitos

- JDK 25
- Maven 3.9 o superior
- MySQL 8
- Git
- Docker, solamente si se desea ejecutar el proyecto en un contenedor

## Instalación local

### 1. Clonar la versión final

La versión integrada del proyecto se encuentra en la rama `master`:

```bash
git clone --branch master --single-branch https://github.com/santDevelo/Gym-Web.git
cd Gym-Web
```

### 2. Crear y poblar la base de datos

Ejecuta completamente el archivo final `sql/ScripFInalFitsysten.sql` desde MySQL
Workbench. El script crea la base `proyecto_final`, sus relaciones y los datos de
prueba requeridos por el sistema.

También puede ejecutarse desde una terminal que tenga disponible el cliente MySQL:

```bash
mysql -u root -p < sql/ScripFInalFitsysten.sql
```

El script crea estas 11 tablas:

```text
rol
usuario
usuario_rol
ruta
home
plan_membresia
membresia
pago
rutina
ejercicio_rutina
asistencia
```

### 3. Configurar MySQL

La aplicación obtiene la conexión desde variables de entorno:

| Variable | Ejemplo local |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/proyecto_final` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | Contraseña del usuario de MySQL |
| `PORT` | `96` |

Ejemplo temporal en PowerShell:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/proyecto_final"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_contraseña_mysql"
$env:PORT="96"
```

Si se ejecuta desde NetBeans, define estas variables en Windows y reinicia NetBeans
para que el proceso de Java pueda leerlas. No escribas contraseñas reales en
`application.properties`.

### 4. Configurar Firebase Storage

Firebase se utiliza para subir imágenes. La implementación actual lee el archivo JSON
de la cuenta de servicio desde los recursos de la aplicación. Genera tus propias
credenciales en Firebase y guarda el archivo dentro de
`src/main/resources/firebase/`. Después, configura estas propiedades con los datos de
tu proyecto:

```properties
firebase.bucket.name=tu-bucket-de-firebase
firebase.storage.path=fitsystem
firebase.json.path=firebase
firebase.json.file=tu-cuenta-de-servicio.json
```

El archivo JSON contiene una clave privada: no debe publicarse ni agregarse a Git.
Cada integrante debe utilizar sus propias credenciales locales.

### 5. Ejecutar

```bash
mvn spring-boot:run
```

Abre `http://localhost:96` en el navegador.

### 6. Ejecutar las pruebas

Las pruebas requieren acceso a la base `proyecto_final` configurada en las variables
de entorno anteriores:

```bash
mvn test
```

## Usuarios de prueba

Las contraseñas están almacenadas con BCrypt. Estas tres cuentas utilizan la
contraseña `1234`:

| Usuario | Rol | Contraseña |
|---|---|---|
| `amora` | Administrador | `1234` |
| `cruiz` | Entrenador | `1234` |
| `sebastian` | Cliente | `1234` |

## Rutas principales

| Ruta | Acceso |
|---|---|
| `/` y `/home` | Público |
| `/login`, `/registro` y `/acceso` | Público |
| `/dashboard` | Cualquier usuario autenticado |
| `/usuario/**` y `/admin/**` | Administrador |
| `/entrenador/**` | Entrenador |
| `/cliente/**` | Cliente |

El idioma puede cambiarse agregando `?lang=es`, `?lang=en` o `?lang=fr` a una ruta.

## Configuración en Render y Aiven

En Render configura las siguientes variables sin escribir sus valores en Git:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

`PORT` es proporcionado automáticamente por Render. `DB_URL` debe contener la URL
JDBC completa entregada para la base MySQL de Aiven.

La versión actual carga las credenciales de Firebase desde el classpath. Para un
despliegue público no se debe subir la cuenta de servicio al repositorio; la carga de
imágenes debe configurarse con un secreto privado del servicio antes de habilitarla
en producción.

## Ejecución con Docker

```bash
docker build -t fitsystem .
docker run --rm -p 96:96 \
  -e PORT=96 \
  -e DB_URL="jdbc:mysql://host:3306/proyecto_final" \
  -e DB_USERNAME="usuario" \
  -e DB_PASSWORD="contraseña" \
  fitsystem
```

## Recursos

- Prototipo en Figma: <https://www.figma.com/design/5frjIYrVXWQgQzg436DiJO/Desarrollo-web>
- Video de la propuesta: <https://youtu.be/JPHgVUjIohM>
- Aplicación desplegada: <https://gym-web-mlet.onrender.com>

El paquete final de entrega incluye además estos archivos:

- `ScripFInalFitsysten.sql`: creación y población completa de la base de datos.
- `Credenciales de prueba.txt`: cuentas de acceso para los roles implementados.
- `FitSystem_Articulo_Cientifico.docx`: artículo científico en formato IEEE.
- `FitSystem_Analisis_Mercado_IEEE.docx`: análisis de oferta de mercado.
- `FitSystem_Defensa_Proyecto.pptx`: presentación de defensa y demostración.

## Alcance actual

El sistema incluye autenticación, control de acceso, internacionalización, dashboard
por rol, usuarios, membresías, pagos, rutinas y asistencias. Los reportes gerenciales
y la reservación de citas no forman parte de esta versión.
