using backend_model;
using RestSharp;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Notifications.Services
{
    public class NotificationService
    {
        private readonly RestClient _client;
        private readonly string _apiUrl;

        public NotificationService(string apiUrl)
        {
            _apiUrl = apiUrl;
            _client = new RestClient(_apiUrl);
        }

        public async Task<List<Usuarios>> ObtenerUsuariosAsync()
        {
            try
            {
                var request = new RestRequest("Usuarios", Method.Get);
                var response = await _client.ExecuteAsync<List<Usuarios>>(request);

                if (response.IsSuccessful)
                {
                    return response.Data ?? new List<Usuarios>();
                }
                else
                {
                    Console.WriteLine($"Error al obtener usuarios: {response.ErrorMessage}");
                    return new List<Usuarios>();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al obtener usuarios: {ex.Message}");
                return new List<Usuarios>();
            }
        }
    }
}