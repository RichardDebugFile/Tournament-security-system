# Configuración de Keycloak para Tournament System

## 🔑 Credenciales de Acceso

- **URL:** http://localhost:8090
- **Usuario Admin:** `admin`
- **Contraseña:** `admin123`

## 📋 Pasos de Configuración

### 1. Acceder a Keycloak Admin Console

1. Espera 1-2 minutos a que Keycloak termine de iniciar
2. Ve a http://localhost:8090
3. Click en **"Administration Console"**
4. Login con `admin` / `admin123`

### 2. Crear Realm "tournament"

1. En el dropdown superior izquierdo (dice "Master"), haz click
2. Click en **"Create Realm"**
3. Configura:
   - **Realm name:** `tournament`
   - **Enabled:** ON
4. Click **"Create"**

### 3. Configurar Client "tournament-system"

1. En el menú izquierdo, ve a **Clients**
2. Click **"Create client"**
3. En la pestaña **General Settings**:
   - **Client type:** OpenID Connect
   - **Client ID:** `tournament-system`
4. Click **"Next"**
5. En **Capability config**:
   - **Client authentication:** ON
   - **Authorization:** OFF
   - **Authentication flow:**
     - ✅ Standard flow
     - ✅ Direct access grants
6. Click **"Next"**
7. En **Login settings**:
   - **Root URL:** `http://localhost:8080`
   - **Home URL:** `http://localhost:8080/home`
   - **Valid redirect URIs:** `http://localhost:8080/login/oauth2/code/keycloak`
   - **Valid post logout redirect URIs:** `http://localhost:8080/login?logout=true`
   - **Web origins:** `http://localhost:8080`
8. Click **"Save"**

### 4. Obtener Client Secret

1. En el client `tournament-system`, ve a la pestaña **"Credentials"**
2. Copia el **Client Secret** (algo como `tournament-secret-key-2024`)
3. **IMPORTANTE:** Este secret ya está configurado en `application.properties`
   - Si es diferente, actualiza el archivo

### 5. Crear Usuario de Prueba

1. En el menú izquierdo, ve a **Users**
2. Click **"Add user"**
3. Configura:
   - **Username:** `testuser`
   - **Email:** `test@tournament.com`
   - **Email verified:** ON
   - **First name:** Test
   - **Last name:** User
4. Click **"Create"**
5. Ve a la pestaña **"Credentials"**
6. Click **"Set password"**
7. Configura:
   - **Password:** `Test123!`
   - **Temporary:** OFF
8. Click **"Save"**

### 6. Configurar Atributos de Usuario (Opcional)

1. En el client `tournament-system`, ve a **"Client scopes"**
2. Click en `tournament-system-dedicated`
3. Click **"Add mapper"** → **"By configuration"**
4. Selecciona **"User Property"**
5. Configura:
   - **Name:** email
   - **Property:** email
   - **Token Claim Name:** email
   - **Claim JSON Type:** String
   - **Add to ID token:** ON
   - **Add to access token:** ON
   - **Add to userinfo:** ON
6. Click **"Save"**

## ✅ Verificación

Una vez configurado:

1. Reinicia Spring Boot si está corriendo
2. Ve a http://localhost:8080
3. Click en **"Iniciar sesión con Keycloak"**
4. Deberías ser redirigido a Keycloak
5. Login con `testuser` / `Test123!`
6. Deberías ser redirigido a `/home` autenticado

## 🔒 Configuración Segura del Logout

El logout ya está configurado en `SecurityConfig.java` para:
- Invalidar la sesión HTTP
- Limpiar la autenticación
- Eliminar cookies de sesión
- Evitar errores de token al cerrar sesión

## 🐛 Troubleshooting

### Error: "Client not found"
- Verifica que el realm sea `tournament` (no `master`)
- Verifica que el Client ID sea exactamente `tournament-system`

### Error: "Invalid redirect_uri"
- Verifica que la URL de redirect en Keycloak sea exactamente:
  `http://localhost:8080/login/oauth2/code/keycloak`

### Error: "Unauthorized"
- Verifica que el Client Secret en `application.properties` coincida con el de Keycloak

### Keycloak no inicia
- Espera 1-2 minutos (tarda en arrancar)
- Verifica los logs: `docker logs tournament-keycloak`
- Verifica que el puerto 8090 no esté ocupado

## 📊 Arquitectura

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Browser   │────────▶│ Spring Boot  │◀───────▶│  Keycloak   │
│             │         │  Frontend    │         │  (OAuth2)   │
└─────────────┘         └──────────────┘         └─────────────┘
                              │                         │
                              │                         │
                              ▼                         ▼
                        ┌──────────┐            ┌──────────┐
                        │ H2 DB    │            │PostgreSQL│
                        └──────────┘            └──────────┘
```

## 🔐 Gestión de Correos con Keycloak

Keycloak incluye capacidades de email para:
- Verificación de email
- Reset de contraseña
- Notificaciones

Para configurar email (opcional):
1. Ve a **Realm Settings** → **Email**
2. Configura tu servidor SMTP
3. Habilita verificación de email en el registro

## 📚 Recursos

- Documentación Keycloak: https://www.keycloak.org/docs/latest/
- Spring Security OAuth2: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/
