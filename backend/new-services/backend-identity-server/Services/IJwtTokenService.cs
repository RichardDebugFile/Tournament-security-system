using BackendIdentityServer.Models;

namespace BackendIdentityServer.Services;

public interface IJwtTokenService
{
    string GenerateAccessToken(ApplicationUser user);
    string GenerateRefreshToken();
    bool ValidateRefreshToken(string refreshToken, ApplicationUser user);
    Task<ApplicationUser?> ValidateAccessTokenAsync(string accessToken);
}
