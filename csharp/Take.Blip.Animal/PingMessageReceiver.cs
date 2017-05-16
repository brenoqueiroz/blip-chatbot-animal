using System.Threading;
using System.Threading.Tasks;
using Lime.Protocol;
using Takenet.MessagingHub.Client.Listener;
using Takenet.MessagingHub.Client.Sender;
using Takenet.MessagingHub.Client;

namespace Take.Blip.Animal
{
    public class PingMessageReceiver : IMessageReceiver
    {
        private readonly IMessagingHubSender _sender;

        public PingMessageReceiver(IMessagingHubSender sender)
        {
            _sender = sender;
        }

        public async Task ReceiveAsync(Message message, CancellationToken cancellationToken = default(CancellationToken))
        {
            await _sender.SendMessageAsync("Pong!", message.From, cancellationToken);
        }
    }
}
