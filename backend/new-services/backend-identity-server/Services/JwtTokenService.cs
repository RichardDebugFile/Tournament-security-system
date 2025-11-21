using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using BackendIdentityServer.Models;
using BackendIdentityServer.Data;
using Microsoft.EntityFrameworkCore;

namespace BackendIdentityServer.Services;

public class JwtTokenService : IJwtTokenService
{
    private readonly IConfiguration _configuration;
    private readonly ApplicationDbContext _context;

    public JwtTokenService(IConfiguration configuration, ApplicationDbContext context)
    {
        _configuration = configuration;
        _context = context;
    }

    public string GenerateAccessToken(ApplicationUser user)
    {
        var signingKey = _configuration["JWT:SigningKey"]
            ?? throw new InvalidOperationException("JWT:SigningKey no configurado");

        var issuer = _configuration["JWT:Issuer"] ?? "IdentityServer";
        var audience = _configuration["JWT:Audience"] ?? "TournamentAPI";
        var expiryMinutes = int.Parse(_configuration["JWT:ExpiryMinutes"] ?? "60");

        var claims = new List<Claim>
        {
            new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
            new Claim(ClaimTypes.Email, user.Email),
            new Claim("auth_provider", user.AuthProvider),
            new Claim("external_id", user.ExternalId),
            new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
            new Claim(JwtRegisteredClaimNames.Iat, DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString())
        };

        if (!string.IsNullOrEmpty(user.FirstName))
            claims.Add(new Claim(ClaimTypes.GivenName, user.FirstName));

        if (!string.IsNullOrEmpty(user.LastName))
            claims.Add(new Claim(ClaimTypes.Surname, user.LastName));

        if (!string.IsNullOrEmpty(user.ProfilePictureUrl))
            claims.Add(new Claim("picture", user.ProfilePictureUrl));

        var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(signingKey));
        var credentials = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

        var token = new JwtSecurityToken(
            issuer: issuer,
            audience: audience,
            claims: claims,
            expires: DateTime.UtcNow.AddMinutes(expiryMinutes),
            signingCredentials: credentials
        );

        return new JwtSecurityTokenHandler().WriteToken(token);
    }

    public string GenerateRefreshToken()
    {
        var randomNumber = new byte[64];
        using var rng = RandomNumberGenerator.Create();
        rng.GetBytes(randomNumber);
        return Convert.ToBase64String(randomNumber);
    }

    public bool ValidateRefreshToken(string refreshToken, ApplicationUser user)
    {
        if (string.IsNullOrEmpty(user.RefreshToken) || user.RefreshToken != refreshToken)
            return false;

        if (user.RefreshTokenExpiresAt == null || user.RefreshTokenExpiresAt < DateTime.UtcNow)
            return false;

        return true;
    }

    public async Task<ApplicationUser?> ValidateAccessTokenAsync(string accessToken)
    {
        try
        {
            var signingKey = _configuration["JWT:SigningKey"]
                ?? throw new InvalidOperationException("JWT:SigningKey no configurado");

            var tokenHandler = new JwtSecurityTokenHandler();
            var key = Encoding.UTF8.GetBytes(signingKey);

            var validationParameters = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(key),
                ValidateIssuer = true,
                ValidIssuer = _configuration["JWT:Issuer"] ?? "IdentityServer",
                ValidateAudience = true,
                ValidAudience = _configuration["JWT:Audience"] ?? "TournamentAPI",
                ValidateLifetime = true,
                ClockSkew = TimeSpan.Zero
            };

            var principal = tokenHandler.ValidateToken(accessToken, validationParameters, out var validatedToken);

            var userIdClaim = principal.FindFirst(ClaimTypes.NameIdentifier);
            if (userIdClaim == null || !Guid.TryParse(userIdClaim.Value, out var userId))
                return null;

            return await _context.Users.FirstOrDefaultAsync(u => u.Id == userId && u.IsActive);
        }
        catch
        {
            return null;
        }
    }
}
