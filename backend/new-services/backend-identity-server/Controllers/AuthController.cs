using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using BackendIdentityServer.Data;
using BackendIdentityServer.Models;
using BackendIdentityServer.Services;

namespace BackendIdentityServer.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly ApplicationDbContext _context;
    private readonly IJwtTokenService _jwtTokenService;
    private readonly IAzureAdService _azureAdService;
    private readonly IGoogleOAuthService _googleOAuthService;
    private readonly ILogger<AuthController> _logger;
    private readonly IConfiguration _configuration;

    public AuthController(
        ApplicationDbContext context,
        IJwtTokenService jwtTokenService,
        IAzureAdService azureAdService,
        IGoogleOAuthService googleOAuthService,
        ILogger<AuthController> logger,
        IConfiguration configuration)
    {
        _context = context;
        _jwtTokenService = jwtTokenService;
        _azureAdService = azureAdService;
        _googleOAuthService = googleOAuthService;
        _logger = logger;
        _configuration = configuration;
    }

    /// <summary>
    /// Endpoint de login que valida token de Azure AD o Google y retorna JWT
    /// </summary>
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest request)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            UserInfo? externalUserInfo = null;

            // Validar token según el proveedor
            if (request.Provider == "AzureAD")
            {
                externalUserInfo = await _azureAdService.ValidateTokenAndGetUserInfoAsync(request.AccessToken);
            }
            else if (request.Provider == "Google")
            {
                externalUserInfo = await _googleOAuthService.ValidateTokenAndGetUserInfoAsync(request.AccessToken);
            }
            else
            {
                return BadRequest(new { error = "Proveedor de autenticación no soportado" });
            }

            if (externalUserInfo == null)
            {
                return Unauthorized(new { error = "Token inválido o expirado" });
            }

            // Buscar o crear usuario en nuestra base de datos
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Email == externalUserInfo.Email && u.AuthProvider == request.Provider);

            if (user == null)
            {
                // Crear nuevo usuario
                user = new ApplicationUser
                {
                    Email = externalUserInfo.Email,
                    FirstName = externalUserInfo.FirstName,
                    LastName = externalUserInfo.LastName,
                    AuthProvider = request.Provider,
                    ExternalId = externalUserInfo.Email, // Usamos email como external ID temporalmente
                    ProfilePictureUrl = externalUserInfo.ProfilePictureUrl,
                    CreatedAt = DateTime.UtcNow,
                    IsActive = true
                };

                _context.Users.Add(user);
            }
            else
            {
                // Actualizar información del usuario existente
                user.FirstName = externalUserInfo.FirstName;
                user.LastName = externalUserInfo.LastName;
                user.ProfilePictureUrl = externalUserInfo.ProfilePictureUrl;
                user.LastLoginAt = DateTime.UtcNow;
            }

            // Generar tokens
            var accessToken = _jwtTokenService.GenerateAccessToken(user);
            var refreshToken = _jwtTokenService.GenerateRefreshToken();

            // Guardar refresh token
            var refreshTokenExpiryDays = int.Parse(_configuration["JWT:RefreshTokenExpiryDays"] ?? "7");
            user.RefreshToken = refreshToken;
            user.RefreshTokenExpiresAt = DateTime.UtcNow.AddDays(refreshTokenExpiryDays);

            await _context.SaveChangesAsync();

            var expiryMinutes = int.Parse(_configuration["JWT:ExpiryMinutes"] ?? "60");

            var response = new TokenResponse
            {
                AccessToken = accessToken,
                RefreshToken = refreshToken,
                ExpiresIn = expiryMinutes * 60, // Convertir a segundos
                ExpiresAt = DateTime.UtcNow.AddMinutes(expiryMinutes),
                User = new UserInfo
                {
                    Id = user.Id,
                    Email = user.Email,
                    FirstName = user.FirstName,
                    LastName = user.LastName,
                    AuthProvider = user.AuthProvider,
                    ProfilePictureUrl = user.ProfilePictureUrl,
                    CreatedAt = user.CreatedAt,
                    LastLoginAt = user.LastLoginAt
                }
            };

            _logger.LogInformation("Usuario {Email} autenticado exitosamente con {Provider}", user.Email, request.Provider);

            return Ok(response);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error durante el proceso de login");
            return StatusCode(500, new { error = "Error interno del servidor" });
        }
    }

    /// <summary>
    /// Endpoint para refrescar el access token usando el refresh token
    /// </summary>
    [HttpPost("refresh")]
    public async Task<IActionResult> RefreshToken([FromBody] RefreshTokenRequest request)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.RefreshToken == request.RefreshToken && u.IsActive);

            if (user == null)
            {
                return Unauthorized(new { error = "Refresh token inválido" });
            }

            if (!_jwtTokenService.ValidateRefreshToken(request.RefreshToken, user))
            {
                return Unauthorized(new { error = "Refresh token expirado o inválido" });
            }

            // Generar nuevos tokens
            var accessToken = _jwtTokenService.GenerateAccessToken(user);
            var refreshToken = _jwtTokenService.GenerateRefreshToken();

            // Actualizar refresh token
            var refreshTokenExpiryDays = int.Parse(_configuration["JWT:RefreshTokenExpiryDays"] ?? "7");
            user.RefreshToken = refreshToken;
            user.RefreshTokenExpiresAt = DateTime.UtcNow.AddDays(refreshTokenExpiryDays);

            await _context.SaveChangesAsync();

            var expiryMinutes = int.Parse(_configuration["JWT:ExpiryMinutes"] ?? "60");

            var response = new TokenResponse
            {
                AccessToken = accessToken,
                RefreshToken = refreshToken,
                ExpiresIn = expiryMinutes * 60,
                ExpiresAt = DateTime.UtcNow.AddMinutes(expiryMinutes),
                User = new UserInfo
                {
                    Id = user.Id,
                    Email = user.Email,
                    FirstName = user.FirstName,
                    LastName = user.LastName,
                    AuthProvider = user.AuthProvider,
                    ProfilePictureUrl = user.ProfilePictureUrl,
                    CreatedAt = user.CreatedAt,
                    LastLoginAt = user.LastLoginAt
                }
            };

            _logger.LogInformation("Token refrescado exitosamente para usuario {Email}", user.Email);

            return Ok(response);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error al refrescar token");
            return StatusCode(500, new { error = "Error interno del servidor" });
        }
    }

    /// <summary>
    /// Endpoint para validar un access token y obtener información del usuario
    /// </summary>
    [HttpPost("validate")]
    public async Task<IActionResult> ValidateToken([FromBody] ValidateTokenRequest request)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var user = await _jwtTokenService.ValidateAccessTokenAsync(request.AccessToken);

            if (user == null)
            {
                return Unauthorized(new { error = "Token inválido o expirado" });
            }

            var userInfo = new UserInfo
            {
                Id = user.Id,
                Email = user.Email,
                FirstName = user.FirstName,
                LastName = user.LastName,
                AuthProvider = user.AuthProvider,
                ProfilePictureUrl = user.ProfilePictureUrl,
                CreatedAt = user.CreatedAt,
                LastLoginAt = user.LastLoginAt
            };

            return Ok(new { valid = true, user = userInfo });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error al validar token");
            return StatusCode(500, new { error = "Error interno del servidor" });
        }
    }

    /// <summary>
    /// Endpoint de logout que invalida el refresh token
    /// </summary>
    [HttpPost("logout")]
    public async Task<IActionResult> Logout([FromBody] LogoutRequest request)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Id == request.UserId && u.IsActive);

            if (user != null)
            {
                // Invalidar refresh token
                user.RefreshToken = null;
                user.RefreshTokenExpiresAt = null;

                await _context.SaveChangesAsync();

                _logger.LogInformation("Usuario {Email} cerró sesión exitosamente", user.Email);
            }

            return Ok(new { message = "Sesión cerrada exitosamente" });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error durante el logout");
            return StatusCode(500, new { error = "Error interno del servidor" });
        }
    }
}

// Modelos adicionales para los endpoints
public class ValidateTokenRequest
{
    public string AccessToken { get; set; } = string.Empty;
}

public class LogoutRequest
{
    public Guid UserId { get; set; }
}
