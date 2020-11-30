package com.venosyd.open.pagseguro.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.venosyd.open.commons.util.JSONUtil;
import com.venosyd.open.commons.util.RESTService;
import com.venosyd.open.pagseguro.lib.PagSeguroConfig;
import com.venosyd.open.pagseguro.logic.CheckoutBS;
import com.venosyd.open.pagseguro.logic.CreditCardBS;
import com.venosyd.open.pagseguro.logic.PagSeguroBS;
import com.venosyd.open.pagseguro.logic.SubscriptionBS;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
@Path(PagSeguroRS.PAGSEGURO_BASE_URL)
public class PagSeguroRSImpl implements PagSeguroRS, RESTService {

    @Context
    private HttpHeaders headers;

    public PagSeguroRSImpl() {

    }

    @Override
    public Response pagseguroToken(String body) {
        return process(_unwrap(body), getauthcode(headers), null, (request) -> {
            var conta = request.get("conta");

            var response = new HashMap<String, String>();
            response.put("status", "ok");
            response.put("payload", PagSeguroBS.INSTANCE.token(conta));

            return makeResponse(response);
        });
    }

    @Override
    public Response pagseguroCredential(String body) {
        return process(_unwrap(body), getauthcode(headers), null, (request) -> {
            var conta = request.get("conta");

            var response = new HashMap<String, String>();
            response.put("status", "ok");
            response.put("payload", PagSeguroBS.INSTANCE.credential(conta));

            return makeResponse(response);
        });
    }

