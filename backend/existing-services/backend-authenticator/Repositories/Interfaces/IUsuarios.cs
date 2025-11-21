using backend_model;

namespace backend_authenticator.Repositories.Interfaces
{
    public interface IUsuarios
    {
        Task<IEnumerable<Usuarios>> GetAllAsync(); // Obtener todos los usuarios
        Task<Usuarios> GetByIdAsync(int id); // Obtener un usuario por ID
        Task AddAsync(Usuarios usuario); // Agregar un nuevo usuario
        Task UpdateAsync(int id, Usuarios usuario); // Actualizar un usuario existente
        Task DeleteAsync(int id); // Eliminar un usuario por ID
        Task<Usuarios> Authenticate(string gmail, string contrasena);

    }
}