namespace BackendIdentityServer.Models;

public class TokenResponse
{
    public string AccessToken { get; set; } = string.Empty;

    public string RefreshToken { get; set; } = string.Empty;

    public int ExpiresIn { get; set; } // Segundos hasta expiración

    public string TokenType { get; set; } = "Bearer";

    public DateTime IssuedAt { get; set; } = DateTime.UtcNow;

    public DateTime ExpiresAt { get; set; }

    public UserInfo User { get; set; } = new UserInfo();
}
