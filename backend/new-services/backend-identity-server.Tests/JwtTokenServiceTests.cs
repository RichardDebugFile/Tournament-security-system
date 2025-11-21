using System;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using FluentAssertions;
using Microsoft.Extensions.Configuration;
using Moq;
using Xunit;
using BackendIdentityServer.Services;
using BackendIdentityServer.Models;
using BackendIdentityServer.Data;
using Microsoft.EntityFrameworkCore;

namespace IdentityServer.Tests;

public class JwtTokenServiceTests
{
    private readonly Mock<IConfiguration> _mockConfig;
    private readonly ApplicationDbContext _context;
    private readonly JwtTokenService _service;

    public JwtTokenServiceTests()
    {
        // Setup InMemory Database
        var options = new DbContextOptionsBuilder<ApplicationDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        _context = new ApplicationDbContext(options);

        // Setup Mock Configuration
        _mockConfig = new Mock<IConfiguration>();
        _mockConfig.Setup(c => c["JWT:SigningKey"]).Returns("tournament-test-secret-key-minimum-32-characters-for-testing");
        _mockConfig.Setup(c => c["JWT:Issuer"]).Returns("TestIssuer");
        _mockConfig.Setup(c => c["JWT:Audience"]).Returns("TestAudience");
        _mockConfig.Setup(c => c["JWT:ExpiryMinutes"]).Returns("60");

        _service = new JwtTokenService(_mockConfig.Object, _context);
    }

    [Fact]
    public void GenerateAccessToken_ValidUser_ReturnsValidJwtToken()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            FirstName = "John",
            LastName = "Doe",
            AuthProvider = "Google",
            ExternalId = "google123",
            ProfilePictureUrl = "https://example.com/picture.jpg",
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Act
        var token = _service.GenerateAccessToken(user);

        // Assert
        token.Should().NotBeNullOrEmpty();

        var tokenHandler = new JwtSecurityTokenHandler();
        var jwtToken = tokenHandler.ReadJwtToken(token);

        jwtToken.Issuer.Should().Be("TestIssuer");
        jwtToken.Audiences.Should().Contain("TestAudience");
        jwtToken.ValidTo.Should().BeCloseTo(DateTime.UtcNow.AddMinutes(60), TimeSpan.FromSeconds(5));

