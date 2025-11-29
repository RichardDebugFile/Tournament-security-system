# 🚀 Guía de Inicio Rápido - Tournament Security System

> **Guía completa para desarrolladores que abren el proyecto por primera vez**

Esta guía te ayudará a configurar y ejecutar el proyecto Tournament Security System desde cero en tu máquina local.

---

## 📋 Tabla de Contenidos

1. [Pre-requisitos](#-pre-requisitos)
2. [Instalación de Dependencias](#-instalación-de-dependencias)
3. [Configuración del Proyecto](#-configuración-del-proyecto)
4. [Iniciar los Servicios](#-iniciar-los-servicios)
5. [Verificar la Instalación](#-verificar-la-instalación)
6. [Acceder a la Aplicación](#-acceder-a-la-aplicación)
7. [Solución de Problemas](#-solución-de-problemas)

---

## 🔧 Pre-requisitos

Antes de comenzar, asegúrate de tener instalado:

### Requerimientos Obligatorios

| Software | Versión Mínima | Comando de Verificación | Descarga |
|----------|----------------|-------------------------|----------|
| **Java JDK** | 17+ | `java -version` | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| **Maven** | 3.6+ | `mvn -version` | [Apache Maven](https://maven.apache.org/download.cgi) |
| **Docker Desktop** | 4.0+ | `docker --version` | [Docker Desktop](https://www.docker.com/products/docker-desktop) |
| **Git** | 2.0+ | `git --version` | [Git SCM](https://git-scm.com/downloads) |

### Requerimientos Opcionales

| Software | Propósito | Comando de Verificación |
|----------|-----------|-------------------------|
| **.NET 8 SDK** | Backend microservices (.NET) | `dotnet --version` |
| **Node.js** | Herramientas de desarrollo | `node --version` |
| **PostgreSQL Client** | Gestión de BD Keycloak | `psql --version` |

---

## 📦 Instalación de Dependencias

### 1. Verificar Java 17

```bash
java -version
# Deberías ver: java version "17.x.x" o superior
```

Si no tienes Java 17, descárgalo e instálalo desde [Oracle JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

### 2. Verificar Maven

```bash
mvn -version
# Deberías ver: Apache Maven 3.x.x
```

**Nota:** El proyecto incluye Maven Wrapper (`mvnw`), así que **NO es obligatorio** tener Maven instalado globalmente. Puedes usar `./mvnw` en su lugar.

### 3. Verificar Docker

```bash
docker --version
docker-compose --version
```

Asegúrate de que **Docker Desktop esté corriendo** antes de continuar.

---

## ⚙️ Configuración del Proyecto

### Paso 1: Clonar el Repositorio

```bash
git clone <repository-url>
cd tournament-security-system
```

### Paso 2: Verificar la Estructura del Proyecto

Tu estructura de carpetas debería verse así:

```
tournament-security-system/
├── backend/                    # Backend .NET (opcional)
├── frontend/                   # Frontend Spring Boot (PRINCIPAL)
│   └── IngWeb/                 # Aplicación web
│       ├── src/
│       ├── pom.xml
│       ├── mvnw                # Maven Wrapper (Unix/Mac)
│       └── mvnw.cmd            # Maven Wrapper (Windows)
├── docker-compose.yml          # Configuración de servicios Docker
├── docs/                       # Documentación
└── README.md
```

### Paso 3: Configurar Variables de Entorno (Opcional)

Crea un archivo `.env` en la raíz del proyecto (si no existe):

```bash
# .env
SQL_SA_PASSWORD=YourStrong@Passw0rd
```

**Nota:** Las credenciales por defecto ya están configuradas en `docker-compose.yml`. Este paso es opcional.

---

## 🐳 Iniciar los Servicios

Sigue estos pasos **EN ORDEN** para iniciar correctamente todos los servicios.

### Paso 1: Iniciar Servicios Docker

Abre una terminal en la raíz del proyecto y ejecuta:

```bash
# Iniciar todos los servicios en segundo plano
docker-compose up -d

# O iniciar solo servicios específicos
docker-compose up -d sqlserver keycloak-db keycloak
```

**Servicios que se inician:**
- ✅ **SQL Server** (puerto 1433) - Base de datos principal
- ✅ **PostgreSQL** (puerto 5432) - Base de datos de Keycloak
- ✅ **Keycloak** (puerto 8090) - Servidor de autenticación

#### Verificar que los servicios estén corriendo:

```bash
docker ps
```

Deberías ver algo como:

```
CONTAINER NAME              STATUS                    PORTS
tournament-sqlserver        Up 2 minutes (healthy)    0.0.0.0:1433->1433/tcp
tournament-keycloak-db      Up 2 minutes (healthy)    5432/tcp
tournament-keycloak         Up About a minute         0.0.0.0:8090->8080/tcp
```

⏱️ **Tiempo de espera:** Keycloak tarda **1-2 minutos** en iniciar completamente. Espera hasta que veas `(healthy)` en el status.

### Paso 2: Verificar que Keycloak está Listo

Abre tu navegador y ve a:

```
http://localhost:8090
```

Deberías ver la página de bienvenida de Keycloak. Si ves un error de conexión, espera 30 segundos más e intenta de nuevo.

### Paso 3: Iniciar el Frontend Spring Boot

Abre una **nueva terminal** y ejecuta:

#### En Windows:
```bash
cd frontend/IngWeb
mvnw.cmd spring-boot:run
```

#### En Linux/Mac:
```bash
cd frontend/IngWeb
./mvnw spring-boot:run
```

#### Usando Maven global (si lo tienes instalado):
```bash
cd frontend/IngWeb
mvn spring-boot:run
```

**Primera ejecución:** Maven descargará todas las dependencias. Esto puede tomar **3-5 minutos** la primera vez.

#### Logs esperados:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.4.4)

...
2025-11-27 15:45:23.456  INFO --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2025-11-27 15:45:23.789  INFO --- [  restartedMain] c.d.c.s.w.s.Application                 : Started Application in 12.345 seconds
```

✅ **Listo:** Cuando veas `Started Application`, el frontend está corriendo.

---

## ✅ Verificar la Instalación

### Servicios Corriendo

Verifica que todos los servicios estén activos:

| Servicio | URL | Status Esperado |
|----------|-----|-----------------|
| **Frontend Spring Boot** | http://localhost:8080 | Página de login visible |
| **Keycloak Admin** | http://localhost:8090 | Página de administración |
| **H2 Database Console** | http://localhost:8080/h2-console | Consola de BD |
| **SQL Server** | localhost:1433 | Conexión disponible (via SQL Client) |

### Test de Conectividad

```bash
# Verificar que el puerto 8080 está escuchando
netstat -ano | findstr :8080     # Windows
netstat -tuln | grep :8080       # Linux/Mac

# Verificar que el puerto 8090 está escuchando (Keycloak)
netstat -ano | findstr :8090     # Windows
netstat -tuln | grep :8090       # Linux/Mac
```

---

## 🌐 Acceder a la Aplicación

### 1. Acceder al Frontend

Abre tu navegador y ve a:

```
http://localhost:8080
```

Deberías ver la **página de login** con dos opciones:
- 🔵 **Iniciar sesión con Google**
- 🔑 **Iniciar sesión con Keycloak**

### 2. Configurar Keycloak (Primera Vez)

Antes de poder usar Keycloak, necesitas configurarlo:

1. Ve a http://localhost:8090
2. Click en **"Administration Console"**
3. Login con las credenciales por defecto:
   - **Usuario:** `admin`
   - **Contraseña:** `admin123`

📚 **Sigue la guía completa:** [CONFIGURACION-KEYCLOAK.md](./docs/CONFIGURACION-KEYCLOAK.md)

Pasos resumidos:
- Crear Realm `tournament`
- Crear Client `tournament-system`
- Crear usuario de prueba
- Configurar redirect URIs

### 3. Probar Google OAuth (Ya Configurado)

1. En la página de login, click en **"Iniciar sesión con Google"**
2. Selecciona tu cuenta de Google
3. Deberías ser redirigido a la página de inicio (`/home`)
4. Verás tu email en la barra de navegación

### 4. Probar Keycloak OAuth (Después de Configurar)

1. Configura Keycloak siguiendo [CONFIGURACION-KEYCLOAK.md](./docs/CONFIGURACION-KEYCLOAK.md)
2. En la página de login, click en **"Iniciar sesión con Keycloak"**
3. Login con el usuario de prueba que creaste
4. Deberías ser redirigido a `/home`

---

## 🔍 Consola H2 Database

Para ver la base de datos de desarrollo:

1. Ve a http://localhost:8080/h2-console
2. Configura la conexión:
   - **JDBC URL:** `jdbc:h2:mem:tournamentdb`
   - **User Name:** `sa`
   - **Password:** *(dejar vacío)*
3. Click **"Connect"**

Tablas disponibles:
- `USUARIOS` - Usuarios registrados
- (Otras tablas según tu modelo de datos)

---

## 🛑 Detener los Servicios

### Detener Spring Boot

En la terminal donde corre Spring Boot, presiona:

```
Ctrl + C
```

### Detener Docker Services

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (resetear bases de datos)
docker-compose down -v
```

---

## 🐛 Solución de Problemas

### ❌ Error: "Port 8080 is already in use"

**Causa:** Ya hay un proceso usando el puerto 8080.

**Solución:**

#### Windows:
```bash
# Encontrar el proceso
netstat -ano | findstr :8080

# Matar el proceso (reemplaza PID con el número que encontraste)
taskkill /F /PID <PID>
```

#### Linux/Mac:
```bash
# Encontrar el proceso
lsof -i :8080

# Matar el proceso
kill -9 <PID>
```

### ❌ Error: "Cannot connect to Keycloak"

**Causa:** Keycloak aún no ha terminado de iniciar.

**Solución:**
```bash
# Ver los logs de Keycloak
docker logs tournament-keycloak -f

# Espera a ver: "Keycloak 23.0 started"
```

⏱️ Espera 1-2 minutos después de `docker-compose up`.

### ❌ Error: "Docker daemon is not running"

**Causa:** Docker Desktop no está iniciado.

**Solución:**
1. Abre Docker Desktop
2. Espera a que el ícono de Docker esté verde
3. Ejecuta `docker ps` para verificar

### ❌ Error: Maven descarga muy lento

**Causa:** Conexión lenta o problemas con el repositorio Maven Central.

**Solución:**
```bash
# Limpiar caché de Maven
mvnw clean

# Actualizar dependencias
mvnw dependency:purge-local-repository
```

### ❌ Error: "Invalid redirect_uri" en OAuth

**Causa:** La URL de callback no está configurada correctamente en Google/Keycloak.

**Solución:**

**Para Google:**
- Ve a [Google Cloud Console](https://console.cloud.google.com)
- APIs & Services → Credentials
- Agrega `http://localhost:8080/login/oauth2/code/google` a Authorized redirect URIs

**Para Keycloak:**
- Ve a http://localhost:8090/admin
- Realm `tournament` → Clients → `tournament-system`
- Verifica que Valid Redirect URIs contenga: `http://localhost:8080/login/oauth2/code/keycloak`

### ❌ Error: "java.lang.OutOfMemoryError"

**Causa:** Maven necesita más memoria heap.

**Solución:**

#### Windows:
```bash
set MAVEN_OPTS=-Xmx1024m
mvnw spring-boot:run
```

#### Linux/Mac:
```bash
export MAVEN_OPTS="-Xmx1024m"
./mvnw spring-boot:run
```

### ❌ Error: "Access denied for user 'sa'"

**Causa:** Credenciales incorrectas de SQL Server.

**Solución:**
1. Verifica el archivo `.env` o las variables de entorno
2. La contraseña por defecto es: `YourStrong@Passw0rd`
3. Reinicia los contenedores:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

---

## 📚 Recursos Adicionales

### Documentación del Proyecto

- 📘 [Configuración de Keycloak](./docs/CONFIGURACION-KEYCLOAK.md)
- 📘 [Sprint Review](../PROYECTO-SEGURIDAD-SOFTWARE/docs/sprints/SPRINT-1-REVIEW.md)
- 📘 [Planificación de Sprints](../PROYECTO-SEGURIDAD-SOFTWARE/docs/sprints/PLANIFICACION-SPRINTS.md)
- 📘 [README Principal](./README.md)

### Tecnologías Utilizadas

- [Spring Boot 3.4.4](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2)
- [Keycloak 23.0](https://www.keycloak.org/documentation)
- [Docker Compose](https://docs.docker.com/compose/)
- [H2 Database](https://www.h2database.com/html/main.html)

### Comunidad y Soporte

- 💬 Stack Overflow: Tags `spring-boot`, `keycloak`, `oauth2`
- 📖 Spring Security Docs: https://docs.spring.io/spring-security/reference/
- 🔑 Keycloak Docs: https://www.keycloak.org/docs/latest/

---

## 🎯 Próximos Pasos

Una vez que hayas verificado que todo funciona:

1. ✅ **Configura Keycloak** siguiendo [CONFIGURACION-KEYCLOAK.md](./docs/CONFIGURACION-KEYCLOAK.md)
2. ✅ **Explora la aplicación** navegando por las diferentes secciones
3. ✅ **Revisa el código** en `frontend/IngWeb/src/main/java`
4. ✅ **Lee la documentación** del proyecto en la carpeta `docs/`
5. ✅ **Comienza a desarrollar** según la planificación de sprints

---

## 📞 Contacto y Ayuda

Si encuentras problemas no listados aquí:

1. 🔍 Revisa los **logs** de la aplicación
2. 📖 Consulta la documentación específica en `/docs`
3. 🐛 Busca el error en **Stack Overflow**
4. 💬 Pregunta al equipo de desarrollo

---

**¡Feliz desarrollo! 🚀**

---

> **Última actualización:** 2025-11-27
> **Versión de la guía:** 2.0
> **Autor:** Tournament Security Team
