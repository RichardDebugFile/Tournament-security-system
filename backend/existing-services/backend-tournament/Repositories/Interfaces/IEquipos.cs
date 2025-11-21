using backend_model;

namespace backend_tournament.Repositories.Interfaces
{
    public interface IEquipos
    {
        Task<IEnumerable<Equipos>> GetALLAsync(); // Obtener todos los equipos con jugadores
        Task<Equipos> GetByIdAsync(int id); // Obtener un equipo con jugadores por ID
        Task AddAsync(Equipos equipo); // Agregar un nuevo equipo
        Task UpdateAsync(int id, Equipos equipo); // Actualizar un equipo existente
        Task DeleteAsync(int id); // Eliminar un equipo y sus jugadores relacionados
    }
}

