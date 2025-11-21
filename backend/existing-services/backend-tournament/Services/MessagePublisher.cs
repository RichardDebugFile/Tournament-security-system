using RabbitMQ.Client;
using System;
using System.Text;

// Crear un alias para evitar conflictos con Entity Framework
using RabbitMqModel = RabbitMQ.Client.IModel;

namespace backend_tournament.Services
{
    public class MessagePublisher : IDisposable
    {
        private readonly string _queueName;
        private readonly IConnection _connection;
        private readonly RabbitMqModel _channel;

        public MessagePublisher(string hostName, string queueName)
        {
            _queueName = queueName;
            var factory = new ConnectionFactory() { HostName = hostName };
            _connection = factory.CreateConnection();
            _channel = _connection.CreateModel();

            // Declarar la cola
            _channel.QueueDeclare(queue: _queueName,
                                  durable: true,
                                  exclusive: false,
                                  autoDelete: false,
                                  arguments: null);
        }

        public void PublishMessage(string message)
        {
            var body = Encoding.UTF8.GetBytes(message);

            _channel.BasicPublish(exchange: "",
                                  routingKey: _queueName,
                                  basicProperties: null,
                                  body: body);

            Console.WriteLine($"Mensaje publicado: {message}");
        }

        public void Dispose()
        {
            _channel?.Close();
            _connection?.Close();
        }
    }
}
