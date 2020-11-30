package com.venosyd.open.pagseguro.logic;

import java.util.Map;

/**
 * @author sergio lisan <sels@venosyd.com>
 */
public interface CheckoutBS {

        /**  */
        CheckoutBS INSTANCE = new CheckoutBSImpl();

        /**
         * Aderir ao plano de beneficios com base na deducao do imposto de renda
         */
        Map<String, Object> doCheckout(String conta, String sessionID, String itemDescricao, String itemSigla,
                        String clienteNome, String clienteCPF, String clienteDDD, String clientePhone,
                        String clienteEmail, String clienteHash, String amount, String ccNumero, String ccCVV,
                        String ccMesExpiracao, String ccAnoExpiracao, String ccDiaNascimento, String parcelas,
                        String database);

        /**  */
        Map<String, Object> seeCheckout(String conta, String checkoutCode);

}