        // Verify claims
        jwtToken.Claims.Should().Contain(c => c.Type == ClaimTypes.NameIdentifier && c.Value == user.Id.ToString());
        jwtToken.Claims.Should().Contain(c => c.Type == ClaimTypes.Email && c.Value == user.Email);
        jwtToken.Claims.Should().Contain(c => c.Type == "auth_provider" && c.Value == user.AuthProvider);
        jwtToken.Claims.Should().Contain(c => c.Type == "external_id" && c.Value == user.ExternalId);
        jwtToken.Claims.Should().Contain(c => c.Type == ClaimTypes.GivenName && c.Value == user.FirstName);
        jwtToken.Claims.Should().Contain(c => c.Type == ClaimTypes.Surname && c.Value == user.LastName);
        jwtToken.Claims.Should().Contain(c => c.Type == "picture" && c.Value == user.ProfilePictureUrl);
        jwtToken.Claims.Should().Contain(c => c.Type == JwtRegisteredClaimNames.Jti);
        jwtToken.Claims.Should().Contain(c => c.Type == JwtRegisteredClaimNames.Iat);
    }

    [Fact]
    public void GenerateAccessToken_UserWithoutOptionalFields_ReturnsValidToken()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "minimal@example.com",
            AuthProvider = "Azure",
            ExternalId = "azure456",
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Act
        var token = _service.GenerateAccessToken(user);

        // Assert
        token.Should().NotBeNullOrEmpty();

        var tokenHandler = new JwtSecurityTokenHandler();
        var jwtToken = tokenHandler.ReadJwtToken(token);

        jwtToken.Claims.Should().NotContain(c => c.Type == ClaimTypes.GivenName);
        jwtToken.Claims.Should().NotContain(c => c.Type == ClaimTypes.Surname);
        jwtToken.Claims.Should().NotContain(c => c.Type == "picture");
    }

    [Fact]
    public void GenerateAccessToken_MissingSigningKey_ThrowsInvalidOperationException()
    {
        // Arrange
        _mockConfig.Setup(c => c["JWT:SigningKey"]).Returns((string)null);
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Act & Assert
        Action act = () => _service.GenerateAccessToken(user);
        act.Should().Throw<InvalidOperationException>()
            .WithMessage("JWT:SigningKey no configurado");
    }

    [Fact]
    public void GenerateRefreshToken_ReturnsUniqueToken()
    {
        // Act
        var token1 = _service.GenerateRefreshToken();
        var token2 = _service.GenerateRefreshToken();

        // Assert
        token1.Should().NotBeNullOrEmpty();
        token2.Should().NotBeNullOrEmpty();
        token1.Should().NotBe(token2);
        token1.Length.Should().BeGreaterThan(50); // Base64 encoded 64 bytes
    }

    [Fact]
    public void ValidateRefreshToken_ValidToken_ReturnsTrue()
    {
        // Arrange
        var refreshToken = "valid-refresh-token-12345";
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            RefreshToken = refreshToken,
            RefreshTokenExpiresAt = DateTime.UtcNow.AddDays(7),
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Act
        var result = _service.ValidateRefreshToken(refreshToken, user);

        // Assert
        result.Should().BeTrue();
    }

    [Fact]
    public void ValidateRefreshToken_MismatchedToken_ReturnsFalse()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            RefreshToken = "correct-token",
            RefreshTokenExpiresAt = DateTime.UtcNow.AddDays(7),
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Act
        var result = _service.ValidateRefreshToken("wrong-token", user);

        // Assert
        result.Should().BeFalse();
    }

    [Fact]
    public void ValidateRefreshToken_ExpiredToken_ReturnsFalse()
    {
        // Arrange
        var refreshToken = "expired-token";
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            RefreshToken = refreshToken,
            RefreshTokenExpiresAt = DateTime.UtcNow.AddDays(-1), // Expired yesterday
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Act
        var result = _service.ValidateRefreshToken(refreshToken, user);

        // Assert
        result.Should().BeFalse();
    }

    [Fact]
    public void ValidateRefreshToken_NullUserRefreshToken_ReturnsFalse()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            RefreshToken = null,
            RefreshTokenExpiresAt = DateTime.UtcNow.AddDays(7),
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Act
        var result = _service.ValidateRefreshToken("any-token", user);

        // Assert
        result.Should().BeFalse();
    }

    [Fact]
    public async Task ValidateAccessTokenAsync_ValidToken_ReturnsUser()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            FirstName = "John",
            LastName = "Doe",
            AuthProvider = "Google",
            ExternalId = "google123",
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        await _context.Users.AddAsync(user);
        await _context.SaveChangesAsync();

        var token = _service.GenerateAccessToken(user);

        // Act
        var result = await _service.ValidateAccessTokenAsync(token);

        // Assert
        result.Should().NotBeNull();
        result.Id.Should().Be(user.Id);
        result.Email.Should().Be(user.Email);
    }

    [Fact]
    public async Task ValidateAccessTokenAsync_InvalidToken_ReturnsNull()
    {
        // Act
        var result = await _service.ValidateAccessTokenAsync("invalid.token.here");

        // Assert
        result.Should().BeNull();
    }

    [Fact]
    public async Task ValidateAccessTokenAsync_InactiveUser_ReturnsNull()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "inactive@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            IsActive = false, // Usuario inactivo
            CreatedAt = DateTime.UtcNow
        };

        await _context.Users.AddAsync(user);
        await _context.SaveChangesAsync();

        var token = _service.GenerateAccessToken(user);

        // Act
        var result = await _service.ValidateAccessTokenAsync(token);

        // Assert
        result.Should().BeNull();
    }

    [Fact]
    public async Task ValidateAccessTokenAsync_NonExistentUser_ReturnsNull()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "deleted@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        // Generate token but don't save user to database
        var token = _service.GenerateAccessToken(user);

        // Act
        var result = await _service.ValidateAccessTokenAsync(token);

        // Assert
        result.Should().BeNull();
    }

    [Fact]
    public async Task ValidateAccessTokenAsync_TamperedToken_ReturnsNull()
    {
        // Arrange
        var user = new ApplicationUser
        {
            Id = Guid.NewGuid(),
            Email = "test@example.com",
            AuthProvider = "Google",
            ExternalId = "google123",
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        await _context.Users.AddAsync(user);
        await _context.SaveChangesAsync();

        var token = _service.GenerateAccessToken(user);

        // Tamper with the token
        var tamperedToken = token.Substring(0, token.Length - 10) + "TAMPERED12";

        // Act
        var result = await _service.ValidateAccessTokenAsync(tamperedToken);

        // Assert
        result.Should().BeNull();
    }

    public void Dispose()
    {
        _context.Dispose();
    }
}
