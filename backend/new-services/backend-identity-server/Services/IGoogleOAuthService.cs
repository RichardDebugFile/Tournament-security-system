using BackendIdentityServer.Models;

namespace BackendIdentityServer.Services;

public interface IGoogleOAuthService
{
    Task<UserInfo?> ValidateTokenAndGetUserInfoAsync(string accessToken);
}
