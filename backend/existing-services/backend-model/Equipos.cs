using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace backend_model
{
    public class Equipos
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public int Id { get; set; } // Clave primaria

        [Required]
        [MaxLength(100)]
        public string NombreEquipo { get; set; } // Nombre del equipo

        // Relación con Jugadores
        public ICollection<Jugadores>? Jugadores { get; set; } = new List<Jugadores>(); // Lista opcional de jugadores

        // Relación con Torneo
        public int? TorneoId { get; set; } // Clave foránea opcional

        [ForeignKey("TorneoId")]
        [JsonIgnore] // Ignorar la propiedad Torneo para evitar ciclos
        public Torneos? Torneo { get; set; } // Torneo relacionado opcional
    }
}

