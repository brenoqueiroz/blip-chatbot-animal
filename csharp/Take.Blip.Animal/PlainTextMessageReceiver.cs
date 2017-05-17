using System;
using System.Threading;
using System.Threading.Tasks;
using Lime.Protocol;
using Takenet.MessagingHub.Client.Listener;
using Takenet.MessagingHub.Client.Sender;
using Takenet.MessagingHub.Client.Extensions.Bucket;
using System.Linq;
using Lime.Messaging.Contents;

namespace Take.Blip.Animal
{
    public class PlainTextMessageReceiver : IMessageReceiver
    {
        private readonly IMessagingHubSender _sender;
        private readonly IBucketExtension _bucketExtension;

        public PlainTextMessageReceiver(IMessagingHubSender sender, IBucketExtension bucketExtension)
        {
            _sender = sender;
            _bucketExtension = bucketExtension;
        }

        public async Task ReceiveAsync(Message message, CancellationToken cancellationToken)
        {
            var fromIdentity = message.From.ToIdentity();

            // Gets the stored document for the identity
            var userSession = await _bucketExtension.GetAsync<JsonDocument>(
                fromIdentity,
                cancellationToken);

            var text = message.Content.ToString().ToLowerInvariant();

            // Creates the response
            var responseMessage = new Message()
            {
                Id = EnvelopeId.NewId(),
                To = message.From,
                Content = "Pong!!!"
            };

            // If there is a stored session
            if (userSession != null)
            {
                if (text == "ping")
                {
                    // Removes the session
                    await _bucketExtension.DeleteAsync(fromIdentity, cancellationToken);
                }
                else
                {
                    var state = userSession["state"].ToString();
                    switch (state)
                    {
                        case "cachorro":
                            responseMessage.Content = "Au Au Au!!!";
                            break;

                        case "gato":
                            responseMessage.Content = "Miau Miau Miau!!!";
                            break;

                        case "EscolherAnimal":
                            if (new[] { "cachorro", "gato" }.Contains(text))
                            {
                                // Sets the current state as the animal name
                                await _bucketExtension.SetAsync(
                                    fromIdentity,
                                    new JsonDocument
                                    {
                                        { "state", text }
                                    },
                                    cancellationToken: cancellationToken);

                                if (text == "cachorro")
                                {
                                    responseMessage.Content = 
                                        new MediaLink
                                        {
                                            Title = "Cachorro",
                                            Text = "Agora sou um cachorro",
                                            Type = "image/jpeg",
                                            Uri = new Uri("http://tudosobrecachorros.com.br/wp-content/uploads/cachorro-independente-766x483.jpg"),
                                        };
                                }
                                else if (text == "gato")
                                {
                                    responseMessage.Content =
                                        new MediaLink
                                        {
                                            Title = "Gato",
                                            Text = "Agora sou um gato",
                                            Type = "image/jpeg",
                                            Uri = new Uri("http://www.gatosmania.com/Uploads/gatosmania.com/ImagensGrandes/linguagem-corporal-gatos.jpg"),
                                        };
                                }
                            }

                            break;
                    }

                }
            }
            else if (text == "animal")
            {
                await _bucketExtension.SetAsync(
                    fromIdentity,
                    new JsonDocument
                    {
                        { "state", "EscolherAnimal" }
                    },
                    cancellationToken: cancellationToken);

                responseMessage.Content =
                    new Select
                    {
                        Text = "Escolha uma opção",
                        Options = new[]
                        {
                            new SelectOption { Order = 1, Text = "Cachorro"},
                            new SelectOption { Order = 2, Text = "Gato"}
                        }
                    };
            }

            await _sender.SendMessageAsync(responseMessage, cancellationToken);
        }
    }
}
