using backend_model;
using Microsoft.EntityFrameworkCore;

namespace backend_tournament.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        // Tablas
        public DbSet<Jugadores> Jugadores { get; set; }
        public DbSet<Equipos> Equipos { get; set; }
        public DbSet<Torneos> Torneos { get; set; }
        public DbSet<Usuarios> Usuarios { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Equipos>()
                .HasMany(e => e.Jugadores)
                .WithOne()
                .OnDelete(DeleteBehavior.Cascade); // Permitir eliminar equipos y sus jugadores relacionados

            modelBuilder.Entity<Equipos>()
                .HasOne(e => e.Torneo)
                .WithMany(t => t.Equipos)
                .HasForeignKey(e => e.TorneoId)
                .OnDelete(DeleteBehavior.SetNull); // Si se elimina el torneo, el campo TorneoId será null
        }

    }
}
