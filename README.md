# ZooKeeper Java

Este projeto é uma implementação simples do sistema ZooKeeper em Java para a disciplina de Sistemas Distribuídos
na UFABC no quadrimestre 2023.2.

## Como compilar e empacotar o projeto

A construção dos executáveis do projeto devem ser criados utilizando o Maven.

Inicie o terminal na pasta raiz do projeto, então execute o comando abaixo pelo terminal
ou utilizando a IDE:

```bash
mvn clean package
```

Feito isso, os arquivos executáveis do tipo .jar devem ser criados na pasta `target`.
Cada arquivo corresponde a uma instância do projeto

## Como executar

- Compilar os módulos utilizando o Maven
- Executar uma instância do servidor controlador através pacote `controller.jar`
  - Deve-se informar os dados necessários de inicialização
- Executar quantas instâncias se desejar de servidores nós através do pacote `node.jar`
    - Deve-se informar os dados necessários de inicialização
- Executar quantas instâncias se desejar de clientes através do pacote `client.jar`
- Agora, podemos executar uma das operações que são
    - Get: solicita um valor aos servidores de acordo com alguma chave
    - Put: insere um valor nos servidores conforme a chave

**Observação:** para executar os pacotes utilize o comando abaixo:

```bash
java -jar ./target/<instancia>.jar
```

## Próximos passos

- [X] Executar as requisições REPLICATE de forma assíncrona
- [X] Generalizar a implementação de criação de um Socket
- [X] Sincronizar os timestamps entre todas as instâncias
- [X] Abstrair a criação da requisição entre Sockets
- [X] Notificar o líder quando um servidor auxiliar se desconectar
- [X] Revisar documentação para entrega
- [X] Gravar vídeo
- [X] Fazer relatório
