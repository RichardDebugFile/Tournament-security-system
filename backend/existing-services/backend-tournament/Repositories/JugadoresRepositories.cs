using backend_model;
using backend_tournament.Data;
using backend_tournament.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace backend_tournament.Repositories
{
    public class JugadoresRepositories : IJugadores
    {
        private readonly AppDbContext _context;

        public JugadoresRepositories(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Jugadores>> GetALLAsync()
        {
            return await _context.Jugadores
                                 .Include(j => j.Equipo) // Incluir equipo asociado
                                 .ToListAsync();
        }

        public async Task<Jugadores> GetByIdAsync(int id)
        {
            return await _context.Jugadores
                                 .Include(j => j.Equipo) // Incluir equipo asociado
                                 .FirstOrDefaultAsync(j => j.Id == id);
        }

        public async Task AddAsync(Jugadores jugador)
        {
            if (jugador.EquipoId == null)
                throw new ArgumentException("El jugador debe estar asociado a un equipo válido.");

            await _context.Jugadores.AddAsync(jugador);
            await _context.SaveChangesAsync();
        }

        public async Task UpdateAsync(int id, Jugadores jugador)
        {
            var existingJugador = await _context.Jugadores.FindAsync(id);
            if (existingJugador == null)
                throw new KeyNotFoundException($"No se encontró un jugador con Id {id}");

            // Actualizar los campos necesarios
            existingJugador.Nombre = jugador.Nombre;
            existingJugador.Apellido = jugador.Apellido;
            existingJugador.Cedula = jugador.Cedula;
            existingJugador.NumCamiseta = jugador.NumCamiseta;
            existingJugador.Lateralidad = jugador.Lateralidad;

            // Actualizar equipo si es necesario
            existingJugador.EquipoId = jugador.EquipoId;

            await _context.SaveChangesAsync();
        }

        public async Task DeleteAsync(int id)
        {
            var jugador = await _context.Jugadores.FindAsync(id);
            if (jugador == null)
                throw new KeyNotFoundException($"No se encontró un jugador con Id {id}");

            _context.Jugadores.Remove(jugador);
            await _context.SaveChangesAsync();
        }
    }
}
