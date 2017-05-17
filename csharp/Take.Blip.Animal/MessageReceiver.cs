using System;
using System.Threading;
using System.Threading.Tasks;
using Lime.Protocol;
using Takenet.MessagingHub.Client;
using Takenet.MessagingHub.Client.Listener;
using Takenet.MessagingHub.Client.Sender;
using System.Diagnostics;
using Takenet.MessagingHub.Client.Extensions.Bucket;

namespace Take.Blip.Animal
{
    public class MessageReceiver : IMessageReceiver
    {
        private readonly IMessagingHubSender _sender;
        private readonly IBucketExtension _bucketExtension;

        public MessageReceiver(IMessagingHubSender sender, IBucketExtension bucketExtension)
        {
            _sender = sender;
            _bucketExtension = bucketExtension;
        }

        public async Task ReceiveAsync(Message message, CancellationToken cancellationToken)
        {
            Trace.TraceInformation($"From: {message.From} \tContent: {message.Content}");
            await _sender.SendMessageAsync("Pong!", message.From, cancellationToken);
        }
    }
}
