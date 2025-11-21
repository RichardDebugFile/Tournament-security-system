using System.ComponentModel.DataAnnotations;

namespace BackendIdentityServer.Models;

public class LoginRequest
{
    [Required(ErrorMessage = "El token de acceso es requerido")]
    public string AccessToken { get; set; } = string.Empty;

    [Required(ErrorMessage = "El proveedor de autenticación es requerido")]
    [RegularExpression("^(AzureAD|Google)$", ErrorMessage = "Proveedor inválido. Use 'AzureAD' o 'Google'")]
    public string Provider { get; set; } = string.Empty; // "AzureAD" o "Google"
}
