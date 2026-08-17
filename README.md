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

- Java 17
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

- JDK 17
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

### 2. Crear la base de datos

Ejecuta el archivo `sql/replicar_base_datos_fitsystem.sql` completo desde MySQL
Workbench. El script crea la base `proyecto_final`, sus relaciones y los datos de
prueba.

También puede ejecutarse desde una terminal que tenga disponible el cliente MySQL:

```bash
mysql -u root -p < sql/replicar_base_datos_fitsystem.sql
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

Firebase es opcional para iniciar el sistema, pero es necesario para subir imágenes.
Nunca guardes el archivo JSON de la cuenta de servicio dentro del repositorio.

Para desarrollo local, guarda el JSON fuera de la carpeta del proyecto y define:

```powershell
$env:FIREBASE_ENABLED="true"
$env:FIREBASE_BUCKET_NAME="fitsystem-c876c.firebasestorage.app"
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\ruta-segura\firebase-service-account.json"
```

Si no vas a probar la carga de imágenes, utiliza:

```powershell
$env:FIREBASE_ENABLED="false"
```

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
FIREBASE_ENABLED
FIREBASE_BUCKET_NAME
FIREBASE_CREDENTIALS_BASE64
```

`PORT` es proporcionado automáticamente por Render. `DB_URL` debe contener la URL
JDBC completa entregada para la base MySQL de Aiven.

Para convertir la nueva cuenta de servicio de Firebase a Base64 desde PowerShell:

```powershell
[Convert]::ToBase64String(
    [IO.File]::ReadAllBytes("C:\ruta-segura\firebase-service-account.json")
)
```

Copia el resultado únicamente en la variable `FIREBASE_CREDENTIALS_BASE64` de
Render. No lo publiques ni lo agregues al código.

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

## Alcance actual

El sistema incluye autenticación, control de acceso, internacionalización, dashboard
por rol, usuarios, membresías, pagos, rutinas y asistencias. Los reportes gerenciales
y la reservación de citas no forman parte de esta versión.
