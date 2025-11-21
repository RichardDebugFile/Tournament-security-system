using backend_model;

namespace backend_tournament.Repositories.Interfaces
{
    public interface ITorneos
    {
        Task<IEnumerable<Torneos>> GetALLAsync(); // Obtener todos los torneos con sus equipos
        Task<Torneos> GetByIdAsync(int id); // Obtener un torneo con sus equipos por ID
        Task AddAsync(Torneos torneos); // Agregar un torneo
        Task UpdateAsync(int id, Torneos torneos); // Actualizar un torneo
        Task DeleteAsync(int id); // Eliminar un torneo y sus equipos asociados
    }
}
