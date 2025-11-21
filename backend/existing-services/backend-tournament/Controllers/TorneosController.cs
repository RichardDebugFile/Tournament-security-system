using backend_model;
using backend_tournament.Repositories.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace backend_tournament.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class TorneosController : ControllerBase
    {
        private readonly ITorneos _torneosRepository;

        public TorneosController(ITorneos torneosRepository)
        {
            _torneosRepository = torneosRepository;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var torneos = await _torneosRepository.GetALLAsync();
            return Ok(torneos);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var torneo = await _torneosRepository.GetByIdAsync(id);
            if (torneo == null) return NotFound();
            return Ok(torneo);
        }

        [HttpPost]
        public async Task<IActionResult> Add([FromBody] Torneos torneo)
        {
            await _torneosRepository.AddAsync(torneo);
            return CreatedAtAction(nameof(GetById), new { id = torneo.Id }, torneo);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] Torneos torneo)
        {
            await _torneosRepository.UpdateAsync(id, torneo);
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            await _torneosRepository.DeleteAsync(id);
            return NoContent();
        }
    }
}
