using backend_model;
using backend_tournament.Repositories.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace backend_tournament.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class JugadoresController : ControllerBase
    {
        private readonly IJugadores _jugadoresRepository;

        public JugadoresController(IJugadores jugadoresRepository)
        {
            _jugadoresRepository = jugadoresRepository;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var jugadores = await _jugadoresRepository.GetALLAsync();
            return Ok(jugadores);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var jugador = await _jugadoresRepository.GetByIdAsync(id);
            if (jugador == null) return NotFound();
            return Ok(jugador);
        }

        [HttpPost]
        public async Task<IActionResult> Add([FromBody] Jugadores jugador)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            if (jugador.EquipoId == null || jugador.EquipoId <= 0)
                return BadRequest("El jugador debe estar asociado a un equipo válido.");

            await _jugadoresRepository.AddAsync(jugador);
            return CreatedAtAction(nameof(GetById), new { id = jugador.Id }, jugador);
        }



        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] Jugadores jugador)
        {
            await _jugadoresRepository.UpdateAsync(id, jugador);
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            await _jugadoresRepository.DeleteAsync(id);
            return NoContent();
        }
    }
}
