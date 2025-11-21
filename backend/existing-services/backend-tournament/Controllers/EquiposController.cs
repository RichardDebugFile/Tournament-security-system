using backend_model;
using backend_tournament.Repositories.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace backend_tournament.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class EquiposController : ControllerBase
    {
        private readonly IEquipos _equiposRepository;

        public EquiposController(IEquipos equiposRepository)
        {
            _equiposRepository = equiposRepository;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var equipos = await _equiposRepository.GetALLAsync();
            return Ok(equipos);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var equipo = await _equiposRepository.GetByIdAsync(id);
            if (equipo == null) return NotFound();
            return Ok(equipo);
        }

        [HttpPost]
        public async Task<IActionResult> Add([FromBody] Equipos equipo)
        {
            // Asegurarte de que el equipo tenga un TorneoId válido
            if (equipo.TorneoId == null)
            {
                return BadRequest("El equipo debe estar asociado a un torneo válido.");
            }

            await _equiposRepository.AddAsync(equipo);
            return CreatedAtAction(nameof(GetById), new { id = equipo.Id }, equipo);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] Equipos equipo)
        {
            // Asegurarte de que el equipo tenga un TorneoId válido
            if (equipo.TorneoId == null)
            {
                return BadRequest("El equipo debe estar asociado a un torneo válido.");
            }

            await _equiposRepository.UpdateAsync(id, equipo);
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            await _equiposRepository.DeleteAsync(id);
            return NoContent();
        }
    }
}
