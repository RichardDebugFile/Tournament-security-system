using backend_authenticator.Data;
using backend_authenticator.Repositories.Interfaces;
using backend_model;
using Microsoft.EntityFrameworkCore;

namespace backend_authenticator.Repositories
{
    public class UsuariosRepositories : IUsuarios
    {
        private readonly AppDbContext _context;

        public UsuariosRepositories(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Usuarios>> GetAllAsync()
        {
            return await _context.Usuarios.ToListAsync();
        }

        public async Task<Usuarios> GetByIdAsync(int id)
        {
            return await _context.Usuarios.FindAsync(id);
        }

        public async Task AddAsync(Usuarios usuario)
        {
            await _context.Usuarios.AddAsync(usuario);
            await _context.SaveChangesAsync();
        }

        public async Task UpdateAsync(int id, Usuarios usuario)
        {
            var existingUsuario = await _context.Usuarios.FindAsync(id);
            if (existingUsuario != null)
            {
                existingUsuario.Gmail = usuario.Gmail;
                existingUsuario.Contrasena = usuario.Contrasena;

                _context.Entry(existingUsuario).State = EntityState.Modified;
                await _context.SaveChangesAsync();
            }
        }

        public async Task DeleteAsync(int id)
        {
            var usuario = await _context.Usuarios.FindAsync(id);
            if (usuario != null)
            {
                _context.Usuarios.Remove(usuario);
                await _context.SaveChangesAsync();
            }
        }

        public async Task<Usuarios> Authenticate(string gmail, string contrasena)
        {
            // Busca un usuario con las credenciales proporcionadas
            return await _context.Usuarios
                .FirstOrDefaultAsync(u => u.Gmail == gmail && u.Contrasena == contrasena);
        }

    }
}

