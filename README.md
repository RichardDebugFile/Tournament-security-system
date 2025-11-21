# 🏆 Tournament Security System

Sistema de gestión de torneos deportivos con características avanzadas de seguridad.

## 📋 Descripción

Este proyecto implementa un sistema completo de gestión de torneos con las siguientes características de seguridad:

- **Single Sign-On (SSO)**: Autenticación centralizada con Azure AD B2C y Google OAuth
- **Encriptación End-to-End**: Comunicaciones seguras entre microservicios usando Azure Key Vault
- **Git-lock**: Prevención de exposición de secretos en el repositorio

## 🏗️ Estructura del Proyecto

```
tournament-security-system/
│
├── backend/                              # Backend .NET 8
│   ├── existing-services/                # Servicios existentes
│   │   ├── backend-model/               # Modelos compartidos
│   │   ├── backend-tournament/          # API de torneos
│   │   ├── backend-authenticator/       # API de autenticación
│   │   └── Notifications/               # Servicio de notificaciones
│   │
│   ├── new-services/                    # Nuevos servicios (a crear)
│   │   ├── backend-identity-server/     # SSO Server (Sprint 1)
│   │   └── backend-encryption-proxy/    # Encryption Proxy (Sprint 2)
│   │
│   └── tournament-security.sln          # Solución .NET
│
├── frontend/                            # Frontend Spring Boot
│   └── IngWeb/                          # Aplicación web
│
├── docker/                              # Configuración Docker (a crear)
├── scripts/                             # Scripts de utilidad (a crear)
└── .github/                             # GitHub Actions (a crear)
```

## 🚀 Inicio Rápido

### Pre-requisitos

- .NET 8 SDK
- Java 17 + Maven
- SQL Server
- Docker Desktop (opcional)
- Azure CLI
- Cuenta de Azure (estudiante)
- Cuenta de Google Cloud Console

### Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd tournament-security-system
   ```

2. **Configurar Backend**
   ```bash
   cd backend
   dotnet restore
   dotnet build
   ```

3. **Configurar Frontend**
   ```bash
   cd frontend/IngWeb
   ./mvnw clean install
   ```

4. **Configurar Base de Datos**
   - Ver instrucciones en `/backend/existing-services/backend-tournament/README.md`

### Ejecución Local

**Backend (cada microservicio)**:
```bash
# Terminal 1 - Tournament API
cd backend/existing-services/backend-tournament
dotnet run

# Terminal 2 - Authenticator API
cd backend/existing-services/backend-authenticator
dotnet run

# Terminal 3 - Notifications
cd backend/existing-services/Notifications
dotnet run
```

**Frontend**:
```bash
cd frontend/IngWeb
./mvnw spring-boot:run
```

Accede a: http://localhost:8080

## 📚 Documentación Completa

La documentación completa del proyecto está en la carpeta **PROYECTO-SEGURIDAD-SOFTWARE/**:

- [Inicio Rápido](../PROYECTO-SEGURIDAD-SOFTWARE/INICIO-RAPIDO.md)
- [Planificación de Sprints](../PROYECTO-SEGURIDAD-SOFTWARE/docs/sprints/PLANIFICACION-SPRINTS.md)
- [Arquitectura del Sistema](../PROYECTO-SEGURIDAD-SOFTWARE/docs/arquitectura/ARQUITECTURA.md)
- [Guía de Setup SSO](../PROYECTO-SEGURIDAD-SOFTWARE/docs/guias-tecnicas/01-SETUP-SSO.md)
- [Checklist Completo](../PROYECTO-SEGURIDAD-SOFTWARE/CHECKLIST-COMPLETO.md)

## 🎯 Sprints del Proyecto

### Sprint 1 (Semanas 1-2): Single Sign-On
- ✅ Configurar Azure AD B2C
- ✅ Configurar Google OAuth
- ✅ Crear Identity Server
- ✅ Integrar con frontend

### Sprint 2 (Semanas 3-4): Encriptación con KMS
- ⬜ Configurar Azure Key Vault
- ⬜ Crear Encryption Proxy
- ⬜ Integrar con microservicios
- ⬜ Testing de seguridad

### Sprint 3 (Semana 5): Git-lock
- ⬜ Configurar Talisman
- ⬜ Configurar GitGuardian
- ⬜ Crear custom hooks
- ⬜ Azure DevOps pipeline

### Sprint 4 (Semana 6): Testing & Deployment
- ⬜ Tests E2E
- ⬜ Deployment a Azure
- ⬜ Documentación final

## 🛠️ Stack Tecnológico

### Backend
- .NET 8
- Entity Framework Core
- SQL Server
- Azure AD / Google OAuth
- Azure Key Vault

### Frontend
- Spring Boot 3.4.4
- Thymeleaf
- Bootstrap 5
- OAuth2 Client

### DevOps
- Docker
- Azure DevOps
- GitHub Actions
- Talisman
- GitGuardian

## 🔒 Seguridad

Este proyecto implementa:

- **Autenticación**: OAuth 2.0 / OpenID Connect
- **Autorización**: JWT Tokens con claims
- **Encriptación**: AES-256-GCM
- **Key Management**: Azure Key Vault
- **Secret Detection**: Talisman + GitGuardian
- **OWASP Top 10**: Mitigaciones implementadas

## 📊 Estado del Proyecto

- **Fase actual**: Preparación (Setup inicial)
- **Próximo sprint**: Sprint 1 - SSO
- **Completado**: 0%

## 👥 Equipo

- Desarrollo Backend: .NET Microservices
- Desarrollo Frontend: Spring Boot
- Seguridad: SSO, Encriptación, Git-lock
- DevOps: CI/CD, Deployment

## 📝 Licencia

Este proyecto es académico y está desarrollado para el curso de Seguridad de Software.

## 🆘 Soporte

Para dudas o problemas:
1. Revisa la [documentación completa](../PROYECTO-SEGURIDAD-SOFTWARE/)
2. Consulta la sección de [Troubleshooting](../PROYECTO-SEGURIDAD-SOFTWARE/docs/guias-tecnicas/01-SETUP-SSO.md#troubleshooting)
3. Busca en Stack Overflow (tags: azure-ad-b2c, oauth-2.0, azure-key-vault)

---

**Última actualización**: 2025-11-20
**Versión**: 1.0-SNAPSHOT
