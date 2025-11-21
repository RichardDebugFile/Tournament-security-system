using System;
using System.Threading.Tasks;
using Notifications.Services;
using Notifications;

namespace Notifications
{
    class Program
    {
        static async Task Main(string[] args)
        {
            string apiUrl = "http://localhost:5070/api/";
            string rabbitMqHost = "localhost";
            string queueName = "emailQueue";

            var notificationService = new NotificationService(apiUrl);
            var consumer = new Consumer(notificationService);

            // Iniciar el consumidor en un hilo separado
            Task.Run(() => consumer.Start(rabbitMqHost, queueName));

            Console.WriteLine("Iniciando el productor...");
            using (var producer = new Producer(rabbitMqHost, queueName))
            {
                Console.WriteLine("Escribe un mensaje y presiona [Enter] para enviarlo. Escribe 'exit' para salir.");

                while (true)
                {
                    Console.Write("Mensaje: ");
                    string message = Console.ReadLine();

                    // Verificar si el mensaje es nulo
                    if (string.IsNullOrEmpty(message))
                    {
                        Console.WriteLine("Mensaje vacío, por favor ingresa un mensaje válido.");
                        continue;
                    }

                    // Salir del bucle si el usuario escribe "exit"
                    if (message.Equals("exit", StringComparison.OrdinalIgnoreCase))
                    {
                        Console.WriteLine("Saliendo...");
                        break;
                    }

                    // Enviar el mensaje
                    producer.EnviarMensaje(message);
                }
            }
        }
    }
}
