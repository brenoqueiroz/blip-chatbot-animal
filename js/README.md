# [BLiP](https://blip.ai) Chatbot Animal

**Você gostou desse projeto? Conheça mais em [BLiP](https://blip.ai)
- [Documentação](https://portal.blip.ai/#/docs)
- [Blog](http://blog.blip.ai/)
- [Forum](forum.blip.ai)

----------
## O Chatbot

Vamos criar um Chatbot bem simples, o intuito desse projeto é entender melhor a plataforma BLiP.ai. O chatbot vai responde Pong! para qualquer mensagem enviada exceto para a mensagem 'Animal', neste caso o bot vai enviar uma menu para escolha entre um cachorro ou um gato, apos escolher nosso chatbot vai se transformar nesse animal, vamos precisar criar uma máquina de estados para controlar o fluxo de interação do usuário.

----------

## Mãos à obra

### Passo 1 - Connectar ao BLiP

Antes de mais nada, precisamos criar um novo contato (chatbot) na plataforma [blip.ai](https://blip.ai)].

- Acesse a plataforma, faça login e clique no botão Criar Contato
- Escolha o modelo para desenvolvedores SDK
- Preencha as informações básicas de seu chatbot (nome e foto)

Com isso temos acesso ao Identifier e a AccessKey.

```javascript
let client = new MessagingHub.ClientBuilder()
    .withIdentifier('{Identifier}')
    .withAccessKey('{AccessKey}')
    .withTransportFactory(() => new WebSocketTransport())
    .build();

client.connect()
    .then(() => {
        console.log('BOT CONNECTADO!');
    });
```

### Passo 2 - Respondendo Pong!

Para isso vamos criar um MessageReceiver que sempre será executado respondendo Pong!

```javascript
client.addMessageReceiver((m) => true, (m) => {
        let message = {
        id: Lime.Guid(),
        type: 'text/plain',
        content: 'Pong!!!',
        to: m.from
    }

    client.sendMessage(message);
});
```

### Passo 3 - Processar a palavra Animal

Conforme definição do chatbot, quando recebermos a mensagens animal devemos direcionar o usuário a escolher entre um cachorro ou um gato.

Vamos validar o conteúdo da mensagem, caso seja animal esse receiver vai processar a mensagem enviado um [Menu (application/vnd.lime.select+json)](https://portal.blip.ai/#/docs/content-types/select) com as opções Cachorro e Gato. 

```javascript
if (m.type == 'text/plain' && m.content.toLowerCase().trim() == 'animal') {
    message = {
        "id": Lime.Guid(),
        "to": m.from,
        "type": "application/vnd.lime.select+json",
        "content": {
            "text": "Escolha uma opção",
            "options": [
                {
                    "order": 1,
                    "text": "Cachorro"
                },
                {
                    "order": 2,
                    "text": "Gato"
                }
            ]
        }
    };
}
```

### Passo 4 - Controlar o estado do usuário

Precisamos entender que o usuário nesse momento esta em um estado de escolha do Animal, a proxima resposta dele para esse estado deve ser 'Cachorro' ou 'Gato', qualquer valor diferente disso para esse estado esta incorreto, então vamos gravar na [extensão armazenamento](https://portal.blip.ai/#/docs/extensions/bucket) o atual estado do usuário.

```javascript
 if (m.type == 'text/plain' && m.content.toLowerCase().trim() == 'animal') {
    message = {
        "id": Lime.Guid(),
        "to": m.from,
        "type": "application/vnd.lime.select+json",
        "content": {
            "text": "Escolha uma opção",
            "options": [
                {
                    "order": 1,
                    "text": "Cachorro"
                },
                {
                    "order": 2,
                    "text": "Gato"
                }
            ]
        }
    };

    let command = {
        "id": Lime.Guid(),
        "method": "set",
        "uri": "/buckets/" + m.from.split('/')[0],
        "type": "application/json",
        "resource": {
            "sessionState": "EscolherAnimal"
        }
    };

    client.sendCommand(command);
}
```

### Passo 5 - Validar escolha

Agora precisamos validar que o usuário escolheu entre um Cachorro ou Gato caso ele esteja no estado 'EscolherAnimal', para isso vamos recuperar na [extensão armazenamento](https://portal.blip.ai/#/docs/extensions/bucket) o atual estado do usuário, caso seja um cachorro ou gato define um novo estado (cachorro ou gato) e envia uma imagem do item selecionado, caso seja uma opção invalida, envia o menu novamente.

```javascript
let command = {
    "id": Lime.Guid(),
    "method": "get",
    "uri": "/buckets/" + encodeURIComponent(m.from.split('/')[0])
};

client.sendCommand(command)
    .then(userSession => {
        //Obtem estado atual
        switch (userSession.resource.sessionState) {
            case 'EscolherAnimal':
                //A escolha deve ser entre cachorro ou gato
                if (m.type == 'text/plain'
                    && (m.content.toLowerCase().trim() == 'cachorro'
                        || m.content.toLowerCase().trim() == 'gato')) {

                    let command = {
                        "id": Lime.Guid(),
                        "method": "set",
                        "uri": "/buckets/" + encodeURIComponent(m.from.split('/')[0]),
                        "type": "application/json",
                        "resource": {
                            "sessionState": m.content.toLowerCase().trim()
                        }
                    };

                    client.sendCommand(command);

                    if (m.content.toLowerCase().trim() == 'cachorro') {
                        message = {
                            "id": Lime.Guid(),
                            "to": m.from,
                            "type": "application/vnd.lime.media-link+json",
                            "content": {
                                "title": "Cachorro",
                                "text": "Agora sou um cachorro",
                                "type": "image/jpeg",
                                "uri": "http://tudosobrecachorros.com.br/wp-content/uploads/cachorro-independente-766x483.jpg"
                            }
                        };

                    }
                    else {
                        message = {
                            "id": Lime.Guid(),
                            "to": m.from,
                            "type": "application/vnd.lime.media-link+json",
                            "content": {
                                "title": "Gato",
                                "text": "Agora sou um gato",
                                "type": "image/jpeg",
                                "uri": "http://www.gatosmania.com/Uploads/gatosmania.com/ImagensGrandes/linguagem-corporal-gatos.jpg"
                            }
                        };
                    }
                }
                else {
                    message = {
                        "id": Lime.Guid(),
                        "to": m.from,
                        "type": "application/vnd.lime.select+json",
                        "content": {
                            "text": "Escolha uma opção",
                            "options": [
                                {
                                    "order": 1,
                                    "text": "Cachorro"
                                },
                                {
                                    "order": 2,
                                    "text": "Gato"
                                }
                            ]
                        }
                    };
                }
                break;
        }
    });
```

### Passo 6 - Se comportar como um Cachorro ou Gato

Agora que nosso usuário escolheu entre um cachorro ou gato precisamos fazer nosso chatbot se comportar como um. No passo anterior gravamos no estado o valor que foi selecionado então basta validar esse estado e direcionar a mensagem, vamos adicionar mais 2 estados no nosso SWITCH.

```javascript
switch (userSession.resource.sessionState) {
    case 'cachorro':
        message = {
            id: Lime.Guid(),
            type: 'text/plain',
            content: 'Au Au Au!!!',
            to: m.from
        };

        break;

    case 'gato':
        message = {
            id: Lime.Guid(),
            type: 'text/plain',
            content: 'Miau Miau Miau!!!',
            to: m.from
        };
        break;
}
```

### Passo 7 - Voltar ao inicio

Quando trabalhamos com estados, temos que tomar cuidado para o usuário não ficar preso em um fluxo, no nosso caso transformamos nosso chatbot em um animal que responde sempre 'Au Au Au!!!' ou 'Miau Miau Miau!!!', agora precisamos criar uma forma do usuário voltar ao inicio para selecionar o animal novamente. Em nosso chatbot a palavra Ping vai fazer isso, sempre que receber um ping vamos deletar o estado do usuário. 

```javascript
if (m.type == 'text/plain' && m.content.toLowerCase().trim() == 'ping') {
    let command = {
        "id": Lime.Guid(),
        "method": "delete",
        "uri": "/buckets/" + encodeURIComponent(m.from.split('/')[0])
    };

    client.sendCommand(command);

    let message = {
        id: Lime.Guid(),
        type: 'text/plain',
        content: 'Pong!!!',
        to: m.from
    };

    client.sendMessage(message);
}
```