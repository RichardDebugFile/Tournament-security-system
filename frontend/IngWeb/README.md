# 🏆 Sistema de Gestión de Torneos y Usuarios - Aplicación Web con Spring Boot

Este proyecto es una aplicación web desarrollada en Java con Spring Boot. Permite la gestión de torneos deportivos y de usuarios autenticados, implementando funcionalidades CRUD y un sistema de login seguro con Spring Security y BCrypt. La aplicación sigue el patrón de diseño MVC, utiliza Thymeleaf para renderizar vistas HTML y Bootstrap 5 para lograr una interfaz moderna y responsiva.

---

## 🚀 Motivación

Este proyecto fue desarrollado como parte del aprendizaje en desarrollo web con Spring Boot, aplicando buenas prácticas de arquitectura, seguridad y experiencia de usuario. También se desplegó en la plataforma Render como parte de la práctica de deploy continuo.

🎥 **Video explicativo del proyecto**:  
https://youtu.be/l_gLvBou-tg

🌐 **Deploy del proyecto (Render)**:  
https://ingweb-5.onrender.com

📦 **Repositorio GitHub**:  
https://github.com/DiegoV22/IngWeb.git

---

## 📖 Tabla de Contenidos

- Características
- Tecnologías
- Instalación y Ejecución
- Uso del Proyecto
- Reporte de Torneos
- Estructura del Proyecto
- Autor
- Futuras Mejoras

---

## ✅ Características

### 🎯 Torneos
- ✅ Listar torneos registrados  
- ✅ Crear nuevos torneos  
- ✅ Editar torneos existentes  
- ✅ Eliminar torneos  
- ✅ Estado del torneo: *En progreso* / *Finalizado*  
- ✅ Reporte de torneos con cálculo de días de retraso

### 👤 Usuarios
- ✅ Registrar nuevos usuarios con contraseña segura (mín. 8 caracteres, mayúscula, minúscula, número y símbolo)  
- ✅ Validación personalizada de contraseñas  
- ✅ Encriptación de contraseñas con BCrypt  
- ✅ Listar, editar y eliminar usuarios registrados  

### 🔐 Autenticación
- ✅ Sistema de login con Spring Security  
- ✅ Protege rutas privadas como `/usuarios` o `/torneos`  
- ✅ Página personalizada de login con validación de credenciales  

---

## 🧮 Reporte de Torneos (NUEVO)

Se agregó una sección adicional accesible en `/reportes`, que genera una tabla de todos los torneos registrados con un cálculo automático de los días de retraso **si el torneo está en progreso pero ya pasó la fecha final**.

### 📋 Campos que se muestran:
- Nombre
- Categoría
- Estado
- Fecha fin
- Días de retraso (calculado dinámicamente)

---

## 🧰 Tecnologías

- Java 17  
- Spring Boot 3.4.4  
- Spring Data JPA  
- Spring Security  
- Thymeleaf  
- Bootstrap 5  
- H2 Database (soporte preparado para SQL Server)  
- BCrypt  
- Render (para el despliegue gratuito)  

---

## 🛠️ Instalación y Ejecución

Clona el repositorio:

```bash
git clone https://github.com/DiegoV22/IngWeb.git
Requisitos:

Java 17

Maven

Ejecución:

bash
Copiar
Editar
./mvnw spring-boot:run
Accede desde el navegador:

bash
Copiar
Editar
http://localhost:8080/login
📌 Uso del Proyecto
Módulo de Torneos
Cada torneo incluye:

Nombre, categoría, ubicación, costo, fechas de inicio y fin, estado

Vistas:

lista.html: muestra todos los torneos

formulario.html: para registrar o editar

Módulo de Usuarios
Cada usuario incluye:

Nombre, apellido, email, contraseña encriptada, fecha de registro

Vistas:

lista.html: muestra todos los usuarios registrados

formulario.html: para registrar o editar usuarios

Login
Ruta protegida /usuarios y /torneos

Página de login en /login

🗂 Estructura del Proyecto
css
Copiar
Editar
src/main/java/
└── com.diego.curso.springboot.webapp.springboot_web
    ├── controllers/
    │   ├── TorneoController.java
    │   ├── UsuarioController.java
    │   ├── LoginController.java
    │   └── ReporteController.java ✅
    ├── models/
    │   ├── Torneo.java
    │   └── Usuario.java
    ├── dto/
    │   └── ReporteDTO.java ✅
    ├── repositories/
    │   ├── TorneoRepository.java
    │   └── UsuarioRepository.java
    ├── services/
    │   ├── TorneoService.java / Impl
    │   ├── UsuarioService.java / Impl
    │   └── UsuarioDetailsService.java (Spring Security)
    └── config/
        └── SecurityConfig.java

src/main/resources/
├── templates/
│   ├── torneos/
│   │   ├── lista.html
│   │   └── formulario.html
│   ├── usuarios/
│   │   ├── lista.html
│   │   ├── formulario.html
│   │   └── login.html
│   └── reportes/ ✅
│       └── reporte-torneos.html ✅
└── application.properties
👨‍💻 Autor
Diego V.
Estudiante de Ingeniería de Software
GitHub: @DiegoV22
