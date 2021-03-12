package com.venosyd.open.pagseguro.logic;

import java.util.HashMap;
import java.util.Map;

import com.venosyd.open.commons.http.Http;
import com.venosyd.open.commons.log.Debuggable;
import com.venosyd.open.commons.util.JSONUtil;
import com.venosyd.open.pagseguro.lib.PagSeguroUtil;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public class PagSeguroBSImpl implements PagSeguroBS, Debuggable {

    @Override
    public String token(String conta) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();
        return util.getValue("token");
    }

    @Override
    public String credential(String conta) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();
        return util.getValue("credential");
    }

    @Override
    public String createSession(String conta) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();

        var email = credential(conta);
        var token = token(conta);

        var baseURL = util.getValue("ws");
        var sessionURL = util.getValue("create-session");
        sessionURL = sessionURL.replace("{{email}}", email);
        sessionURL = sessionURL.replace("{{token}}", token);

        try {
            var response = Http.post(baseURL + sessionURL, new HashMap<>());
            var body = response.getStringBody();

            var json = JSONUtil.<String, Object>fromJSONToMap(util.fromXMLtoJSON(body));
            return (String) json.get("id");

        } catch (Exception e) {
            err.exception("PAGSEGURO CREATE-SESSION", e);
        }

        return "INVALID_SESSION_ID";
    }

    @Override
    public Map<String, Object> createPlan(String conta, String planoNome, String planoSigla,
            String planoURLCancelamento, String planoPreco, String trialPeriod) {
        var util = conta != null ? new PagSeguroUtil(conta) : new PagSeguroUtil();

        var email = credential(conta);
        var token = token(conta);

        var baseURL = util.getValue("ws");
        var createPlanURL = util.getValue("create-plan");
        createPlanURL = createPlanURL.replace("{{email}}", email);
        createPlanURL = createPlanURL.replace("{{token}}", token);

        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>            "
                + "<preApprovalRequest>                                                             "
                + "     <preApproval>                                                               "
                + "         <name>{{plan-name}}</name>                                              "
                + "         <reference>{{plan-reference}}</reference>                               "
                + "         <charge>AUTO</charge>                                                   "
                + "         <period>MONTHLY</period>                                                "
                + "         <amountPerPayment>{{plan-price}}</amountPerPayment>                     "
                + "         <cancelURL>{{plan-cancel-url}}</cancelURL>                              "
                + "         <membershipFee>0.00</membershipFee>                                     "
                + "         <trialPeriodDuration>{{trial-period}}</trialPeriodDuration>             "
                + "     </preApproval>                                                              "
                + "     <maxUses>100000</maxUses>                                                   "
                + "</preApprovalRequest>                                                            ";

        xml = xml.replace("{{plan-name}}", planoNome);
        xml = xml.replace("{{trial-period}}", trialPeriod);
        xml = xml.replace("{{plan-reference}}", planoSigla);
        xml = xml.replace("{{plan-cancel-url}}", planoURLCancelamento);

        planoPreco = String.format("%.2f", Double.parseDouble(planoPreco));
        xml = xml.replace("{{plan-price}}", planoPreco);

        try {
            var header = new HashMap<String, String>();
            header.put("Accept", "application/vnd.pagseguro.com.br.v3+json;charset=ISO-8859-1");
            header.put("Content-Type", "application/xml;charset=ISO-8859-1");

            var response = Http.postXML(baseURL + createPlanURL, xml, header);
            var body = response.getStringBody();

            return JSONUtil.<String, Object>fromJSONToMap(body);

        } catch (Exception e) {
            err.exception("PAGSEGURO CREATE-PLAN", e);
        }

        return null;
    }

}