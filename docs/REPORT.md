# UNIVERSIDADE FEDERAL DO ABC

## Vinícius de Oliveira Campos dos Reis, 11041416

## Sistemas Distribuídos

## Relatório do projeto EP 2

## Funcionalidades

A implementação de todas as funcionalidades do solicitadas no projeto utilizam 
o protocolo TCP para comunicação. O princípio de troca de mensagens entre as 
instâncias é de enviar primeiro o tipo de operação (`GET`, `PUT`, `REPLICATE`, etc)
e então o corpo da requisição no formato JSON.

### Servidor

#### GET

Quando uma requisição GET é enviada ao servidor, um JSON no seguinte formato precisa 
ser enviado:

```json
{
  "host": "String",
  "port": "int",
  "key": "String",
  "timestamp": "Long?"
}
```

Os campos devem estar preenchidos com os seguintes dados:

- `host`: o endereço do cliente que enviou a requisição
- `port`: a porta do cliente que enviou a requisição
- `key`: a key da qual o cliente que acessar o valor
- `timestamp`: (opcional) a última timestamp associada a key. Se o cliente não tiver 
timestamp associada a key, pode-se enviar o valor `null` ou `0`

Ao servidor receber a requisição uma busca é realizada no repositório pela key e pelo
timestamp enviado. A resposta é retornada conforme o resultado da busca.

A resposta também será um JSON no formato a seguir:

```json
{
  "result": "Result",
  "message": "String?",
  "value": "String?",
  "timestamp": "Long?"
}
```

Onde os campos são:
- `result`: resultado da operação, que podem ser:
  - `Result.NOT_FOUND`: a chave pode não ser encontrada no repositório
  - `Result.TRY_OTHER`: o timestamp enviado é maior que zero e maior que o
  timestamp do repositório para determinada chave
  - `Result.EXCEPTION`: caso alguma exceção não esperada seja lançada no servidor
  - `Result.OK`: se a chave for encontrada e o timestamp do servidor for maior do que o do
  cliente
- `message`: (opcional) mensagem em caso de erro
- `value`: (opcional) valor da chave caso seja encontrada
- `timestamp`: (opcional) timestamp do servidor caso a chave seja encontrada e o 
timestamp do servidor não esteja desatualizado

## Servidor - Controller

### PUT

As requisições do tipo PUT podem chegar de instâncias de servidores nós quanto 
diretamente dos clientes. As requisições devem ser um JSON no formato:

```json
{
  "host": "String",
  "port": "int",
  "key": "String",
  "value": "String"
}
```

Os dados da requisição serão preenchidos com as seguintes informações:

- `host`: endereço da instância que enviou a requisição
- `port`: porta da instância que enviou a requisição
- `key`: key a qual um valor será inserido
- `value`: valor que será associado à key