    @Override
    public Response createSession(String body) {
        return process(_unwrap(body), getauthcode(headers), null, (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");

            var result = PagSeguroBS.INSTANCE.createSession(conta);

            if (!result.equals("INVALID_SESSION_ID")) {
                response.put("status", "ok");
                response.put("payload", result);
            } else {
                response.put("status", "error");
                response.put("message", result);
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response createPlan(String body) {
        List<String> arguments = Arrays.asList("planoNome", "planoSigla", "planoURLCancelamento", "planoPreco");

        return process(_unwrap(body), getauthcode(headers), arguments, (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");

            var planoNome = request.get("planoNome");
            var planoSigla = request.get("planoSigla");
            var planoURLCancelamento = request.get("planoURLCancelamento");
            var planoPreco = request.get("planoPreco");

            var result = PagSeguroBS.INSTANCE.createPlan(conta, planoNome, planoSigla, planoURLCancelamento,
                    planoPreco);

            if (result != null && !result.containsKey("error")) {
                response.put("status", "ok");
                response.put("payload", JSONUtil.toJSON(result));
            } else {
                response.put("status", "error");
                response.put("message", JSONUtil.toJSON(result));
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response ccBrand(String body) {
        return process(_unwrap(body), getauthcode(headers), Arrays.asList("sessionID", "ccBin"), (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");

            var sessionID = request.get("sessionID");
            var ccBin = request.get("ccBin");

            var result = CreditCardBS.INSTANCE.getCCBrand(conta, sessionID, ccBin);

            if (!result.equals("INVALID_CARD_BRAND")) {
                response.put("status", "ok");
                response.put("payload", result);
            } else {
                response.put("status", "error");
                response.put("message", result);
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response ccToken(String body) {
        List<String> arguments = Arrays.asList("sessionID", "amount", "ccNumero", "ccCVV", "ccMesExpiracao",
                "ccAnoExpiracao");

        return process(_unwrap(body), getauthcode(headers), arguments, (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");

            var sessionID = request.get("sessionID");
            var amount = String.format("%.2f", Double.parseDouble(request.get("amount")));
            var ccNumero = request.get("ccNumero");
            var ccCVV = request.get("ccCVV");
            var ccMesExpiracao = request.get("ccMesExpiracao");
            var ccAnoExpiracao = request.get("ccAnoExpiracao");

            var result = CreditCardBS.INSTANCE.getCCToken(conta, sessionID, amount, ccNumero, ccCVV, ccMesExpiracao,
                    ccAnoExpiracao);

            if (!result.equals("INVALID_CARD_TOKEN")) {
                response.put("status", "ok");
                response.put("payload", result);
            } else {
                response.put("status", "error");
                response.put("message", result);
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response getParcelas(String body) {
        List<String> arguments = Arrays.asList("sessionID", "amount", "ccBrand");

        return process(_unwrap(body), getauthcode(headers), arguments, (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");

            var sessionID = request.get("sessionID");
            var amount = String.format("%.2f", Double.parseDouble(request.get("amount")));
            var ccBrand = request.get("ccBrand");

            var result = CreditCardBS.INSTANCE.getParcelas(conta, sessionID, amount, ccBrand);

            if (result != null && result.get("error").equals("false")) {
                response.put("status", "ok");
                response.put("payload", JSONUtil.toJSON(result));
            } else {
                response.put("status", "error");
                response.put("message", JSONUtil.toJSON(result));
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response doCheckout(String body) {
        List<String> arguments = Arrays.asList("sessionID", "itemDescricao", "itemSigla", "clienteNome", "clienteCPF",
                "clienteDDD", "clientePhone", "clienteEmail", "clienteHash", "amount", "parcelas", "ccNumero", "ccCVV",
                "ccMesExpiracao", "ccAnoExpiracao", "ccDiaNascimento");

        return process(_unwrap(body), getauthcode(headers), arguments, (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");

            var sessionID = request.get("sessionID");
            var itemDescricao = request.get("itemDescricao");
            var itemSigla = request.get("itemSigla");
            var clienteNome = request.get("clienteNome");
            var clienteCPF = request.get("clienteCPF");
            var clienteDDD = request.get("clienteDDD");
            var clientePhone = request.get("clientePhone");
            var clienteEmail = request.get("clienteEmail");
            var clienteHash = request.get("clienteHash");
            var amount = String.format("%.2f", Double.parseDouble(request.get("amount")));
            var parcelas = request.get("parcelas");
            var ccNumero = request.get("ccNumero");
            var ccCVV = request.get("ccCVV");
            var ccMesExpiracao = request.get("ccMesExpiracao");
            var ccAnoExpiracao = request.get("ccAnoExpiracao");
            var ccDiaNascimento = request.get("ccDiaNascimento");

            var config = conta != null ? new PagSeguroConfig(conta) : new PagSeguroConfig();
            String database = (String) config.get("bancodedados");

            var result = CheckoutBS.INSTANCE.doCheckout(conta, sessionID, itemDescricao, itemSigla, clienteNome,
                    clienteCPF, clienteDDD, clientePhone, clienteEmail, clienteHash, amount, ccNumero, ccCVV,
                    ccMesExpiracao, ccAnoExpiracao, ccDiaNascimento, parcelas, database);

            if (result != null && !result.containsKey("error")) {
                response.put("status", "ok");
                response.put("payload", JSONUtil.toJSON(result));
            } else {
                response.put("status", "error");
                response.put("message", JSONUtil.toJSON(result));
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response seeCheckout(String body) {
        return process(_unwrap(body), getauthcode(headers), Arrays.asList("transactionID"), (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");
            var transactionID = request.get("transactionID");

            var result = CheckoutBS.INSTANCE.seeCheckout(conta, transactionID);

            if (result != null && !result.containsKey("error")) {
                response.put("status", "ok");
                response.put("payload", JSONUtil.toJSON(result));
            } else {
                response.put("status", "error");
                response.put("message", JSONUtil.toJSON(result));
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response doSubscription(String body) {
        var arguments = Arrays.asList("sessionID", "planoID", "planoSigla", "planoPreco", "clienteNome", "clienteCPF",
                "clienteDDD", "clientePhone", "clienteEmail", "clienteHash", "enderecoRua", "enderecoDistrito",
                "enderecoCidade", "enderecoEstado", "enderecoCEP", "ccNumero", "ccCVV", "ccMesExpiracao",
                "ccAnoExpiracao", "ccDiaNascimento");

        return process(_unwrap(body), getauthcode(headers), arguments, (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");

            var sessionID = request.get("sessionID");
            var clienteNome = request.get("clienteNome");
            var clienteCPF = request.get("clienteCPF");
            var clienteDDD = request.get("clienteDDD");
            var clientePhone = request.get("clientePhone");
            var clienteEmail = request.get("clienteEmail");
            var clienteHash = request.get("clienteHash");
            var planoID = request.get("planoID");
            var planoPreco = request.get("planoPreco");
            var planoSigla = request.get("planoSigla");
            var enderecoRua = request.get("enderecoRua");
            var enderecoNumero = request.get("enderecoNumero");
            var enderecoDistrito = request.get("enderecoDistrito");
            var enderecoCidade = request.get("enderecoCidade");
            var enderecoEstado = request.get("enderecoEstado");
            var enderecoCEP = request.get("enderecoCEP");
            var ccNumero = request.get("ccNumero");
            var ccCVV = request.get("ccCVV");
            var ccMesExpiracao = request.get("ccMesExpiracao");
            var ccAnoExpiracao = request.get("ccAnoExpiracao");
            var ccDiaNascimento = request.get("ccDiaNascimento");

            var config = conta != null ? new PagSeguroConfig(conta) : new PagSeguroConfig();
            String database = (String) config.get("bancodedados");

            var result = SubscriptionBS.INSTANCE.doSubcription(conta, sessionID, planoID, planoSigla, planoPreco,
                    clienteNome, clienteCPF, clienteDDD, clientePhone, clienteEmail, clienteHash, enderecoRua,
                    enderecoNumero, enderecoDistrito, enderecoCidade, enderecoEstado, enderecoCEP, ccNumero, ccCVV,
                    ccMesExpiracao, ccAnoExpiracao, ccDiaNascimento, database);

            if (result != null && !result.containsKey("error")) {
                response.put("status", "ok");
                response.put("payload", JSONUtil.toJSON(result));
            } else {
                response.put("status", "error");
                response.put("message", JSONUtil.toJSON(result));
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response seeSubscription(String body) {
        return process(_unwrap(body), getauthcode(headers), Arrays.asList("subscriptionID"), (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");
            var subscriptionID = request.get("subscriptionID");

            var result = SubscriptionBS.INSTANCE.seeSubscription(conta, subscriptionID);

            if (result != null && !result.containsKey("error")) {
                response.put("status", "ok");
                response.put("payload", JSONUtil.toJSON(result));
            } else {
                response.put("status", "error");
                response.put("message", JSONUtil.toJSON(result));
            }

            return makeResponse(response);
        });
    }

    @Override
    public Response cancelSubscription(String body) {
        return process(_unwrap(body), getauthcode(headers), Arrays.asList("subscriptionID"), (request) -> {
            var response = new HashMap<String, String>();
            var conta = request.get("conta");
            var subscriptionID = request.get("subscriptionID");

            var result = SubscriptionBS.INSTANCE.cancelSubscription(conta, subscriptionID);

            if (result != null && !result.containsKey("error")) {
                response.put("status", "ok");
                response.put("payload", JSONUtil.toJSON(result));
            } else {
                response.put("status", "error");
                response.put("message", JSONUtil.toJSON(result));
            }

            return makeResponse(response);
        });
    }

    /**
     * traduz o JSON pra mapa e insere um dado importante que eh a base de dados
     * para o servidor saber direcionar o fluxo de persistencia e consulta
     * corretamente
     */
    private Map<String, String> _unwrap(String body) {
        body = unzip(body);
        var request = JSONUtil.<String, String>fromJSONToMap(body);
        var conta = request.get("conta");
        var config = conta != null ? new PagSeguroConfig(conta) : new PagSeguroConfig();

        String database = (String) config.get("bancodedados");
        request.put("database", database);

        return request;
    }

}
