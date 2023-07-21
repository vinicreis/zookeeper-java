# UNIVERSIDADE FEDERAL DO ABC

## Vinícius de Oliveira Campos dos Reis, 11041416

## Sistemas Distribuídos

## Relatório do projeto EP 2

## Funcionalidades

A implementação de todas as funcionalidades do solicitadas no projeto utilizam 
o protocolo TCP para comunicação. O princípio de troca de mensagens entre as 
instâncias é de enviar primeiro o tipo de operação (`GET`, `PUT`, `REPLICATE`, etc)
e então o corpo da requisição no formato JSON.

### JOIN

A requisição JOIN deve partir de servidores nós com a finalidade de indicar ao
controlador que um novo servidor nó será adicionado ao sistema.

O corpo da requisição JOIN deve seguir o seguinte formato:

```json
{
  "host": "String",
  "port": "int"
}
```

Onde os campos indicam:

- `host`: endereço do nó que irá entrar no sistema
- `port`: porta do nó que irá entrar ao sistema

Ao receber a requisição, o controlador verifica se o servidor nó já existe
numa lista de nós que fazem parte do sistema (vide o método em 
`ControllerImpl:81`). Se não estiver, o servidor entra com
sucesso ao sistema.

A resposta da operação `JOIN` deve ser um JSON no seguinte formato:

```json
{
  "result": "Result",
  "message": "String?"
}
```

Contendo as seguintes informações:

- `result`: o resultado da operação, que pode assumir os valores
  - `Result.OK`: caso a operação ocorra com sucesso
  - `Result.ERROR`: caso o nó já esteja na lista do controlador ou
  ocorra algum erro na validação da mensagem 
  - `Result.EXCEPTION`: caso uma exceção não esperada seja lançada no servidor
- `message`: (opcional) uma mensagem de detalhamento em caso de falha na operação

### GET

A requisição GET será realizada entre clientes e servidores (sejam nós
ou controladores). Esta tem a finalidade de ler um valor associado a uma chave
do servidor de acordo com um timestamp. O timestamp pode ser utilizado para
controlar a consistência dos dados persistidos no servidor.

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
timestamp enviado (vide código em `Server:66`). A resposta é retornada conforme o resultado da busca.

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
  - `Result.ERROR`: algum erro de negócio ocorreu na operação que será detalhado
    no campo `message`
  - `Result.EXCEPTION`: caso alguma exceção não esperada seja lançada no servidor
  - `Result.OK`: se a chave for encontrada e o timestamp do servidor for maior do que o do
    cliente
- `message`: (opcional) mensagem em caso de erro
- `value`: (opcional) valor da chave caso seja encontrada
- `timestamp`: (opcional) timestamp do servidor caso a chave seja encontrada e o
  timestamp do servidor não esteja desatualizado

### PUT

A requisição PUT também será enviada a partir de clientes a instâncias
de servidores de qualquer tipo. Porém, se ela chegar um servidor nó, ela será
encaminhada ao servidor controlador e ele se encarregará de replicar a informação
a todos os outros servidores nós. 

A finalidade desta é adicionar dados ao servidor de acordo com determinada chave. 

Para que os clientes possam validar a consistência dos dados uma timestamp
é retornada quando um valor é adicionado aos servidores.

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

Quando uma mensagem do tipo `PUT` chega ao servidor controlador, o valor é
adicionado ao repositório (vide código em `ControllerImpl:117`) e uma requisição
do tipo `REPLICATE` (que veremos a seguir) a todos os nós cadastrados no 
controlador de forma assíncrona(vide código em `ControllerImpl:118` e
`ControllerImpl:153`). Se a replicação da informação obter sucesso 
(conforme linha `ControllerImpl:128`), o que indica que todos os
nós replicaram a informação em seus repositórios, então o controlador 
pode responder ao cliente ou ao nó que encaminhou a chamada.

A resposta do servidor será um JSON no seguinte formato:

```json
{
  "result": "Result",
  "message": "String?",
  "timestamp": "Long?"
}
```

Onde os dados da resposta indicam:

- `result`: resultado da operação, que pode assumir os valores
  - `Result.OK`: indica que a operação ocorreu com sucesso
  - `Result.ERROR`: indica que houve um erro de negócio no processamento da operação
  - `Result.EXCEPTION`: indica que uma exceção não esperada foi lançada ao
  processar a operação
- `message`: (opcional) mensagem de detalhe do resultado da operação
- `timestamp`: (opcional) em caso de sucesso, indica o timestamp do valor 
inserido no repositório

### REPLICATE

