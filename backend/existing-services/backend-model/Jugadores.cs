using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace backend_model
{
    public class Jugadores
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; } // Clave primaria

        [Required]
        [MaxLength(100)]
        public string Nombre { get; set; }

        [Required]
        [MaxLength(100)]
        public string Apellido { get; set; }

        [Required]
        [MaxLength(10)]
        public string Cedula { get; set; }

        [Required]
        public int NumCamiseta { get; set; }

        [Required]
        [MaxLength(10)]
        public string Lateralidad { get; set; }

        public int? EquipoId { get; set; } // Clave foránea opcional

        [ForeignKey("EquipoId")]
        [JsonIgnore] // ✅ Evita que el backend espere `Equipo` en el JSON de entrada
        public Equipos? Equipo { get; set; }


    }
}
