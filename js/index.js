"use strict";

let MessagingHub = require('messaginghub-client');
let WebSocketTransport = require('lime-transport-websocket');
let Lime = require('lime-js');

let client = new MessagingHub.ClientBuilder()
    .withIdentifier('{identifier}')
    .withAccessKey('{accessKey}')
    .withTransportFactory(() => new WebSocketTransport())
    .build();

client.connect()
    .then(() => {

        console.log('BOT CONNECTADO!');

        //Toda mensagem que chegar vai ser logada no console
        client.addMessageReceiver((m) => true, (m) => {
            console.log(m);
            //Passa a execução para o proximo receiver.
            return true;
        });

        //Recebe todas as mensagens
        client.addMessageReceiver((m) => true, (m) => {
            let command = {
                "id": Lime.Guid(),
                "method": "get",
                "uri": "/buckets/" + encodeURIComponent(m.from.split('/')[0])
            };

            client.sendCommand(command)
                .then(userSession => {
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
                    else {
                        let message = null;

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

                            case 'EscolherAnimal':
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
                        };

                        client.sendMessage(message);
                    }
                })
                .catch((err) => {
                    let message = {};

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
                            "uri": "/buckets/" + encodeURIComponent(m.from.split('/')[0]),
                            "type": "application/json",
                            "resource": {
                                "sessionState": "EscolherAnimal"
                            }
                        };

                        //Grava a sessão do usuário no servidor
                        client.sendCommand(command);
                    }
                    else {
                        message = {
                            id: Lime.Guid(),
                            type: 'text/plain',
                            content: 'Pong!!!',
                            to: m.from
                        };
                    }

                    client.sendMessage(message);
                });
        });
    })
    .catch((err) => console.error(err));;
