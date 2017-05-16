using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Lime.Protocol;
using Takenet.MessagingHub.Client.Listener;

namespace Take.Blip.Animal
{
    public class CatMessageReceiver : IMessageReceiver
    {
        public Task ReceiveAsync(Message envelope, CancellationToken cancellationToken = default(CancellationToken))
        {
            throw new NotImplementedException();
        }
    }
}
