using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace backend_model
{
    public class Torneos
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string NombreTorneo { get; set; }

        [Required]
        public DateTime FechaInicio { get; set; }

        [Required]
        public DateTime FechaFin { get; set; }

        // Relación con Equipos
        [JsonIgnore] // Evitar ciclos de referencia
        public ICollection<Equipos>? Equipos { get; set; } = new List<Equipos>(); // Hacerlo opcional
    }
}
