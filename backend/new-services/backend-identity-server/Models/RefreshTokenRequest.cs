using System.ComponentModel.DataAnnotations;

namespace BackendIdentityServer.Models;

public class RefreshTokenRequest
{
    [Required(ErrorMessage = "El refresh token es requerido")]
    public string RefreshToken { get; set; } = string.Empty;
}
