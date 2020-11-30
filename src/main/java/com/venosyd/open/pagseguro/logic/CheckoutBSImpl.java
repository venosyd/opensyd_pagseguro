package com.venosyd.open.pagseguro.logic;

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
public class CheckoutBSImpl implements CheckoutBS, Debuggable {

    @Override
    public Map<String, Object> doCheckout(String conta, String sessionID, String itemDescricao, String itemSigla,
            String clienteNome, String clienteCPF, String clienteDDD, String clientePhone, String clienteEmail,
            String clienteHash, String amount, String ccNumero, String ccCVV, String ccMesExpiracao,
            String ccAnoExpiracao, String ccDiaNascimento, String parcelas, String database) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();

        var ccToken = CreditCardBS.INSTANCE.getCCToken(conta, sessionID, amount, ccNumero, ccCVV, ccMesExpiracao,
                ccAnoExpiracao);
        var ccBrand = CreditCardBS.INSTANCE.getCCBrand(conta, sessionID, ccNumero.substring(0, 6));

        var baseURL = util.getValue("ws");
        var doCheckoutURL = util.getValue("do-checkout");
        doCheckoutURL = doCheckoutURL.replace("{{email}}", PagSeguroBS.INSTANCE.credential(conta));
        doCheckoutURL = doCheckoutURL.replace("{{token}}", PagSeguroBS.INSTANCE.token(conta));

        var parcela = util.getParcelamento(sessionID, amount, ccBrand, Integer.parseInt(parcelas));
        var repo = new Repository(database);

        try {
            var form = new HashMap<String, Object>();

            // checkout payment data
            form.put("paymentMode", "default");
            form.put("paymentMethod", "CREDIT_CARD");
            form.put("currency", "BRL");
            form.put("itemId1", "0001");
            form.put("itemDescription1", itemDescricao);
            form.put("itemAmount1", amount);
            form.put("itemQuantity1", "1");
            form.put("notificationURL", util.getValue("redirection-url"));
            form.put("reference", itemSigla);

            // sender data
            form.put("senderName", clienteNome);
            form.put("senderCPF", clienteCPF);
            form.put("senderAreaCode", clienteDDD);
            form.put("senderPhone", clientePhone);
            form.put("senderEmail", clienteEmail);
            form.put("senderHash", clienteHash);

            form.put("shippingAddressRequired", "false");

            // user creditcard
            form.put("creditCardToken", ccToken);
            form.put("installmentQuantity", parcelas);
            form.put("installmentValue",
                    String.format("%.2f", Double.parseDouble(parcela.get("installmentAmount") + "")));
            form.put("creditCardHolderName", clienteNome);
            form.put("creditCardHolderCPF", clienteCPF);
            form.put("creditCardHolderBirthDate", ccDiaNascimento);
            form.put("creditCardHolderAreaCode", clienteDDD);
            form.put("creditCardHolderPhone", clientePhone);

            // endereco da empresa
            form.put("billingAddressStreet", util.getValue("addr-rua"));
            form.put("billingAddressNumber", util.getValue("addr-numero"));
            form.put("billingAddressDistrict", util.getValue("addr-bairro"));
            form.put("billingAddressPostalCode", util.getValue("addr-cep"));
            form.put("billingAddressCity", util.getValue("addr-cidade"));
            form.put("billingAddressState", util.getValue("addr-estado"));
            form.put("billingAddressCountry", "BRA");

            var response = Http.postForm(baseURL + doCheckoutURL, form, new HashMap<>());
            var body = response.getStringBody();

            var result = JSONUtil.<String, Object>fromJSONToMap(util.fromXMLtoJSON(body));

            if (!result.containsKey("error")) {
                var transaction = new Transaction();
                transaction.setType("Checkout");
                transaction.setCode((String) result.get("code"));
                transaction.setMetadata(result);

                repo.save(transaction);
            }

            return result;

        } catch (Exception e) {
            err.exception("PAGSEGURO DO-CHECKOUT", e);
        }

        return null;
    }

    @Override
    public Map<String, Object> seeCheckout(String conta, String checkoutCode) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();
        
        var email = PagSeguroBS.INSTANCE.credential(conta);
        var token = PagSeguroBS.INSTANCE.token(conta);

        var baseURL = util.getValue("ws");
        var seeCheckoutURL = util.getValue("see-checkout");
        seeCheckoutURL = seeCheckoutURL.replace("{{transactionID}}", checkoutCode);
        seeCheckoutURL = seeCheckoutURL.replace("{{email}}", email);
        seeCheckoutURL = seeCheckoutURL.replace("{{token}}", token);

        try {
            var response = Http.get(baseURL + seeCheckoutURL);
            var body = response.getStringBody();

            return JSONUtil.fromJSONToMap(util.fromXMLtoJSON(body));

        } catch (Exception e) {
            err.exception("PAGSEGURO SEE-CHECKOUT", e);
        }

        return null;
    }

}