using System.Net.Http.Headers;
using System.Text.Json;
using BackendIdentityServer.Models;

namespace BackendIdentityServer.Services;

public class GoogleOAuthService : IGoogleOAuthService
{
    private readonly HttpClient _httpClient;
    private readonly IConfiguration _configuration;
    private readonly ILogger<GoogleOAuthService> _logger;

    public GoogleOAuthService(HttpClient httpClient, IConfiguration configuration, ILogger<GoogleOAuthService> logger)
    {
        _httpClient = httpClient;
        _configuration = configuration;
        _logger = logger;
    }

    public async Task<UserInfo?> ValidateTokenAndGetUserInfoAsync(string accessToken)
    {
        try
        {
            // Google UserInfo endpoint
            var googleApiUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

            _httpClient.DefaultRequestHeaders.Authorization =
                new AuthenticationHeaderValue("Bearer", accessToken);

            var response = await _httpClient.GetAsync(googleApiUrl);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogWarning("Error validando token de Google: {StatusCode}", response.StatusCode);
                return null;
            }

            var content = await response.Content.ReadAsStringAsync();
            var googleUser = JsonSerializer.Deserialize<GoogleUserResponse>(content, new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            });

            if (googleUser == null)
            {
                _logger.LogWarning("No se pudo deserializar la respuesta de Google");
                return null;
            }

            return new UserInfo
            {
                Email = googleUser.Email ?? string.Empty,
                FirstName = googleUser.GivenName,
                LastName = googleUser.FamilyName,
                ProfilePictureUrl = googleUser.Picture,
                AuthProvider = "Google"
            };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error al validar token de Google");
            return null;
        }
    }

    // Clase interna para deserializar respuesta de Google
    private class GoogleUserResponse
    {
        public string? Id { get; set; }
        public string? Email { get; set; }
        public bool? VerifiedEmail { get; set; }
        public string? Name { get; set; }
        public string? GivenName { get; set; }
        public string? FamilyName { get; set; }
        public string? Picture { get; set; }
        public string? Locale { get; set; }
    }
}
