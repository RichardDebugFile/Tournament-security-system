using backend_model;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;

namespace backend_authenticator.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        // Tablas

        public DbSet<Usuarios> Usuarios { get; set; }
    }
}