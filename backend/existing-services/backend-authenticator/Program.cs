using backend_authenticator.Repositories.Interfaces;
using backend_authenticator.Repositories;
using Microsoft.EntityFrameworkCore;
using backend_authenticator.Data;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Text;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Configurar DbContext
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

// Configurar JWT Authentication
var jwtSigningKey = builder.Configuration["JWT:SigningKey"] 
    ?? throw new InvalidOperationException("JWT:SigningKey not found in configuration.");

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtSigningKey)),
        ValidateIssuer = true,
        ValidIssuer = builder.Configuration["JWT:Issuer"] ?? "IdentityServer",
        ValidateAudience = true,
        ValidAudience = builder.Configuration["JWT:Audience"] ?? "TournamentAPI",
        ValidateLifetime = true,
        ClockSkew = TimeSpan.Zero
    };
});

// Registrar repositorios
builder.Services.AddScoped<IUsuarios, UsuariosRepositories>();
var app = builder.Build();


// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthentication(); // Agregar Authentication antes de Authorization
app.UseAuthorization();

app.MapControllers();

app.Run();