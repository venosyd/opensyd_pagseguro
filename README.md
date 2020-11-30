# Opensyd Pagseguro

Backend de integracao para PagSeguro para ser usado com o Opensyd Backend. Este serviço permite múltiplas contas sejam configuradas

## CONFIGURAR UMA CONTA
---
1. Se for configurar várias contas, crie um arquivo ```pagseguro-<nome_da_conta>.yaml``` na pasta ```<raiz_backend>/assets/config```. Se for apenas uma única conta, então apenas copie o ```pagseguro.yaml``` sem alterar o nome e edite as informações necessárias nele.

2. Use o template que está aqui neste repositório em ```assets/config/pagseguro.yaml```.

3. Adicione as páginas em ```assets/pages``` para a raiz do seu servidor web.

## REST API

### 1. CRIAR SESSÃO
---
```
URL: 
[POST] /pagseguro/session

Body:
{
    "conta": (opcional) requerido apenas em caso de terem múltiplas contas configuradas
}

Headers:
{
    "Authorization": "Basic <token-de-login-em-base64>"
}
```
O usuário devidamente logado pode solicitar que  
uma sessão seja criada. A partir daí as operações  
podem ser feitas. Retorna uma ```sessionID```.

### 2. OBTER O SENDER HASH
---

Envie o ```sessionID``` obtido acima para a página ```https://<seu-host>/pagseguro.html?sessionID=<sessionID>```. A página irá gerar um código que precisa ser copiado, este será o ```senderHash```. Pode ser feito via requisição GET.

### 3a. FAZER CHECKOUT
---
```
[POST] /pagseguro/do-checkout

Body:
{
      "conta": (opcional) requerido apenas em caso de terem múltiplas contas configuradas
      "sessionID": id de sessão,
      "itemDescricao": descrição do item configurado no PagSeguro,
      "itemSigla": sigla do item configurada no PagSeguro,
      "clienteNome": nome do cliente,
      "clienteCPF": cpf do cliente (apenas números),
      "clienteDDD": ddd do número de telefone,
      "clientePhone": número de telefone,
      "clienteEmail": email do cliente,
      "clienteHash": hash gerado pelo senderHash,
      "amount": quantidade em valor (double),
      "ccNumero": número do cartão de crédito,
      "ccCVV": código de validação,
      "ccMesExpiracao": mes de expiração,
      "ccAnoExpiracao": ano de expiração,
      "ccDiaNascimento": dia de nascimento,
      "parcelas": número de parcelas,
}

Headers:
{
    "Authorization": "Basic <token-de-login-em-base64>"
}
```
Usuário devidamente logado, com ID de ```sessionID``` e ```senderHash``` em mãos, pode user este endpoint para realizar o registro de compra. O retorno é uma mensagem de sucesso com o ID da transação ou a descrição da falha.

### 4a. VERIFICAR CHECKOUT
---
```
[POST] /pagseguro/see-checkout

Body:
{
    "conta": (opcional) requerido apenas em caso de terem múltiplas contas configuradas
    "transactionID": ID da transação
}

Headers:
{
    "Authorization": "Basic <token-de-login-em-base64>"
}
```
Usuário devidamente logado pode passar o ID de transação para ter informações

### 3b. ASSINAR PLANO
---
```
[POST] /pagseguro/do-subscription

Body: 
{
    "conta": (opcional) requerido apenas em caso de terem múltiplas contas configuradas
    "sessionID": ID da sessão,
    "planoID": ID do plano configurado no PagSeguro,
    "planoSigla": sigla do plano configurado no PagSeguro,
    "planoPreco": preço da mensalidade configurada no PagSeguro,
    "clienteNome": nome do cliente,
    "clienteCPF": cpf do cliente (apenas números),
    "clienteDDD": ddd do número de telefone,
    "clientePhone": número de telefone,
    "clienteEmail": email do cliente,
    "clienteHash": hash gerado pelo senderHash,
    "enderecoRua": endereço do cliente (logradouro),
    "enderecoNumero": número da residência,
    "enderecoDistrito": nome do distrito,
    "enderecoCidade": nome da cidade,
    "enderecoEstado": nome do estado,
    "enderecoCEP": cep,
    "ccNumero": número do cartão de crédito,
    "ccCVV": código de validação,
    "ccMesExpiracao": mes de expiração,
    "ccAnoExpiracao": ano de expiração,
    "ccDiaNascimento": dia de nascimento,
}

Headers:
{
    "Authorization": "Basic <token-de-login-em-base64>"
}
```
Usuário devidamente logado, com ID de ```sessionID``` e ```senderHash``` em mãos, pode user este endpoint para realizar o registro de assinatura de um plano. O retorno é uma mensagem de sucesso com o ID da assinatura ou a descrição da falha.


### 4b. VERIFICAR ASSINATURA
---
```
[POST] /pagseguro/see-subscription

Body:
{
    "conta": (opcional) requerido apenas em caso de terem múltiplas contas configuradas
    "subscriptionCode": ID da assinatura
}

Headers:
{
    "Authorization": "Basic <token-de-login-em-base64>"
}
```

Usuário devidamente logado pode passar o ID de assintura para ter informações.

### 5. CANCELAR ASSINATURA
---
```
[POST] /pagseguro/cancel-subscription

Body:
{
    "conta": (opcional) requerido apenas em caso de terem múltiplas contas configuradas
    "subscriptionID": ID da assinatura
}

Headers:
{
    "Authorization": "Basic <token-de-login-em-base64>"
}
```

Usuário devidamente logado pode cancelar sua assinatura.