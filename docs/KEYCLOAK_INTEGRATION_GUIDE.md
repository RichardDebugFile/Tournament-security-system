# Guía de Integración de Keycloak con Spring Boot OAuth2

## Problema Resuelto

Se ha solucionado el error `UnsatisfiedDependencyException` que ocurría al intentar integrar Keycloak 23.0 con Spring Boot 3.4.4 usando `issuer-uri`. El problema se debía a que Spring Boot intentaba contactar a Keycloak durante el arranque para obtener la configuración OIDC automáticamente, y si Keycloak no estaba disponible, la aplicación fallaba al iniciar.

## Solución Implementada

### 1. Configuración Manual de Endpoints (Recomendado)

En lugar de usar `issuer-uri` que requiere que Keycloak esté disponible durante el arranque, se configuraron manualmente todos los endpoints OAuth2/OIDC en `application.properties`:

```properties
# Keycloak Client Registration
spring.security.oauth2.client.registration.keycloak.client-id=tournament-system
spring.security.oauth2.client.registration.keycloak.client-secret=EISy4zgMyGRjRqJXqrPe4NMHwJN7cdZ8
spring.security.oauth2.client.registration.keycloak.scope=openid,profile,email
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.redirect-uri={baseUrl}/login/oauth2/code/keycloak
spring.security.oauth2.client.registration.keycloak.client-name=Keycloak

# Keycloak Provider - Configuración manual de endpoints
spring.security.oauth2.client.provider.keycloak.authorization-uri=http://localhost:8090/realms/tournament/protocol/openid-connect/auth
spring.security.oauth2.client.provider.keycloak.token-uri=http://localhost:8090/realms/tournament/protocol/openid-connect/token
spring.security.oauth2.client.provider.keycloak.user-info-uri=http://localhost:8090/realms/tournament/protocol/openid-connect/userinfo
spring.security.oauth2.client.provider.keycloak.jwk-set-uri=http://localhost:8090/realms/tournament/protocol/openid-connect/certs
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
```

**Ventajas:**
- La aplicación arranca correctamente aunque Keycloak no esté disponible
- No hay dependencia en tiempo de arranque
- Mayor control sobre la configuración

**Desventajas:**
- Debes especificar manualmente todos los endpoints
- Si cambias el realm o la URL de Keycloak, debes actualizar 5 propiedades

### 2. Configuración con issuer-uri (Alternativa)

Si prefieres usar la configuración automática y tienes garantía de que Keycloak siempre estará disponible:

```properties
# Comentar las 5 líneas de configuración manual y descomentar esta:
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8090/realms/tournament
```

**Ventajas:**
- Una sola línea de configuración
- Actualización automática si Keycloak cambia su configuración

**Desventajas:**
- Keycloak DEBE estar disponible cuando la aplicación arranca
- Falla con `UnsatisfiedDependencyException` si Keycloak está caído

## Archivos Modificados y Creados

### Modificados

1. **CustomOidcUserService.java**
   - Agregado soporte para mapear `preferred_username` de Keycloak
   - Lógica de fallback: preferred_username → email → sub
   - Logging detallado para usuarios de Keycloak

2. **SecurityConfig.java**
   - Integrados los handlers de auditoría
   - `OAuth2AuthenticationSuccessHandler` para eventos exitosos
   - `OAuth2AuthenticationFailureHandler` para eventos fallidos

3. **application.properties**
   - Configuración completa de Keycloak con endpoints manuales
   - Documentación de alternativas

### Creados

#### Entidades y Repositorios

1. **OAuth2AuthenticationLog.java** (`models/`)
   - Entidad JPA para almacenar logs de autenticación
   - Campos: provider, username, email, success, error, IP, user-agent, timestamp, etc.

2. **OAuth2AuthenticationLogRepository.java** (`repositories/`)
   - Queries personalizadas para estadísticas
   - Búsqueda por proveedor, IP, usuario, rango de fechas
   - Contadores y métricas

#### Servicios

3. **OAuth2AuthenticationAuditService.java** (`services/`)
   - Servicio asíncrono para registrar autenticaciones
   - Detección de actividad sospechosa
   - Generación de estadísticas y métricas
   - Métodos para limpieza de logs antiguos

#### Handlers de Seguridad

4. **OAuth2AuthenticationSuccessHandler.java** (`config/`)
   - Captura autenticaciones exitosas
   - Registra en BD de forma asíncrona
   - Detecta actividad sospechosa por IP

5. **OAuth2AuthenticationFailureHandler.java** (`config/`)
   - Captura fallos de autenticación
   - Extrae información de errores OAuth2
   - Alerta de múltiples intentos fallidos

#### Tests

6. **CustomOidcUserServiceTest.java** (`test/config/`)
   - Tests unitarios para mapeo de usuarios
   - Casos de prueba para Keycloak, Google y Azure
   - Verificación de fallbacks

7. **OAuth2IntegrationTest.java** (`test/`)
   - Tests de integración con MockMvc
   - Simulación de login con diferentes proveedores
   - Tests del repositorio de logs

8. **OAuth2AuthenticationAuditServiceTest.java** (`test/services/`)
   - Tests del servicio de auditoría
   - Verificación de logging asíncrono
   - Tests de detección de actividad sospechosa

9. **application-test.properties** (`test/resources/`)
   - Configuración para tests
   - Base de datos H2 en memoria

## Configuración de Keycloak

### Requisitos en Keycloak

1. **Realm:** `tournament`
2. **Client ID:** `tournament-system`
3. **Client Secret:** `EISy4zgMyGRjRqJXqrPe4NMHwJN7cdZ8`
4. **Client Type:** Confidential
5. **Valid Redirect URIs:** `http://localhost:8080/login/oauth2/code/keycloak`
6. **Access Type:** confidential
7. **Standard Flow Enabled:** ON
8. **Direct Access Grants Enabled:** OFF (recomendado)