A operação de replicação tem a finalidade de replicar determinado par de
chave/valor para outras instâncias de servidores nós. Se uma requisição
PUT chega ao servidor controlador, a replicação desta informação precisa ser
realizada a todos os servidores nós.

Logo, este tipo de requisição será enviada apenas entre um servidor controlador
e servidores nós. O corpo da requisição REPLICATE deverá ser um JSON no seguinte
formato:

```json
{
  "host": "String",
  "port": "int",
  "key": "String",
  "value": "String",
  "timestamp": "Long"
}
```

Esta contém os seguintes dados:

- `host`: endereço do servidor controlador que envia a requisição
- `port`: porta do servidor controlador que envia a requisição
- `key`: chave a qual o valor será inserido
- `value`: valor que será inserido ao repositório
- `timestamp`: timestamp em que o valor foi adicionado

Quando a requisição é recebida por um servidor nó, as informações são replicadas
em seu repositório para futuras consultas (conforme código `NodeImpl:141`),
se o servidor que enviou a informação for o controlador válido a qual o
servidor nó realizou o JOIN anteriormente.

A resposta devolvida pelo servidor nó deve ser um JSON com o seguinte formato:

```json
{
  "result": "Result",
  "message": "String?"
}
```

Onde a resposta contém os seguintes dados:

- `result`: o resultado a operação, podendo assumir os seguintes valores
  - `Result.OK`: indica que a operação ocorreu com sucesso
  - `Result.ERROR`: indica que houve um erro na validação dos dados
  - `Result.EXCEPTION`: indica que houve uma exceção não esperada no servidor
- `message`: (opcional) mensagem de detalhamento do erro na operação

### EXIT

A requisição do tipo `EXIT` é semelhante à requisição `JOIN`, porém, ela indica 
a saída de um nó do sistema. Portanto, este tipo de requisição ocorrerá apenas
de servidores nós para controladores.

O corpo da requisição `EXIT` deve seguir o seguinte formato:

```json
{
  "host": "String",
  "port": "int"
}
```

Onde os campos indicam:

- `host`: endereço do nó que irá sair no sistema
- `port`: porta do nó que irá sair ao sistema

Ao receber a requisição, o controlador verifica se o servidor nó já existe
numa lista de nós que fazem parte do sistema (conforme código em `ControllerImpl:210`). 
Se não tiver, o controlador remove o nó de sua lista de nós do sistema.

A resposta da operação `EXIT` deve ser um JSON no seguinte formato:

```json
{
  "result": "Result",
  "message": "String?"
}
```

Contendo as seguintes informações:

- `result`: o resultado da operação, que pode assumir os valores
  - `Result.OK`: caso a operação ocorra com sucesso
  - `Result.ERROR`: caso o nó já não esteja na lista do controlador ou
    ocorra algum erro na validação da mensagem
  - `Result.EXCEPTION`: caso uma exceção não esperada seja lançada no servidor
- `message`: (opcional) uma mensagem de detalhamento em caso de falha na operação

## Operações assíncronas

### Recebimento e processamento de requisições por servidores

Quando um servidor inicia, seja ele controlador ou nó, ele inicia uma Thread
que tem a finalidade de iniciar um socket de servidor que recebe as
requisições tanto dos clientes quanto de outros servidores 
(`server.thread.DispatcherThread:38`).

Quando uma conexão é aberta, a operação é lida (`server.thread.DispatcherThread:42`)
e então o conteúdo da mensagem (`server.thread.DispatcherThread:39`). Com isso,
uma nova `WorkerThread` é criada e iniciada para execução da operação enviada ao servidor
(`server.thread.DispatcherThread:46` - vide descrição da [Execução de operações em servidores controladores](#execução-de-operações-em-servidores-controladores)).

A execução desta thread se dá enquanto o servidor estiver executando, ele pode 
ser interrompido a qualquer momento pela implementação do controlador 
(`server.thread.DispatcherThread:58`).

### Execução de operações em servidores

Quando uma operação é despachada pela `DispatcherThread` uma `WorkerThread` é
iniciada para execução desta operação. Esta instância recebe o `Socket` aberto
para comunicação com o cliente ou servidor, a operação e o corpo da requisição 
enviado.

Conforme a operação enviada, esta thread sabe realizar o parse da 
requisição e também a chamada do método correspondente a operação de acordo
na instância de `Server` (`server.thread.WorkerThread:39`).

Quando a operação termina e uma resposta é retornada pela instância de `Server`,
esta resposta é enviada no `Socket` (`server.thread.WorkerThread:67`) e o mesmo
é fechado (`server.thread.WorkerThread:68`).

### Envio de requisições de replicação pelo servidor controlador

### Execução de operações nos clientes

### Contagem do timestamp
