# Zookepeer Java

Este projeto é uma implementação simples do sistema Zookepeer em Java para a disciplina de Sistemas Distribuídos
na UFABC no quadrimestre 2023.2.

## Como utilizar

- Compilar os arquivos do projeto utilizando Java 8
- Executar uma instância do servidor líder na classe `server.Controller`
  - Deve-se informar os dados necessários de inicialização
- Executar quantas instâncias se desejar de servidores auxiliares na classe `server.Node`
    - Deve-se informar os dados necessários de inicialização
- Executar quantas instâncias se desejar de clientes na classe `client.Client`
- Agora, podemos executar uma das operações que são
    - Get: solicita um valor aos servidores de acordo com alguma chave
    - Put: insere um valor nos servidores conforme a chave

## Como compilar e executar utilizando `javac`

A fim de não haver dependência nas IDEs para execução do projeto, segue um passo a passo 
de como compilar e executar o projeto diretamente pelo Terminal:

- Abrir o terminal na pasta `src`
- Executar os comandos
    - Para executar o servidor líder
  ```bash
  javac -cp . server/Controller.java
  java -cp . server.Controller
  ```
    - Para executar os servidores auxiliares
  ```bash
  javac -cp . server/Node.java
  java -cp . server.Node
  ```
    - Para executar os clientes
  ```bash
  javac -cp . client/Client.java
  java -cp . client.Client
  ```

## Próximos passos

- [ ] Executar as requisições REPLICATE de forma assíncrona
- [ ] Generalizar a implementação de criação de um Socket
- [ ] Sincronizar os timestamps entre todas as instâncias
- [ ] Revisar documentação para entrega
- [ ] Gravar vídeo
- [ ] Fazer relatório