### Scopes Requeridos

- `openid` (obligatorio para OIDC)
- `profile`
- `email`

### Mappers de Usuario

Keycloak por defecto incluye estos claims en el token:
- `sub`: ID único del usuario
- `preferred_username`: Nombre de usuario
- `email`: Email del usuario
- `email_verified`: Si el email está verificado

## Auditoría Implementada

### Eventos Capturados

1. **Autenticaciones Exitosas**
   - Proveedor (Google, Keycloak, Azure, etc.)
   - Username e email del usuario
   - IP y User-Agent
   - Session ID
   - Atributos adicionales específicos del proveedor

2. **Autenticaciones Fallidas**
   - Proveedor (si se puede determinar)
   - Mensaje de error
   - IP y User-Agent
   - Timestamp

### Métricas Disponibles

El servicio `OAuth2AuthenticationAuditService` proporciona:

```java
// Estadísticas por proveedor
Map<String, Map<String, Long>> stats = auditService.getAuthenticationStatistics();
// Resultado: {"keycloak": {"successful": 150, "failed": 5}, "google": {...}}

// Autenticaciones en las últimas 24 horas
Map<String, Long> last24h = auditService.getAuthenticationsByProviderLast24Hours();

// Usuarios únicos por proveedor
Map<String, Long> uniqueUsers = auditService.getUniqueUsersByProvider();

// Últimas autenticaciones
List<OAuth2AuthenticationLog> recent = auditService.getRecentAuthentications();

// Verificar actividad sospechosa
boolean suspicious = auditService.hasSuspiciousActivity(ipAddress, 5, 15);
```

### Detección de Actividad Sospechosa

- Se monitorean intentos fallidos por IP
- Configurable: X intentos fallidos en Y minutos
- Default: 5 intentos en 15 minutos
- Logs de alerta en caso de actividad sospechosa

## Instrucciones de Uso

### 1. Iniciar Keycloak

```bash
cd /ruta/a/keycloak
bin/kc.sh start-dev --http-port=8090
```

### 2. Configurar el Client en Keycloak

1. Acceder a: http://localhost:8090
2. Login como admin
3. Crear realm "tournament" (si no existe)
4. Crear client "tournament-system"
5. Configurar según los requisitos arriba
6. Copiar el client secret generado

### 3. Actualizar application.properties

Si el client secret cambió, actualizar en `application.properties`:

```properties
spring.security.oauth2.client.registration.keycloak.client-secret=TU_NUEVO_SECRET
```

### 4. Iniciar la Aplicación Spring Boot

```bash
cd tournament-security-system/frontend/IngWeb
mvn spring-boot:run
```

La aplicación arrancará en: http://localhost:8080

### 5. Probar la Integración

1. Acceder a: http://localhost:8080
2. Click en "Login"
3. Seleccionar "Keycloak" como proveedor
4. Serás redirigido a Keycloak
5. Login con credenciales de Keycloak
6. Serás redirigido de vuelta a la aplicación autenticado

### 6. Verificar Logs de Auditoría

Los logs se guardan automáticamente en la tabla `oauth2_authentication_logs` de H2.

Acceder a H2 Console:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:tournamentdb`
- Username: `sa`
- Password: (vacío)

Query de ejemplo:
```sql
SELECT * FROM oauth2_authentication_logs
WHERE provider = 'keycloak'
ORDER BY timestamp DESC;
```

## Ejecutar Tests

### Tests Unitarios

```bash
mvn test -Dtest=CustomOidcUserServiceTest
mvn test -Dtest=OAuth2AuthenticationAuditServiceTest
```

### Tests de Integración

```bash
mvn test -Dtest=OAuth2IntegrationTest
```

### Todos los Tests

```bash
mvn test
```

## Troubleshooting

### Problema: UnsatisfiedDependencyException al arranque

**Solución:** Usa la configuración manual de endpoints en lugar de `issuer-uri`

### Problema: Invalid redirect_uri

**Solución:** Verifica que en Keycloak, el client tenga configurado exactamente:
```
http://localhost:8080/login/oauth2/code/keycloak
```

### Problema: Unauthorized client

**Solución:**
1. Verifica que el client-id y client-secret sean correctos
2. Asegúrate de que el client type sea "confidential"
3. Verifica que "Standard Flow Enabled" esté ON

### Problema: Usuario no tiene preferred_username

**Solución:** El código ya maneja este caso con fallback a email y luego a sub.

## Seguridad y Mejores Prácticas

1. **Secrets en Variables de Entorno**
   ```properties
   spring.security.oauth2.client.registration.keycloak.client-secret=${KEYCLOAK_CLIENT_SECRET}
   ```

2. **HTTPS en Producción**
   - Cambiar todas las URLs de `http://` a `https://`
   - Configurar certificados SSL

3. **Limpieza de Logs**
   ```java
   // Ejecutar periódicamente (ej: cada noche)
   auditService.cleanupOldLogs(90); // Mantener solo 90 días
   ```

4. **Rate Limiting**
   - Implementar rate limiting adicional en el gateway/proxy
   - Bloquear IPs con actividad sospechosa

5. **Monitoreo**
   - Configurar alertas para intentos fallidos masivos
   - Monitorear métricas de autenticación

## Referencias

- [Spring Security OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Boot 3 + Keycloak Integration](https://developers.redhat.com/articles/2023/07/24/how-integrate-spring-boot-3-spring-security-and-keycloak)
- [OAuth 2.0 Authorization Code Flow](https://oauth.net/2/grant-types/authorization-code/)

## Contacto y Soporte

Para problemas o preguntas, consultar la documentación oficial o abrir un issue en el repositorio del proyecto.
