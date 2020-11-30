package com.venosyd.open.pagseguro.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.venosyd.open.commons.http.Http;
import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.services.interfaces.Repository;
import com.venosyd.open.commons.util.JSONUtil;
import com.venosyd.open.pagseguro.lib.PagSeguroUtil;
import com.venosyd.open.pagseguro.lib.entities.Transaction;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public class SubscriptionBSImpl implements SubscriptionBS, Debuggable {

    @Override
    public Map<String, Object> doSubcription(String conta, String sessionID, String planoID, String planoSigla,
            String planoPreco, String clienteNome, String clienteCPF, String clienteDDD, String clientePhone,
            String clienteEmail, String clienteHash, String enderecoRua, String enderecoNumero, String enderecoDistrito,
            String enderecoCidade, String enderecoEstado, String enderecoCEP, String ccNumero, String ccCVV,
            String ccMesExpiracao, String ccAnoExpiracao, String ccDiaNascimento, String database) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();

        var ccToken = CreditCardBS.INSTANCE.getCCToken(conta, sessionID, planoPreco, ccNumero, ccCVV, ccMesExpiracao,
                ccAnoExpiracao);

        var baseURL = util.getValue("ws");
        var doSubscriptionURL = util.getValue("do-subscription");
        doSubscriptionURL = doSubscriptionURL.replace("{{email}}", PagSeguroBS.INSTANCE.credential(conta));
        doSubscriptionURL = doSubscriptionURL.replace("{{token}}", PagSeguroBS.INSTANCE.token(conta));

        var repo = new Repository(database);

        try {
            var form = new HashMap<String, Object>();
            form.put("plan", planoID);
            form.put("reference", planoSigla);

            var senderForm = new HashMap<String, Object>();
            senderForm.put("name", clienteNome);
            senderForm.put("email", clienteEmail);
            senderForm.put("hash", clienteHash);

            // sender
            var senderPhone = new HashMap<String, Object>();
            senderPhone.put("areaCode", clienteDDD);
            senderPhone.put("number", clientePhone);

            senderForm.put("phone", senderPhone);

            var senderAddress = new HashMap<String, Object>();
            senderAddress.put("street", enderecoRua);
            senderAddress.put("number", enderecoNumero);
            senderAddress.put("complement", "");
            senderAddress.put("district", enderecoDistrito);
            senderAddress.put("city", enderecoCidade);
            senderAddress.put("state", enderecoEstado);
            senderAddress.put("country", "BRA");
            senderAddress.put("postalCode", enderecoCEP);

            senderForm.put("address", senderAddress);

            var docs = new ArrayList<Map<String, Object>>();
            var docsForm = new HashMap<String, Object>();
            docsForm.put("type", "CPF");
            docsForm.put("value", clienteCPF);
            docs.add(docsForm);
            senderForm.put("documents", docs);

            form.put("sender", senderForm);

            // /-sender

            // payment
            var paymentForm = new HashMap<String, Object>();
            paymentForm.put("type", "CREDITCARD");

            var ccForm = new HashMap<String, Object>();
            ccForm.put("token", ccToken);

            var ccHolder = new HashMap<String, Object>();
            ccHolder.put("name", clienteNome);
            ccHolder.put("birthDate", ccDiaNascimento);
            ccHolder.put("documents", docs);
            ccHolder.put("phone", senderPhone);
            ccHolder.put("billingAddress", senderAddress);

            ccForm.put("holder", ccHolder);
            paymentForm.put("creditCard", ccForm);

            form.put("paymentMethod", paymentForm);

            // /-payment

            var header = new HashMap<String, String>();
            header.put("Accept", "application/vnd.pagseguro.com.br.v1+json;charset=ISO-8859-1");

            var response = Http.post(baseURL + doSubscriptionURL, form, header);
            var result = JSONUtil.<String, Object>fromJSONToMap(response.getStringBody());

            if (!result.containsKey("error")) {
                var transaction = new Transaction();
                transaction.setType("Subscription");
                transaction.setCode((String) result.get("code"));
                transaction.setMetadata(result);

                repo.save(transaction);
            }

            return result;

        } catch (Exception e) {
            err.exception("PAGSEGURO DO-SUBSCRIPTION", e);
        }

        return null;
    }

    @Override
    public Map<String, Object> seeSubscription(String conta, String subscriptionCode) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();

        var email = PagSeguroBS.INSTANCE.credential(conta);
        var token = PagSeguroBS.INSTANCE.token(conta);

        var baseURL = util.getValue("ws");
        var seeSubscriptionURL = util.getValue("see-subscription");
        seeSubscriptionURL = seeSubscriptionURL.replace("{{subscriptionID}}", subscriptionCode);
        seeSubscriptionURL = seeSubscriptionURL.replace("{{email}}", email);
        seeSubscriptionURL = seeSubscriptionURL.replace("{{token}}", token);

        try {
            var header = new HashMap<String, String>();
            header.put("Accept", "application/vnd.pagseguro.com.br.v3+json;charset=ISO-8859-1");

            var response = Http.get(baseURL + seeSubscriptionURL, header);
            return JSONUtil.fromJSONToMap(response.getStringBody());

        } catch (Exception e) {
            err.exception("PAGSEGURO SEE-SUBSCRIPTION", e);
        }

        return null;
    }

    @Override
    public Map<String, Object> cancelSubscription(String conta, String subscriptionCode) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();

        var email = PagSeguroBS.INSTANCE.credential(conta);
        var token = PagSeguroBS.INSTANCE.token(conta);

        var baseURL = util.getValue("ws");
        var cancelSubscriptionURL = util.getValue("cancel-subscription");
        cancelSubscriptionURL = cancelSubscriptionURL.replace("{{subscriptionID}}", subscriptionCode);
        cancelSubscriptionURL = cancelSubscriptionURL.replace("{{email}}", email);
        cancelSubscriptionURL = cancelSubscriptionURL.replace("{{token}}", token);

        try {
            var header = new HashMap<String, String>();
            header.put("Accept", "application/vnd.pagseguro.com.br.v3+json;charset=ISO-8859-1");

            var response = Http.put(baseURL + cancelSubscriptionURL, header);
            return JSONUtil.fromJSONToMap(response.getStringBody());

        } catch (Exception e) {
            err.exception("PAGSEGURO CANCEL-SUBSCRIPTION", e);
        }

        return null;
    }

}