using BackendIdentityServer.Models;

namespace BackendIdentityServer.Services;

public interface IAzureAdService
{
    Task<UserInfo?> ValidateTokenAndGetUserInfoAsync(string accessToken);
}
