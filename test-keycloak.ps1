# Script de diagnóstico para Keycloak OAuth2
# Este script verifica la configuración de Keycloak paso a paso

Write-Host "========== KEYCLOAK OAUTH2 DIAGNOSTIC TOOL ==========" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar que Keycloak esté corriendo
Write-Host "[1/5] Verificando que Keycloak esté corriendo..." -ForegroundColor Yellow
try {
    $keycloakHealth = Invoke-WebRequest -Uri "http://localhost:8090/health/ready" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Keycloak está corriendo (Status: $($keycloakHealth.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ ERROR: Keycloak no está corriendo o no responde" -ForegroundColor Red
    Write-Host "  Asegúrate de que el contenedor tournament-keycloak esté activo" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. Verificar la configuración OpenID Connect
Write-Host "[2/5] Verificando configuración OpenID Connect del realm 'tournament'..." -ForegroundColor Yellow
try {
    $oidcConfig = Invoke-RestMethod -Uri "http://localhost:8090/realms/tournament/.well-known/openid-configuration" -Method GET -ErrorAction Stop
    Write-Host "✓ Configuración OIDC obtenida correctamente" -ForegroundColor Green
    Write-Host "  - Authorization Endpoint: $($oidcConfig.authorization_endpoint)" -ForegroundColor Gray
    Write-Host "  - Token Endpoint: $($oidcConfig.token_endpoint)" -ForegroundColor Gray
    Write-Host "  - UserInfo Endpoint: $($oidcConfig.userinfo_endpoint)" -ForegroundColor Gray
    Write-Host "  - JWKS URI: $($oidcConfig.jwks_uri)" -ForegroundColor Gray
} catch {
    Write-Host "✗ ERROR: No se pudo obtener la configuración OIDC del realm 'tournament'" -ForegroundColor Red
    Write-Host "  Verifica que el realm 'tournament' exista en Keycloak" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 3. Verificar configuración de Spring Boot
Write-Host "[3/5] Verificando configuración en application.properties..." -ForegroundColor Yellow
$appPropsPath = "G:\Documentos G\Ing. Sotware\Periodo 3\Desarrollo de Software Seguro\Proyecto\Referencias\tournament-security-system\frontend\IngWeb\src\main\resources\application.properties"
$appProps = Get-Content $appPropsPath -Raw

# Extraer client-id
if ($appProps -match 'spring\.security\.oauth2\.client\.registration\.keycloak\.client-id=(.+)') {
    $clientId = $matches[1].Trim()
    Write-Host "✓ Client ID encontrado: $clientId" -ForegroundColor Green
} else {
    Write-Host "✗ ERROR: No se encontró client-id en application.properties" -ForegroundColor Red
    exit 1
}

# Extraer client-secret
if ($appProps -match 'spring\.security\.oauth2\.client\.registration\.keycloak\.client-secret=(.+)') {
    $clientSecret = $matches[1].Trim()
    Write-Host "✓ Client Secret encontrado: ${clientSecret.Substring(0, 10)}..." -ForegroundColor Green
} else {
    Write-Host "✗ ERROR: No se encontró client-secret en application.properties" -ForegroundColor Red
    exit 1
}

# Verificar authentication method
if ($appProps -match 'spring\.security\.oauth2\.client\.registration\.keycloak\.client-authentication-method=(.+)') {
    $authMethod = $matches[1].Trim()
    Write-Host "✓ Authentication Method: $authMethod" -ForegroundColor Green
} else {
    Write-Host "⚠ WARNING: No se especificó client-authentication-method (usará default)" -ForegroundColor Yellow
    $authMethod = "client_secret_basic"
}

Write-Host ""

# 4. Verificar que el cliente existe en Keycloak
Write-Host "[4/5] Verificando cliente '$clientId' en Keycloak..." -ForegroundColor Yellow
Write-Host "  Nota: Esta verificación requiere autenticación de admin" -ForegroundColor Gray
Write-Host "  Verifica manualmente en: http://localhost:8090/admin/master/console/#/tournament/clients" -ForegroundColor Gray

Write-Host ""

# 5. Simular flujo OAuth2 (hasta donde sea posible sin browser)
Write-Host "[5/5] Información del flujo OAuth2:" -ForegroundColor Yellow
Write-Host "  URL de autorización que debes visitar:" -ForegroundColor Cyan
$authUrl = "http://localhost:8090/realms/tournament/protocol/openid-connect/auth?response_type=code&client_id=$clientId&scope=openid%20profile%20email&redirect_uri=http://localhost:8080/login/oauth2/code/keycloak&state=test123"
Write-Host "  $authUrl" -ForegroundColor White
Write-Host ""
Write-Host "  Después de autenticarte, Keycloak te redirigirá a:" -ForegroundColor Cyan
Write-Host "  http://localhost:8080/login/oauth2/code/keycloak?code=CODIGO&state=test123" -ForegroundColor White
Write-Host ""
Write-Host "  Spring Boot intercambiará el código por un token usando:" -ForegroundColor Cyan
Write-Host "  POST $($oidcConfig.token_endpoint)" -ForegroundColor White
Write-Host "  Body: grant_type=authorization_code&code=CODIGO&redirect_uri=..." -ForegroundColor White
Write-Host "  Auth: client_id=$clientId, client_secret=***" -ForegroundColor White

Write-Host ""
Write-Host "========== DIAGNÓSTICO COMPLETO ==========" -ForegroundColor Cyan
Write-Host ""
Write-Host "SIGUIENTE PASO:" -ForegroundColor Yellow
Write-Host "1. Abre http://localhost:8080/login en tu navegador" -ForegroundColor White
Write-Host "2. Haz clic en 'Iniciar sesión con Keycloak'" -ForegroundColor White
Write-Host "3. Ingresa las credenciales en Keycloak" -ForegroundColor White
Write-Host "4. Observa los logs de Spring Boot para ver los detalles del intercambio de token" -ForegroundColor White
Write-Host ""
Write-Host "Si ves un error 501, verifica:" -ForegroundColor Yellow
Write-Host "- Que el client secret en Keycloak coincida con el de application.properties" -ForegroundColor White
Write-Host "- Que 'Client authentication' esté ON en la configuración del cliente en Keycloak" -ForegroundColor White
Write-Host "- Que el método de autenticación (client_secret_post) sea compatible con Keycloak" -ForegroundColor White
Write-Host "- Los logs de Keycloak: docker logs tournament-keycloak --tail 50" -ForegroundColor White
Write-Host ""
