using Notifications.Services;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System;
using System.Net.Mail;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Notifications
{
    public class Consumer
    {
        private readonly NotificationService _notificationService;

        public Consumer(NotificationService notificationService)
        {
            _notificationService = notificationService;
        }

        public void Start(string rabbitMqHost, string queueName)
        {
            var factory = new ConnectionFactory() { HostName = rabbitMqHost };

            using (var connection = factory.CreateConnection())
            using (var channel = connection.CreateModel())
            {
                // Declarar la cola
                channel.QueueDeclare(queue: queueName,
                                     durable: true,
                                     exclusive: false,
                                     autoDelete: false,
                                     arguments: null);

                Console.WriteLine($"Esperando mensajes en la cola '{queueName}'...");

                var consumer = new EventingBasicConsumer(channel);

                consumer.Received += async (model, ea) =>
                {
                    // Leer el mensaje recibido
                    var body = ea.Body.ToArray();
                    var message = Encoding.UTF8.GetString(body);

                    Console.WriteLine($"Mensaje recibido: {message}");

                    // Enviar correos con el mensaje como cuerpo
                    string asunto = "Notificación de Torneos";
                    await EnviarCorreosAsync(asunto, message);
                };

                // Iniciar el consumo de la cola
                channel.BasicConsume(queue: queueName,
                                     autoAck: true,
                                     consumer: consumer);

                Console.WriteLine("Presiona [Enter] para salir.");
                Console.ReadLine();
            }
        }

        private async Task EnviarCorreosAsync(string asunto, string cuerpo)
        {
            string remitente = "jossfarfan80@gmail.com";
            string contrasena = "ankq hjgx arpb jimp"; // Reemplaza esto con tus credenciales reales

            try
            {
                // Obtener los usuarios a los que se enviarán los correos
                var destinatarios = await _notificationService.ObtenerUsuariosAsync();

                // Configurar el cliente SMTP
                var smtpClient = new SmtpClient("smtp.gmail.com")
                {
                    Port = 587,
                    Credentials = new NetworkCredential(remitente, contrasena),
                    EnableSsl = true,
                };

                // Enviar un correo a cada destinatario
                foreach (var destinatario in destinatarios)
                {
                    var mailMessage = new MailMessage
                    {
                        From = new MailAddress(remitente),
                        Subject = asunto,
                        Body = cuerpo,
                        IsBodyHtml = false,
                    };

                    mailMessage.To.Add(destinatario.Gmail);

                    await smtpClient.SendMailAsync(mailMessage);

                    Console.WriteLine($"Correo enviado exitosamente a {destinatario.Gmail}.");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al enviar correo: {ex.Message}");
            }
        }
    }
}
