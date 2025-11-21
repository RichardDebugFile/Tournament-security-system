using System.ComponentModel.DataAnnotations;

namespace BackendIdentityServer.Models;

public class ApplicationUser
{
    [Key]
    public Guid Id { get; set; } = Guid.NewGuid();

    [Required]
    [EmailAddress]
    [MaxLength(256)]
    public string Email { get; set; } = string.Empty;

    [MaxLength(256)]
    public string? FirstName { get; set; }

    [MaxLength(256)]
    public string? LastName { get; set; }

    [Required]
    [MaxLength(50)]
    public string AuthProvider { get; set; } = string.Empty; // "AzureAD" o "Google"

    [Required]
    [MaxLength(512)]
    public string ExternalId { get; set; } = string.Empty; // ID del proveedor externo

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    public DateTime? LastLoginAt { get; set; }

    public bool IsActive { get; set; } = true;

    // Metadata adicional del proveedor
    public string? ProfilePictureUrl { get; set; }

    // Refresh token almacenado (encriptado en producción)
    public string? RefreshToken { get; set; }

    public DateTime? RefreshTokenExpiresAt { get; set; }
}
