$clientId = "tournament-system"
$clientSecret = "aTw5ZcUI18GfYv7LTUzYDuqPQ6W24sin"
$tokenUrl = "http://localhost:8090/realms/tournament/protocol/openid-connect/token"

Write-Host "Testing Keycloak Connection..."
Write-Host "Client ID: $clientId"
Write-Host "URL: $tokenUrl"

try {
    $body = @{
        grant_type = "client_credentials"
        client_id = $clientId
        client_secret = $clientSecret
    }

    $response = Invoke-RestMethod -Uri $tokenUrl -Method Post -Body $body -ErrorAction Stop
    Write-Host "✅ SUCCESS: Authentication successful! Token received." -ForegroundColor Green
}
catch {
    Write-Host "❌ ERROR: Authentication failed." -ForegroundColor Red
    Write-Host $_.Exception.Message
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody" -ForegroundColor Yellow
    }
    Write-Host "`nPOSSIBLE CAUSES:"
    Write-Host "1. The Client Secret in application.properties does not match Keycloak."
    Write-Host "2. The Client ID 'tournament-system' does not exist in Keycloak."
    Write-Host "3. The realm 'tournament' does not exist."
}
