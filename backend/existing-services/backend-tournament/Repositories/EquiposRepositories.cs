using backend_model;
using backend_tournament.Data;
using backend_tournament.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend_tournament.Repositories
{
    public class EquiposRepositories : IEquipos
    {
        private readonly AppDbContext _context;

        public EquiposRepositories(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Equipos>> GetALLAsync()
        {
            return await _context.Equipos
                                 .Include(e => e.Jugadores) // Incluir jugadores relacionados
                                 .Include(e => e.Torneo) // Incluir torneo relacionado
                                 .ToListAsync();
        }

        public async Task<Equipos> GetByIdAsync(int id)
        {
            return await _context.Equipos
                                 .Include(e => e.Jugadores) // Incluir jugadores relacionados
                                 .Include(e => e.Torneo) // Incluir torneo relacionado
                                 .FirstOrDefaultAsync(e => e.Id == id);
        }

        public async Task AddAsync(Equipos equipo)
        {
            equipo.Jugadores = equipo.Jugadores ?? new List<Jugadores>(); // Asegurar que no sea null
            await _context.Equipos.AddAsync(equipo);
            await _context.SaveChangesAsync();
        }


        public async Task UpdateAsync(int id, Equipos equipo)
        {
            var existingEquipo = await _context.Equipos.FirstOrDefaultAsync(e => e.Id == id);
            if (existingEquipo != null)
            {
                existingEquipo.NombreEquipo = equipo.NombreEquipo;
                existingEquipo.TorneoId = equipo.TorneoId; // Actualizar torneo
                                                           // No actualizamos jugadores aquí, eso debe manejarse en su propia lógica
                await _context.SaveChangesAsync();
            }
        }


        public async Task DeleteAsync(int id)
        {
            var equipo = await _context.Equipos
                                        .Include(e => e.Jugadores) // Incluir jugadores
                                        .FirstOrDefaultAsync(e => e.Id == id);
            if (equipo != null)
            {
                _context.Equipos.Remove(equipo);
                await _context.SaveChangesAsync();
            }
        }
    }
}
