using System.Net.Http.Headers;
using System.Text.Json;
using BackendIdentityServer.Models;

namespace BackendIdentityServer.Services;

public class AzureAdService : IAzureAdService
{
    private readonly HttpClient _httpClient;
    private readonly IConfiguration _configuration;
    private readonly ILogger<AzureAdService> _logger;

    public AzureAdService(HttpClient httpClient, IConfiguration configuration, ILogger<AzureAdService> logger)
    {
        _httpClient = httpClient;
        _configuration = configuration;
        _logger = logger;
    }

    public async Task<UserInfo?> ValidateTokenAndGetUserInfoAsync(string accessToken)
    {
        try
        {
            // Microsoft Graph API endpoint para obtener información del usuario
            var graphApiUrl = "https://graph.microsoft.com/v1.0/me";

            _httpClient.DefaultRequestHeaders.Authorization =
                new AuthenticationHeaderValue("Bearer", accessToken);

            var response = await _httpClient.GetAsync(graphApiUrl);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogWarning("Error validando token de Azure AD: {StatusCode}", response.StatusCode);
                return null;
            }

            var content = await response.Content.ReadAsStringAsync();
            var azureUser = JsonSerializer.Deserialize<AzureUserResponse>(content, new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            });

            if (azureUser == null)
            {
                _logger.LogWarning("No se pudo deserializar la respuesta de Azure AD");
                return null;
            }

            return new UserInfo
            {
                Email = azureUser.Mail ?? azureUser.UserPrincipalName ?? string.Empty,
                FirstName = azureUser.GivenName,
                LastName = azureUser.Surname,
                ProfilePictureUrl = null, // Se puede obtener con un endpoint adicional si se necesita
                AuthProvider = "AzureAD"
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error al validar token de Azure AD");
            return null;
        }
    }

    // Clase interna para deserializar respuesta de Microsoft Graph
    private class AzureUserResponse
    {
        public string? Id { get; set; }
        public string? UserPrincipalName { get; set; }
        public string? Mail { get; set; }
        public string? GivenName { get; set; }
        public string? Surname { get; set; }
        public string? DisplayName { get; set; }
    }
}
