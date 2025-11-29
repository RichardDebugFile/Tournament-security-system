using backend_authenticator.Repositories.Interfaces;
using backend_model;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;

namespace backend_authenticator.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class UsuariosController : ControllerBase
    {
        private readonly IUsuarios _usuariosRepository;

        public UsuariosController(IUsuarios usuariosRepository)
        {
            _usuariosRepository = usuariosRepository;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var usuarios = await _usuariosRepository.GetAllAsync();
            return Ok(usuarios);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            var usuario = await _usuariosRepository.GetByIdAsync(id);
            if (usuario == null) return NotFound();
            return Ok(usuario);
        }

        [HttpPost]
        public async Task<IActionResult> Add([FromBody] Usuarios usuario)
        {
            await _usuariosRepository.AddAsync(usuario);
            return CreatedAtAction(nameof(GetById), new { id = usuario.Id }, usuario);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, [FromBody] Usuarios usuario)
        {
            await _usuariosRepository.UpdateAsync(id, usuario);
            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            await _usuariosRepository.DeleteAsync(id);
            return NoContent();
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] Usuarios usuario)
        {
            if (usuario == null || string.IsNullOrEmpty(usuario.Gmail) || string.IsNullOrEmpty(usuario.Contrasena))
            {
                return BadRequest("Faltan datos de inicio de sesión.");
            }

            var authenticatedUsuario = await _usuariosRepository.Authenticate(usuario.Gmail, usuario.Contrasena);

            if (authenticatedUsuario == null)
            {
                return Unauthorized("Credenciales incorrectas.");
            }

            // Retornar los datos del usuario si las credenciales son correctas
            return Ok(authenticatedUsuario);
        }

    }
}
