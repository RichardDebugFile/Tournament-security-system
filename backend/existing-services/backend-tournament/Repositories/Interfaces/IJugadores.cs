using backend_model;

namespace backend_tournament.Repositories.Interfaces
{
    public interface IJugadores
    {
        Task<IEnumerable<Jugadores>> GetALLAsync(); // Obtener todos los jugadores con sus equipos
        Task<Jugadores> GetByIdAsync(int id); // Obtener un jugador con su equipo por ID
        Task AddAsync(Jugadores jugador); // Agregar un nuevo jugador
        Task UpdateAsync(int id, Jugadores jugador); // Actualizar un jugador existente
        Task DeleteAsync(int id); // Eliminar un jugador
    }
}
