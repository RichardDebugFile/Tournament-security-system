using backend_model;
using backend_tournament.Data;
using backend_tournament.Repositories.Interfaces;
using backend_tournament.Services;
using Microsoft.EntityFrameworkCore;

namespace backend_tournament.Repositories
{
    public class TorneosRepositories : ITorneos
    {
        private readonly AppDbContext _context;

        public TorneosRepositories(AppDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<Torneos>> GetALLAsync()
        {
            return await _context.Torneos
                                 .Include(t => t.Equipos) // Incluir equipos asociados
                                 .ToListAsync();
        }

        public async Task<Torneos> GetByIdAsync(int id)
        {
            return await _context.Torneos
                                 .Include(t => t.Equipos) // Incluir equipos asociados
                                 .FirstOrDefaultAsync(t => t.Id == id);
        }

        public async Task AddAsync(Torneos torneo)
        {
            await _context.Torneos.AddAsync(torneo);
            await _context.SaveChangesAsync();

            // Publicar mensaje en RabbitMQ
            using (var publisher = new MessagePublisher("localhost", "emailQueue"))
            {
                string message = $"Se ha creado un nuevo torneo: {torneo.NombreTorneo}";
                publisher.PublishMessage(message);
            }
        }


        public async Task UpdateAsync(int id, Torneos torneo)
        {
            var existingTorneo = await _context.Torneos.Include(t => t.Equipos).FirstOrDefaultAsync(t => t.Id == id);
            if (existingTorneo != null)
            {
                // Actualizar propiedades principales del torneo
                existingTorneo.NombreTorneo = torneo.NombreTorneo;
                existingTorneo.FechaInicio = torneo.FechaInicio;
                existingTorneo.FechaFin = torneo.FechaFin;

                // Los equipos existentes permanecen asociados, no se eliminan automáticamente
                _context.Entry(existingTorneo).State = EntityState.Modified;
                await _context.SaveChangesAsync();
            }
        }

        public async Task DeleteAsync(int id)
        {
            var torneo = await _context.Torneos.Include(t => t.Equipos).FirstOrDefaultAsync(t => t.Id == id);
            if (torneo != null)
            {
                _context.Torneos.Remove(torneo);
                await _context.SaveChangesAsync();
            }
        }
    }
}
