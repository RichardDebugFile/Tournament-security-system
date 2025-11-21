using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace backend_model
{
    public class Usuarios
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; } // Clave primaria, generada automáticamente.

        [Required]
        [MaxLength(100)]
        [EmailAddress]
        public string Gmail { get; set; } // Correo electrónico del usuario.

        [Required]
        [MaxLength(100)]
        public string Contrasena { get; set; } // Contraseña del usuario.
    }
}

