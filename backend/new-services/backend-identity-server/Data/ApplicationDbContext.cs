using Microsoft.EntityFrameworkCore;
using BackendIdentityServer.Models;

namespace BackendIdentityServer.Data;

public class ApplicationDbContext : DbContext
{
    public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
        : base(options)
    {
    }

    public DbSet<ApplicationUser> Users { get; set; } = null!;

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<ApplicationUser>(entity =>
        {
            entity.HasKey(e => e.Id);

            // Índice único en Email
            entity.HasIndex(e => e.Email)
                .IsUnique();

            // Índice compuesto en Provider + ExternalId (único)
            entity.HasIndex(e => new { e.AuthProvider, e.ExternalId })
                .IsUnique()
                .HasDatabaseName("IX_Provider_ExternalId");

            // Índice en IsActive para consultas de usuarios activos
            entity.HasIndex(e => e.IsActive);

            // Configuración de columnas
            entity.Property(e => e.Email)
                .IsRequired()
                .HasMaxLength(256);

            entity.Property(e => e.FirstName)
                .HasMaxLength(256);

            entity.Property(e => e.LastName)
                .HasMaxLength(256);

            entity.Property(e => e.AuthProvider)
                .IsRequired()
                .HasMaxLength(50);

            entity.Property(e => e.ExternalId)
                .IsRequired()
                .HasMaxLength(512);

            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("GETUTCDATE()");

            entity.Property(e => e.IsActive)
                .HasDefaultValue(true);
        });
    }
}
