package com.venosyd.open.pagseguro.lib;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.venosyd.open.pagseguro.logic.CreditCardBS;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public class PagSeguroUtil {

    /** */
    private PagSeguroConfig config;

    /** */
    private String conta;

    /** */
    public PagSeguroUtil() {
        conta = "pagseguro";
        config = new PagSeguroConfig(conta);
    }

    /** */
    public PagSeguroUtil(String file) {
        conta = file;
        config = new PagSeguroConfig(conta);
    }

    /**
     * retorna o valor de uma chave de configuracao do pagseguro no config.yaml
     */
    public String getValue(String key) {
        return (String) config.get(key);
    }

    /**
     * transforma uma resposta xml em json
     */
    public String fromXMLtoJSON(String xml) throws Exception {
        var xmlMapper = new XmlMapper();
        var node = xmlMapper.readTree(xml.getBytes());

        var jsonMapper = new ObjectMapper();
        return jsonMapper.writeValueAsString(node);
    }

    /**
     * dada sessionID, quantidade inicial, bandeira do cartao e parcelas, retorna
     * uma relacao com o valor parcela e juros calculados
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> getParcelamento(String sessionID, String amount, String ccBrand, int parcelas) {
        var parcels = (Map) CreditCardBS.INSTANCE.getParcelas(conta, sessionID, amount, ccBrand);
        var installments = (Map) parcels.get("installments");
        var parcelamento = (List<Map<String, Object>>) installments.get(ccBrand);

        return parcelamento.get(parcelas - 1);
    }
}