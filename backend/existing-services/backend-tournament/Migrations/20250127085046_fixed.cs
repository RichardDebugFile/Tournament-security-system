using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace backend_tournament.Migrations
{
    /// <inheritdoc />
    public partial class @fixed : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Equipos_Torneos_TorneoId",
                table: "Equipos");

            migrationBuilder.DropForeignKey(
                name: "FK_Jugadores_Equipos_EquipoId",
                table: "Jugadores");

            migrationBuilder.AddColumn<int>(
                name: "EquiposId",
                table: "Jugadores",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Jugadores_EquiposId",
                table: "Jugadores",
                column: "EquiposId");

            migrationBuilder.AddForeignKey(
                name: "FK_Equipos_Torneos_TorneoId",
                table: "Equipos",
                column: "TorneoId",
                principalTable: "Torneos",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);

            migrationBuilder.AddForeignKey(
                name: "FK_Jugadores_Equipos_EquipoId",
                table: "Jugadores",
                column: "EquipoId",
                principalTable: "Equipos",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Jugadores_Equipos_EquiposId",
                table: "Jugadores",
                column: "EquiposId",
                principalTable: "Equipos",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Equipos_Torneos_TorneoId",
                table: "Equipos");

            migrationBuilder.DropForeignKey(
                name: "FK_Jugadores_Equipos_EquipoId",
                table: "Jugadores");

            migrationBuilder.DropForeignKey(
                name: "FK_Jugadores_Equipos_EquiposId",
                table: "Jugadores");

            migrationBuilder.DropIndex(
                name: "IX_Jugadores_EquiposId",
                table: "Jugadores");

            migrationBuilder.DropColumn(
                name: "EquiposId",
                table: "Jugadores");

            migrationBuilder.AddForeignKey(
                name: "FK_Equipos_Torneos_TorneoId",
                table: "Equipos",
                column: "TorneoId",
                principalTable: "Torneos",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Jugadores_Equipos_EquipoId",
                table: "Jugadores",
                column: "EquipoId",
                principalTable: "Equipos",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
